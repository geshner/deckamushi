package io.capistudio.deckamushi.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

actual fun createHttpClient() = HttpClient(Darwin) {
    commonConfig()
}