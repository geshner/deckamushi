package io.capistudio.deckamushi.presentation.detail

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import io.capistudio.deckamushi.domain.usecase.DecrementOwnedUseCase
import io.capistudio.deckamushi.domain.usecase.GetCardByIdUseCase
import io.capistudio.deckamushi.domain.usecase.GetOwnedQuantityUseCase
import io.capistudio.deckamushi.domain.usecase.IncrementOwnedUseCase
import io.capistudio.deckamushi.domain.util.onFailure
import io.capistudio.deckamushi.domain.util.onSuccess
import io.capistudio.deckamushi.presentation.detail.CardDetailContract.Effect
import io.capistudio.deckamushi.presentation.mvi.Mvi
import kotlinx.coroutines.launch

class CardDetailViewModel(
    private val cardId: String,
    private val getCardByIdUseCase: GetCardByIdUseCase,
    private val getOwnedQuantityUseCase: GetOwnedQuantityUseCase,
    private val incrementOwnedUseCase: IncrementOwnedUseCase,
    private val decrementOwnedUseCase: DecrementOwnedUseCase,
) : Mvi<CardDetailContract.State, CardDetailContract.Action, Effect>(
    initialState = CardDetailContract.State(),
) {

    private val log = Logger.withTag("CardDetailVM")

    override suspend fun handleAction(action: CardDetailContract.Action) {
        when (action) {
            CardDetailContract.Action.BackClicked -> emitEffect(Effect.NavigateBack)
            CardDetailContract.Action.OnStart -> loadCardData()
            CardDetailContract.Action.DecrementOwnedClick -> {
                decrementOwnedCount()
                loadOwned()
            }

            CardDetailContract.Action.IncrementOwnedClick -> {
                incrementOwnedCount()
                loadOwned()
            }
        }
    }

    private suspend fun loadCardData() {

        if (state.value.isLoading) return

        setState { copy(isLoading = true, error = null) }

        viewModelScope.launch {
            loadCardDetail()
            loadOwned()
        }
    }

    private suspend fun loadCardDetail() {
        getCardByIdUseCase(cardId)
            .onSuccess { card -> setState { copy(isLoading = false, card = card, error = null) } }
            .onFailure { msg, e ->
                log.e(e) { "Load card failed (id=$cardId)" }
                emitEffect(Effect.ShowMessage(msg))
                setState { copy(isLoading = false, card = null, error = msg) }
            }
    }

    private suspend fun loadOwned() {
        getOwnedQuantityUseCase(cardId)
            .onSuccess {
                setState { copy(ownedQuantity = it) }
            }
    }

    private suspend fun incrementOwnedCount() {
        incrementOwnedUseCase(cardId)
            .onFailure { msg, e ->
                log.e(e) { "failed to increment (id=$cardId)" }
                emitEffect(Effect.ShowMessage(msg))
                setState { copy(isLoading = false, error = msg) }
            }
    }

    private suspend fun decrementOwnedCount() {
        decrementOwnedUseCase(cardId)
            .onFailure { msg, e ->
                log.e(e) { "failed to decrement (id=$cardId)" }
                emitEffect(Effect.ShowMessage(msg))
                setState { copy(isLoading = false, error = msg) }
            }
    }
}
