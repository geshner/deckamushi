package io.capistudio.deckamushi.domain.model

data class CartItemSummary(
    val id: String,
    val variant: String?,
    val name: String,
    val imageUrl: String?,
    val cartQuantity: Long,
    val ownedQuantity: Long,
) {
    val isReprint: Boolean get() = variant?.startsWith("r", ignoreCase = true) == true
    val isNew: Boolean get() = ownedQuantity == 0L
}