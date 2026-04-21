package io.capistudio.deckamushi.presentation.collection

object CollectionContract {

    data class State(
        val totalCount: Long = 0,
        val error: String? = null,
        val scrollIndex: Int = 0,
        val scrollOffset: Int = 0
    )

    sealed interface Action {
        data object OnStart : Action
        data class CardClicked(val id: String) : Action

        data class ScrollPositionChanged(val index: Int, val offset: Int) : Action
    }

    sealed interface Effect {
        data class NavigateToDetail(val id: String) : Effect
        data class ShowMessage(val message: String) : Effect
    }
}