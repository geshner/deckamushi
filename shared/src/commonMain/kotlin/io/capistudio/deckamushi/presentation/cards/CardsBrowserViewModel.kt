package io.capistudio.deckamushi.presentation.cards

import io.capistudio.deckamushi.domain.model.Card
import io.capistudio.deckamushi.domain.usecase.GetCardsCountUseCase
import io.capistudio.deckamushi.domain.usecase.GetCardsPageUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class CardBrowserState(
    val isLoading: Boolean = false,
    val totalCount: Long? = null,
    val cards: List<Card> = emptyList(),
    val error: String? = null,
)


class CardsBrowserViewModel(
    private val getCardsPageUseCase: GetCardsPageUseCase,
    private val getCardsCountUseCase: GetCardsCountUseCase,
    private val scope: CoroutineScope = MainScope(),
) {
    private val pageSize = 50
    private var offset = 0
    private var reachedEnd = false
    private var loading = false

    private val _state = MutableStateFlow(CardBrowserState())
    val state: StateFlow<CardBrowserState> = _state

    fun loadInitial() {
        if (_state.value.cards.isNotEmpty()) return

        scope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val count = getCardsCountUseCase()
                offset = 0
                reachedEnd = false
                val first = getCardsPageUseCase(pageSize, offset)
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
            }
        }
    }

    fun loadMore() {
        if (loading || reachedEnd) return
        loading = true
        scope.launch {
            try {
                val next = getCardsPageUseCase(limit = pageSize, offset = offset)
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

    fun clear() {
        scope.cancel()
    }
}