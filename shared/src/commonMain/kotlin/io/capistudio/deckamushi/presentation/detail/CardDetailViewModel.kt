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

/**
 * Drives the card detail screen and owned-quantity mutations.
 *
 * `fromScan` is not just navigation metadata: it changes back behavior. When detail was opened
 * from the scanner flow and the user changes owned quantity, backing out skips `ScanResults`
 * and returns directly to `Scanner` to support repeated scanning.
 */
class CardDetailViewModel(
    private val cardId: String,
    private val fromScan: Boolean,
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
            CardDetailContract.Action.BackClicked -> {
                // Only scan-origin detail screens can skip ScanResults. Normal browsing flows
                // should continue using regular back navigation.
                val effect = if (state.value.quantityChanged && fromScan)
                    Effect.NavigateBackSkipScanResults
                else
                    Effect.NavigateBack
                emitEffect(effect)
            }
            CardDetailContract.Action.OnStart -> loadCardData()
            CardDetailContract.Action.DecrementOwnedClick -> {
                decrementOwnedCount()
                loadOwned()
                // Once quantity changes, scan-origin back should return directly to Scanner.
                setState { copy(quantityChanged = true) }
            }

            CardDetailContract.Action.IncrementOwnedClick -> {
                incrementOwnedCount()
                loadOwned()
                // Same rule as decrement: this marks the detail screen as "work completed" in
                // scan flow so the variant picker does not need to be shown again on back.
                setState { copy(quantityChanged = true) }
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
