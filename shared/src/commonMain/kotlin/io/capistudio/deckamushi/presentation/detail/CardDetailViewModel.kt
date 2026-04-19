package io.capistudio.deckamushi.presentation.detail

import co.touchlab.kermit.Logger
import io.capistudio.deckamushi.domain.usecase.DecrementOwnedUseCase
import io.capistudio.deckamushi.domain.usecase.GetCardByIdUseCase
import io.capistudio.deckamushi.domain.usecase.GetOwnedQuantityUseCase
import io.capistudio.deckamushi.domain.usecase.IncrementOwnedUseCase
import io.capistudio.deckamushi.presentation.detail.CardDetailContract.Effect
import io.capistudio.deckamushi.presentation.mvi.Mvi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class CardDetailViewModel(
    private val cardId: String,
    private val getCardByIdUseCase: GetCardByIdUseCase,
    private val getOwnedQuantityUseCase: GetOwnedQuantityUseCase,
    private val incrementOwnedUseCase: IncrementOwnedUseCase,
    private val decrementOwnedUseCase: DecrementOwnedUseCase,
    scope: CoroutineScope = MainScope(),
) : Mvi<CardDetailContract.State, CardDetailContract.Action, CardDetailContract.Effect>(
    initialState = CardDetailContract.State(),
    scope = scope
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

        scope.launch {
            loadCardDetail()
            loadOwned()
        }
    }

    private suspend fun loadCardDetail() {
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

    private suspend fun loadOwned() {
        val qty = getOwnedQuantityUseCase(cardId)
        setState { copy(ownedQuantity = qty) }
    }

    private suspend fun incrementOwnedCount() {
        incrementOwnedUseCase(cardId)
    }

    private suspend fun decrementOwnedCount() {
        decrementOwnedUseCase(cardId)
    }

    fun clear() {
        scope.cancel()
    }
}