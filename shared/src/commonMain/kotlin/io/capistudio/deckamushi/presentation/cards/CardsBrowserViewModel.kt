package io.capistudio.deckamushi.presentation.cards

import io.capistudio.deckamushi.domain.model.Card
import io.capistudio.deckamushi.domain.usecase.GetCardsCountUseCase
import io.capistudio.deckamushi.domain.usecase.GetCardsFoundByNameCountUseCase
import io.capistudio.deckamushi.domain.usecase.GetCardsPageUseCase
import io.capistudio.deckamushi.domain.usecase.SearchCardByNameUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class CardBrowserState(
    val isLoading: Boolean = false,
    val totalCount: Long? = null,
    val cards: List<Card> = emptyList(),
    val error: String? = null,
    val query: String = "",
)


class CardsBrowserViewModel(
    private val getCardsPageUseCase: GetCardsPageUseCase,
    private val getCardsCountUseCase: GetCardsCountUseCase,
    private val searchCardByNameUseCase: SearchCardByNameUseCase,
    private val getCardsFoundByNameCountUseCase: GetCardsFoundByNameCountUseCase,
    private val scope: CoroutineScope = MainScope(),
) {
    private val pageSize = 50
    private var offset = 0
    private var reachedEnd = false
    private var loading = false

    private val _state = MutableStateFlow(CardBrowserState())
    val state: StateFlow<CardBrowserState> = _state

    private var searchJob: Job? = null

    fun loadInitial() {
        refresh()
    }

    fun loadMore() {
        if (loading || reachedEnd) return
        loading = true
        scope.launch {
            try {
                val q = _state.value.query.trim()
                val next = if (q.isBlank()) {
                    getCardsPageUseCase(limit = pageSize, offset = offset)
                } else {
                    searchCardByNameUseCase(q, pageSize, offset)
                }
                offset += next.size
                if (next.isEmpty()) reachedEnd = true

                _state.update { s ->
                    s.copy(
                        cards = s.cards + next,
                        error = null,
                    )
                }
            } catch (t: Throwable) {
                _state.update { it.copy(error = t.message ?: "Unknown error") }
            } finally {
                loading = false
            }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _state.update { it.copy(query = newQuery) }
        searchJob?.cancel()
        searchJob = scope.launch {
            delay(300)
            refresh()
        }
    }

    fun refresh() {
        if (loading) return

        loading = true
        scope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val q = _state.value.query.trim()
            offset = 0
            reachedEnd = false

            try {
                val count = if (q.isBlank()) {
                    getCardsCountUseCase()
                } else {
                    getCardsFoundByNameCountUseCase(q)
                }
                val first = if (q.isBlank()) {
                    getCardsPageUseCase(pageSize, offset)
                } else {
                    searchCardByNameUseCase(q, pageSize, offset)
                }
                offset += first.size
                reachedEnd = first.isEmpty()
                _state.update {
                    it.copy(
                        isLoading = false,
                        totalCount = count,
                        cards = first
                    )
                }
            } catch (t: Throwable) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = t.message
                    )
                }
            } finally {
                loading = false
            }
        }
    }

    fun clear() {
        scope.cancel()
    }
}