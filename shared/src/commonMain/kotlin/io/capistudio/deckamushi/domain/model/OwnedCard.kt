package io.capistudio.deckamushi.domain.model

data class OwnedCard(
    val id: String,
    val baseId: String,
    val variant: String?,
    val name: String,
    val imageUrl: String?,
    val ownedQuantity: Long,
)
