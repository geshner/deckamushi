package io.capistudio.deckamushi.core.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import co.touchlab.kermit.Logger as KermitLogger
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

expect fun createHttpClient(): HttpClient

private val TIMEOUT_MILLIS = 30_000L

fun HttpClientConfig<*>.commonConfig() {
    install(HttpTimeout) {
        requestTimeoutMillis = TIMEOUT_MILLIS
        socketTimeoutMillis = TIMEOUT_MILLIS
        connectTimeoutMillis = TIMEOUT_MILLIS
    }
    install(ContentNegotiation) {
        json(
            Json { ignoreUnknownKeys = true },
            ContentType.Text.Plain
        )
    }
    install(Logging) {
        // Delegate Ktor logs to Kermit
        logger = object : Logger {
            override fun log(message: String) {
                KermitLogger.d(tag = "HTTP Client") { message }
            }
        }
        level = LogLevel.ALL
        sanitizeHeader { header -> header == HttpHeaders.Authorization }
    }
}