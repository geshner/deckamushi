package io.capistudio.deckamushi.presentation.collection

import io.capistudio.deckamushi.domain.model.OwnedCard

object CollectionContract {

    data class State(
        val totalCount: Long = 0,
        val error: String? = null
    )

    sealed interface Action {
        data object OnStart : Action
        data class CardClicked(val id: String) : Action
    }

    sealed interface Effect {
        data class NavigateToDetail(val id: String) : Effect
        data class ShowMessage(val message: String) : Effect
    }
}