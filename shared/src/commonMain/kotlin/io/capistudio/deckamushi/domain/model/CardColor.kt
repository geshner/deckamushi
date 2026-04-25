package io.capistudio.deckamushi.domain.model

enum class CardColor(val bit: Int) {
    RED(1),
    GREEN(2),
    BLUE(4),
    PURPLE(8),
    BLACK(16),
    YELLOW(32)
}

fun Set<CardColor>.toFlags(): Int = fold(0) { acc, c ->
    acc or c.bit
}

fun Int.toCardColors(): Set<CardColor> = CardColor
    .entries
    .filter {
        this and it.bit != 0
    }.toSet()