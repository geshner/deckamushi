package io.capistudio.deckamushi.domain.model

data class Card(
    val id: String,
    val baseId: String,
    val variant: String?,
    val name: String,
    val imageUrl: String?
)
