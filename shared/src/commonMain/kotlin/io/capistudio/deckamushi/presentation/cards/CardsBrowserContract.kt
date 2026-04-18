package io.capistudio.deckamushi.presentation.cards

import io.capistudio.deckamushi.domain.model.Card

object CardsBrowserContract {

    data class State(
        val queryDraft: String = "",
        val isSearching: Boolean = false,
        val totalCount: Long? = null,
        val cards: List<Card> = emptyList(),
        val isAppending: Boolean = false,
        val endReached: Boolean = false,
        val error: String? = null,
    )

    sealed interface Action {
        data object OnStart : Action
        data class QueryChanged(val value: String) : Action
        data object SearchClicked : Action
        data object LoadMore : Action
        data class CardClicked(val id: String) : Action
    }

    sealed interface Effect {
        data class NavigateToDetail(val id: String) : Effect
        data class ShowMessage(val message: String) : Effect
    }
}
