package io.capistudio.deckamushi.presentation.cards

import io.capistudio.deckamushi.domain.model.Card

object CardsBrowserContract {

    data class State(
        val queryDraft: String = "",
        val totalCount: Long? = null,
        val error: String? = null,

        val scrollIndex: Int = 0,
        val scrollOffset: Int = 0
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
