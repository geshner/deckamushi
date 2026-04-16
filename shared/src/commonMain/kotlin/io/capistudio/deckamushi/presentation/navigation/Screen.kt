package io.capistudio.deckamushi.presentation.navigation

sealed interface Screen {
    data object Sync: Screen
    data object CardList: Screen
    data class CardDetail(val id: String) : Screen
}