package io.capistudio.deckamushi.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    object Home : Screen
    @Serializable
    data object Sync: Screen
    @Serializable
    data object CardList: Screen
    @Serializable
    data object Collection: Screen
    @Serializable
    data class CardDetail(val id: String) : Screen
    @Serializable
    data object Scanner : Screen
    @Serializable
    data class ScanResults(val baseId: String) : Screen
}