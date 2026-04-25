package io.capistudio.deckamushi.domain.model

enum class Rarity(val id: Int, val code: String) {
    UNKNOWN(-1, ""),
    COMMON(0, "C"),
    UNCOMMON(1, "UC"),
    RARE(2, "R"),
    SUPER_RARE(3, "SR"),
    SECRET_RARE(4, "SEC"),
    LEADER(5, "L"),
    PROMO(6, "P"),
    SP_PROMO(7, "SP P"),
    SP_CARD(8, "SPカード");
}

fun Int.toRarity(): Rarity = Rarity.entries.find { it.id == this } ?: Rarity.UNKNOWN

fun String.toRarity(): Rarity = Rarity.entries.find { it.code == this.trim() } ?: Rarity.UNKNOWN