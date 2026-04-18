package io.capistudio.deckamushi.presentation.cards

import co.touchlab.kermit.Logger
import io.capistudio.deckamushi.domain.usecase.GetCardsCountUseCase
import io.capistudio.deckamushi.domain.usecase.GetCardsFoundByNameCountUseCase
import io.capistudio.deckamushi.domain.usecase.GetCardsPageUseCase
import io.capistudio.deckamushi.domain.usecase.SearchCardByNameUseCase
import io.capistudio.deckamushi.presentation.cards.CardsBrowserContract.Action
import io.capistudio.deckamushi.presentation.cards.CardsBrowserContract.Effect
import io.capistudio.deckamushi.presentation.cards.CardsBrowserContract.State
import io.capistudio.deckamushi.presentation.mvi.Mvi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class CardsBrowserViewModel(
    private val getCardsPageUseCase: GetCardsPageUseCase,
    private val getCardsCountUseCase: GetCardsCountUseCase,
    private val searchCardByNameUseCase: SearchCardByNameUseCase,
    private val getCardsFoundByNameCountUseCase: GetCardsFoundByNameCountUseCase,
    scope: CoroutineScope = MainScope(),
) : Mvi<State, Action, Effect>(
    initialState = State(),
    scope = scope
) {
    private val log = Logger.withTag("CardsBrowserVM")

    private val pageSize = 50
    private var offset = 0
    private var loadingJob: Job? = null

    override suspend fun handleAction(action: Action) {
        when (action) {
            Action.OnStart -> refreshAllCards()
            is Action.QueryChanged -> {
                setState { copy(queryDraft = action.value) }
            }

            Action.SearchClicked -> refreshSearchOrAll()
            Action.LoadMore -> loadMore()
            is Action.CardClicked -> emitEffect(Effect.NavigateToDetail(action.id))
        }
    }

    private suspend fun refreshSearchOrAll() {
        val query = state.value.queryDraft.trim()
        if (query.isBlank()) refreshAllCards() else refreshSearch(query)
    }

    private suspend fun refreshAllCards() {
        if (loadingJob?.isActive == true) return

        offset = 0
        setState {
            copy(
                isSearching = true,
                isAppending = false,
                endReached = false,
                error = null
            )
        }

        loadingJob = scope.launch {
            runCatching {
                val page = getCardsPageUseCase(pageSize, offset)
                val count = getCardsCountUseCase()

                val endReachedNow = page.size < pageSize
                offset += page.size

                setState {
                    copy(
                        isSearching = false,
                        cards = page,
                        totalCount = count,
                        endReached = endReachedNow,
                        error = null
                    )
                }
            }.onFailure { e ->
                log.e(e) { "refreshAllCards failed" }
                setState {
                    copy(isSearching = false, error = e.message ?: "Unknow error")
                }
            }
        }
    }

    private suspend fun refreshSearch(query: String) {
        if (loadingJob?.isActive == true) return

        offset = 0
        setState {
            copy(
                isSearching = true,
                isAppending = false,
                endReached = false,
                error = null,
            )
        }

        loadingJob = scope.launch {
            runCatching {
                val page = searchCardByNameUseCase(query = query, limit = pageSize, offset = offset)
                val count = getCardsFoundByNameCountUseCase(query = query)

                val endReachedNow = page.size < pageSize
                offset += page.size

                setState {
                    copy(
                        isSearching = false,
                        cards = page,
                        totalCount = count,
                        endReached = endReachedNow,
                        error = null,
                    )
                }
            }.onFailure { e ->
                log.e(e) { "refreshSearch failed" }
                setState { copy(isSearching = false, error = e.message ?: "Unknown error") }
            }
        }
    }

    private suspend fun loadMore() {
        val s = state.value

        if (s.isSearching || s.isAppending || s.endReached) return
        if (s.cards.isEmpty()) return
        if (loadingJob?.isActive == true) return

        setState { copy(isAppending = true, error = null) }

        val query = s.queryDraft.trim()

        loadingJob = scope.launch {
            runCatching {
                val page = if (query.isBlank()) {
                    getCardsPageUseCase(limit = pageSize, offset = offset)
                } else {
                    searchCardByNameUseCase(query, pageSize, offset)
                }

                val endReachedNow = page.size < pageSize
                offset += page.size

                setState {
                    copy(
                        isAppending = false,
                        cards = cards + page,
                        endReached = endReachedNow,
                        error = null
                    )
                }
            }.onFailure { e ->
                log.e(e) { "loadMore failed" }
                setState {
                    copy(isAppending = false, error = e.message ?: "Unknow error")
                }
            }
        }
    }

    fun clear() {
        scope.cancel()
    }
}