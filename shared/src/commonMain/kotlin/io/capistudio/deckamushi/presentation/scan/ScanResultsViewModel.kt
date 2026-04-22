package io.capistudio.deckamushi.presentation.scan

import io.capistudio.deckamushi.domain.usecase.GetCardsByBaseIdUseCase
import io.capistudio.deckamushi.domain.util.onFailure
import io.capistudio.deckamushi.domain.util.onSuccess
import io.capistudio.deckamushi.presentation.mvi.Mvi

/**
 * Resolves a scanned base id into the concrete variant the user actually holds.
 *
 * Scanner flow only knows `base_id`, so this viewmodel loads all matching variants and emits a
 * navigation effect when the user selects the correct card art/printing.
 */
class ScanResultsViewModel(
    private val baseId: String,
    private  val getCardsByBaseIdUseCase: GetCardsByBaseIdUseCase,
) : Mvi<ScanResultsContract.State, ScanResultsContract.Action, ScanResultsContract.Effect>(
    initialState = ScanResultsContract.State()
) {

    override suspend fun handleAction(action: ScanResultsContract.Action) {
        when (action) {
            ScanResultsContract.Action.OnStart -> loadVariants()
            is ScanResultsContract.Action.CardClicked -> emitEffect(
                ScanResultsContract.Effect.NavigateToDetail(action.id)
            )
        }
    }

    private suspend fun loadVariants() {
        // Loading is eager because this screen exists only to resolve variants for one scanned id.
        setState { copy(isLoading = true) }
        getCardsByBaseIdUseCase(baseId)
            .onSuccess { cards -> setState { copy(isLoading = false, cards = cards) } }
            .onFailure { message, _ -> setState { copy(isLoading = false, error = message) } }
    }
}