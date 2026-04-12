package io.capistudio.deckamushi

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform