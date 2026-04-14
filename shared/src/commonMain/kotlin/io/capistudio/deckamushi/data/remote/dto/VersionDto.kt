package io.capistudio.deckamushi.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VersionDto(
    @SerialName("schema_version") val schemaVersion: Int,
    @SerialName("cards_version") val cardsVersion: String,
    @SerialName("generated_at_utc") val generatedAtUtc: String,
    @SerialName("card_count") val cardCount: Int,
    @SerialName("cards_sha256") val cardsSha256: String
)
