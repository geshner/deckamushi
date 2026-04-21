package io.capistudio.deckamushi.presentation.collection

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.cachedIn
import co.touchlab.kermit.Logger
import io.capistudio.deckamushi.domain.usecase.GetOwnedCardsUseCase
import io.capistudio.deckamushi.domain.usecase.GetOwnedQuantityUseCase
import io.capistudio.deckamushi.domain.usecase.GetOwnedTotalUseCase
import io.capistudio.deckamushi.presentation.collection.CollectionContract.Action
import io.capistudio.deckamushi.presentation.collection.CollectionContract.Effect
import io.capistudio.deckamushi.presentation.collection.CollectionContract.Effect.*
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

    val ownedCardsPagingData = getOwnedCardsUseCase()
        .cachedIn(viewModelScope)

    override suspend fun handleAction(action: Action) {
        when (action) {
            Action.OnStart -> updateTotalCount()
            is Action.CardClicked -> emitEffect(NavigateToDetail(action.id))
            is Action.ScrollPositionChanged -> setState {
                copy(
                    scrollIndex = action.index,
                    scrollOffset = action.offset
                )
            }
        }
    }

    private fun updateTotalCount() {
        viewModelScope.launch {
            runCatching {
                val count = getOwnedTotalUseCase()
                setState { copy(totalCount = count) }
            }.onFailure { e ->
                log.e(e) { "updateTotalCount failed" }
            }
        }
    }
}