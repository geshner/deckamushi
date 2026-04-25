package io.capistudio.deckamushi.domain.model

data class CardSummary(
    val id: String,
    val variant: String?,
    val name: String,
    val imageUrl: String,
    val ownedCount: Long = 0L
) {
    val isReprint: Boolean
        get() = variant?.startsWith("r", true) == true
}