package io.capistudio.deckamushi.presentation.detail

import io.capistudio.deckamushi.domain.model.Card

object CardDetailContract {

    data class State(
        val isLoading : Boolean = false,
        val card: Card? = null,
        val error: String? = null,
    )

    sealed interface Action {
        data object OnStart: Action
        data object BackClicked: Action
    }

    sealed interface Effect {
        data object NavigateBack : Effect
        data class ShowMessage(val message: String) : Effect
    }
}