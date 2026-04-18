package io.capistudio.deckamushi.presentation.detail

import co.touchlab.kermit.Logger
import io.capistudio.deckamushi.domain.usecase.GetCardByIdUseCase
import io.capistudio.deckamushi.presentation.detail.CardDetailContract.Effect
import io.capistudio.deckamushi.presentation.mvi.Mvi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CardDetailViewModel(
    private val cardId: String,
    private val getCardByIdUseCase: GetCardByIdUseCase,
    scope: CoroutineScope = MainScope(),
) : Mvi<CardDetailContract.State, CardDetailContract.Action, CardDetailContract.Effect>(
    initialState = CardDetailContract.State(),
    scope = scope
) {

    private val log = Logger.withTag("CardDetailVM")

    override suspend fun handleAction(action: CardDetailContract.Action) {
        when (action) {
            CardDetailContract.Action.BackClicked -> emitEffect(Effect.NavigateBack)
            CardDetailContract.Action.OnStart -> load()
        }
    }

    private suspend fun load() {

        if (state.value.isLoading) return

        setState { copy(isLoading = true, error = null) }

        scope.launch {
            runCatching {
                val card = getCardByIdUseCase(cardId)
                setState { copy(
                    isLoading = false,
                    card = card,
                    error = null
                ) }
            }.onFailure { t ->
                log.e(t) { "load card failed (id=$cardId)" }
                setState { copy(isLoading = false, card = null, error = t.message ?: "Unknown error") }
            }
        }
    }

    fun clear() {
        scope.cancel()
    }
}