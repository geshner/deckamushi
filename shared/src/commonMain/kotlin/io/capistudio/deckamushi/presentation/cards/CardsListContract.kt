package io.capistudio.deckamushi.presentation.cards

object CardsListContract {

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
        data class CardClicked(val id: String) : Action
        data class ScrollPositionChanged(val index: Int, val offset: Int) : Action
    }

    sealed interface Effect {
        data class NavigateToDetail(val id: String) : Effect
        data class ShowMessage(val message: String) : Effect
    }
}
