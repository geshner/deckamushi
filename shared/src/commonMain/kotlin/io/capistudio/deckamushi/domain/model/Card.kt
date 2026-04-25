package io.capistudio.deckamushi.domain.model

data class Card(
    val id: String,
    val baseId: String,
    val variant: String?,
    val name: String,
    val colors: Set<CardColor>,
    val rarity: Rarity,
    val cardType: CardType,
    val attackPower: Long?,
    val counterPower: Long?,
    val life: Long?,
    val combatAttribute: String?,
    val feature: String?,
    val cardText: String?,
    val blockIconCode: String?,
    val packName: String?,
    val imageUrl: String,
) {
    val isReprint: Boolean
        get() = variant?.startsWith("r", true) == true
}
