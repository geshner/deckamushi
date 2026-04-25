package io.capistudio.deckamushi.domain.model

enum class CardType(val code: String) {
    UNKNOWN(""),
    CHARACTER("CHARACTER"),
    EVENT("EVENT"),
    LEADER("LEADER"),
    STAGE("STAGE"),
}

fun String.toCardType(): CardType =
    CardType.entries.find { it.code == this.trim() } ?: CardType.UNKNOWN