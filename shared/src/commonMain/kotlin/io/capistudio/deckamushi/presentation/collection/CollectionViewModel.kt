package io.capistudio.deckamushi.presentation.collection

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import co.touchlab.kermit.Logger
import io.capistudio.deckamushi.domain.usecase.GetOwnedCardsUseCase
import io.capistudio.deckamushi.domain.usecase.GetOwnedQuantityUseCase
import io.capistudio.deckamushi.domain.usecase.GetOwnedTotalUseCase
import io.capistudio.deckamushi.presentation.collection.CollectionContract.Action
import io.capistudio.deckamushi.presentation.collection.CollectionContract.Effect
import io.capistudio.deckamushi.presentation.collection.CollectionContract.State
import io.capistudio.deckamushi.presentation.mvi.Mvi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class CollectionViewModel(
    private val getOwnedCardsUseCase: GetOwnedCardsUseCase,
    private val getOwnedTotalUseCase: GetOwnedTotalUseCase,
) : Mvi<State, Action, Effect>(
    initialState = State(),
) {
    private val log = Logger.withTag("CollectionVM")

    private val pageSize =50
    private var offset =0

    private var loadingJob: Job? = null

    override suspend fun handleAction(action: Action) {
        when (action) {
            Action.OnStart -> {
                if (state.value.cards.isEmpty()) {
                    loadOwned()
                }
            }
            Action.LoadMore -> loadMore()
            is Action.CardClicked -> emitEffect(Effect.NavigateToDetail(action.id))
        }
    }

    private suspend fun loadOwned() {
        if (loadingJob?.isActive == true) return

        offset = 0
        setState { copy(
            isAppending = false,
            endReached = false,
            error = null
        ) }

        loadingJob = viewModelScope.launch {
            runCatching {
                val page = getOwnedCardsUseCase(pageSize, offset)
                val count = getOwnedTotalUseCase()

                val endReachedNow = page.size < pageSize
                offset += page.size


                setState { copy(
                    cards = page,
                    totalCount = count,
                    endReached = endReachedNow,
                    error = null,
                )}
            }.onFailure { e ->
                log.e(e) { "loadOwned failed" }
                setState {
                    copy(error = e.message ?: "Unknow error")
                }
            }
        }
    }

    private suspend fun loadMore() {
        val s = state.value

        if (s.isAppending || s.endReached) return
        if (s.cards.isEmpty()) return
        if (loadingJob?.isActive == true) return

        setState { copy(isAppending = true, error = null) }

        loadingJob = viewModelScope.launch {
            runCatching {
                val page = getOwnedCardsUseCase(pageSize, offset)


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
}