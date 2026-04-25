package io.capistudio.deckamushi.data.mapper

import io.capistudio.deckamushi.data.remote.dto.CardDto
import io.capistudio.deckamushi.domain.model.CardColor
import io.capistudio.deckamushi.domain.model.toRarity

object CardDtoMapper {

    fun colorFlagsToBitmask(color: String): Int = color.split('/')
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .fold(0) { acc, part ->
            acc or when (part) {
                "赤" -> CardColor.RED.bit
                "緑" -> CardColor.GREEN.bit
                "青" -> CardColor.BLUE.bit
                "紫" -> CardColor.PURPLE.bit
                "黒" -> CardColor.BLACK.bit
                "黄" -> CardColor.YELLOW.bit
                else -> 0
            }
        }

    fun CardDto.toDbModel(): CardDbRow = CardDbRow(
        id = id,
        baseId = baseId,
        variant = variant,
        name = name,
        colorFlags = colorFlagsToBitmask(colorFlags),
        rarityId = rarity.toRarity().id,
        cardCategory = cardType,
        attackPower = attackPower,
        counterPower = counterPower,
        life = life,
        combatAttribute = combatAttribute,
        feature = feature,
        cardText = text,
        blockIconCode = blockIconCode,
        packName = packName,
        imageUrl = imageUrl,
    )
}

data class CardDbRow(
    val id: String,
    val baseId: String,
    val variant: String?,
    val name: String,
    val colorFlags: Int,
    val rarityId: Int,
    val cardCategory: String,
    val attackPower: Int?,
    val counterPower: Int?,
    val life: Int?,
    val combatAttribute: String?,
    val feature: String?,
    val cardText: String?,
    val blockIconCode: String?,
    val packName: String?,
    val imageUrl: String?,
)