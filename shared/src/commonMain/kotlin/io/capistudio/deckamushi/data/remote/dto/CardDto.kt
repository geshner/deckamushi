package io.capistudio.deckamushi.data.remote.dto

import io.ktor.http.HttpStatusCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CardDto (
    @SerialName("variant_id") val id: String,
    @SerialName("id") val baseId: String,
    @SerialName("variant") val variant: String?,
    @SerialName("name") val name: String,
    @SerialName("color") val colorFlags: String,
    @SerialName("rarity") val rarity: String,
    @SerialName("card_type") val cardType: String,
    @SerialName("power") val attackPower: Int?,
    @SerialName("counter") val counterPower: Int?,
    @SerialName("life") val life: Int?,
    @SerialName("attribute") val combatAttribute: String?,
    @SerialName("feature") val feature: String?,
    @SerialName("text") val text: String?,
    @SerialName("block_icon") val blockIconCode: String?,
    @SerialName("get_info") val packName: String?,
    @SerialName("image_url") val imageUrl: String
)
