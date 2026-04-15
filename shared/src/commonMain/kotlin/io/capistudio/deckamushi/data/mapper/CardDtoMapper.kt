package io.capistudio.deckamushi.data.mapper

import io.capistudio.deckamushi.data.remote.dto.CardDto

object CardDtoMapper {

    // Bitmask values (you can change later, just keep them power-of-two)
    //TODO ADD PROPER CODE
    private const val COLOR_RED = 1 shl 0
    private const val COLOR_GREEN = 1 shl 1
    private const val COLOR_BLUE = 1 shl 2
    private const val COLOR_PURPLE = 1 shl 3
    private const val COLOR_BLACK = 1 shl 4
    private const val COLOR_YELLOW = 1 shl 5

    fun colorFlagsToBitmask(color: String): Int {
        val parts = color.split('/')
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        var mask = 0
        parts.forEach {
            mask = mask or when (it.lowercase()) {
                "赤" -> COLOR_RED
                "緑" -> COLOR_GREEN
                "青" -> COLOR_BLUE
                "紫" -> COLOR_PURPLE
                "黒" -> COLOR_BLACK
                "黄" -> COLOR_YELLOW
                else -> 0
            }
        }
        return mask
    }

    fun rarityToId(rarity: String): Int =
        when (rarity.trim().uppercase()) {
            "C" -> 1
            "UC" -> 2
            "R" -> 3
            "SR" -> 4
            "L" -> 5
            "SEC" -> 6
            "SP" -> 7
            "TR" -> 8
            "PR" -> 9
            else -> 0
        }

    fun CardDto.toDbModel(): CardDbRow = CardDbRow(
        id = id,
        baseId = baseId,
        variant = variant,
        name = name,
        colorFlags = colorFlagsToBitmask(colorFlags),
        rarityId = rarityToId(rarity),
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