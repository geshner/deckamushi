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
    data class CardDetail(
        /** Unique variant id for the card to show in detail. */
        val id: String,
        /**
         * Marks that this detail screen belongs to scanner flow.
         *
         * When `true`, back behavior may skip `ScanResults` after a quantity change so repeated
         * scanning is faster.
         */
        val fromScan: Boolean = false
    ) : Screen
    @Serializable
    data object Scanner : Screen
    @Serializable
    data class ScanResults(
        /** Base id used to load all print variants that matched a scan. */
        val baseId: String
    ) : Screen
}