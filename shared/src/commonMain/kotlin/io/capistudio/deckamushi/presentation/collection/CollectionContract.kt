package io.capistudio.deckamushi.presentation.collection

import io.capistudio.deckamushi.domain.model.OwnedCard

object CollectionContract {

    data class State(
        val cards: List<OwnedCard> = emptyList(),
        val totalCount: Long? = null,
        val isAppending: Boolean = false,
        val endReached: Boolean = false,
        val error: String? = null
    )

    sealed interface Action {
        data object OnStart : Action
        data object LoadMore : Action
        data class CardClicked(val id: String) : Action
    }

    sealed interface Effect {
        data class NavigateToDetail(val id: String) : Effect
        data class ShowMessage(val message: String) : Effect
    }
}