package io.capistudio.deckamushi.navigation

object AndroidRoutes {
    const val HOME = "home"
    const val CARDS = "cards"
    const val COLLECTION = "collection"
    const val SYNC = "sync"
    const val CARD_DETAIL = "cardDetail/{cardId}"
    const val ARG_CARD_ID = "cardId"
    fun cardDetail(cardId: String): String = "cardDetail/$cardId"

    const val SCAN = "scan"
    const val SCAN_RESULTS = "scanResults/{baseId}"
    const val ARG_BASE_ID = "baseId"
    fun scanResults(baseId: String): String = "scanResults/$baseId"
}
