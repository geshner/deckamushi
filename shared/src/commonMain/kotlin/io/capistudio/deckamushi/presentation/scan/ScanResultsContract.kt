package io.capistudio.deckamushi.presentation.scan

import io.capistudio.deckamushi.domain.model.Card

object ScanResultsContract {

    data class State(
        val isLoading: Boolean = false,
        val cards: List<Card> = emptyList(),
        val error: String? = null,
    )

    sealed interface Action{
        data object OnStart : Action
        data class CardClicked(val id: String) : Action
    }

    sealed interface Effect {
        data class NavigateToDetail(val cardId: String) : Effect
    }
}