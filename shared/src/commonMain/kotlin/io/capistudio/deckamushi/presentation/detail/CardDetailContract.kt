package io.capistudio.deckamushi.presentation.detail

import io.capistudio.deckamushi.domain.model.Card

object CardDetailContract {

    data class State(
        val isLoading : Boolean = false,
        val card: Card? = null,
        val error: String? = null,
        val ownedQuantity: Long = 0L,
        val quantityChanged: Boolean = false,
    )

    sealed interface Action {
        data object OnStart: Action
        data object BackClicked: Action
        data object IncrementOwnedClick : Action
        data object DecrementOwnedClick : Action
    }

    sealed interface Effect {
        data object NavigateBack : Effect
        data object NavigateBackSkipScanResults : Effect
        data class ShowMessage(val message: String) : Effect
    }
}