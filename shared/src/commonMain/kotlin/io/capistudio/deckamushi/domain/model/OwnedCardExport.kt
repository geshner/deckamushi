package io.capistudio.deckamushi.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class OwnedCardExport(
    val cardId: String,
    val quantity: Long,
)
