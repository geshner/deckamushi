package io.capistudio.deckamushi.domain.model

data class Card(
    val id: String,
    val baseId: String,
    val variant: String?,
    val name: String,
//    val colorFlags: Int,
//    val rarityId: Int,
//    val cardCategory: String,
//    val attackPower: Int?,
//    val counterPower: Int?,
//    val life: Int?,
//    val combatAttribute: String?,
//    val feature: String?,
//    val cardText: String?,
//    val blockIconCode: String?,
//    val packName: String?,
    val imageUrl: String?,
)
