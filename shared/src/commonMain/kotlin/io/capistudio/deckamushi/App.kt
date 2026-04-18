package io.capistudio.deckamushi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.capistudio.deckamushi.core.network.createHttpClient
import io.capistudio.deckamushi.data.local.VersionCache
import io.capistudio.deckamushi.data.local.VersionCacheFactory
import io.capistudio.deckamushi.data.remote.DeckamushiDataApi
import io.capistudio.deckamushi.data.remote.RemoteResult
import io.capistudio.deckamushi.domain.usecase.UpdateCardDataUseCase
import io.capistudio.deckamushi.presentation.cards.CardListRoute
import io.capistudio.deckamushi.presentation.detail.CardDetailRoute
import io.capistudio.deckamushi.presentation.navigation.Screen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject


@Composable
fun App() {
    var screen by remember { mutableStateOf<Screen>(Screen.Sync) }
    MaterialTheme {

        when (val s = screen) {
            Screen.Sync -> key("sync") {
                DebugSyncScreen(
                    goToList = { screen = Screen.CardList }
                )
            }

            Screen.CardList -> key("list") {
                CardListRoute { id -> screen = Screen.CardDetail(id) }
            }

            is Screen.CardDetail -> key("detail") {
                CardDetailRoute(s.id) {
                    screen = Screen.CardList
                }
            }
        }
    }
}

@Composable
private fun DebugSyncScreen(
    goToList: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf("Idle") }
    var lastSeededVersion by remember { mutableStateOf<String?>(null) }
    var lastSeededCount by remember { mutableStateOf<Int?>(null) }
    val updateCardDataUseCase: UpdateCardDataUseCase = koinInject()

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("cards.json → seed DB")
        Spacer(Modifier.height(12.dp))

        Text("Status: $status")
        Text("Last seeded version: ${lastSeededVersion ?: "<none>"}")
        Text("Rows written: ${lastSeededCount?.toString() ?: "<none>"}")

        Spacer(Modifier.height(12.dp))

        Button(
            enabled = status != "Working...",
            onClick = {
                scope.launch {

                    status = "Working..."
                    when (val result = updateCardDataUseCase.run()) {
                        is UpdateCardDataUseCase.Result.UpToDate -> {
                            status = "Up to date"
                        }

                        is UpdateCardDataUseCase.Result.Seeded -> {
                            status = "Seeded OK"
                            lastSeededVersion = result.cardsVersion
                            lastSeededCount = result.insertedOrReplaced
                        }

                        is UpdateCardDataUseCase.Result.Error -> {
                            status = "Error: ${result.message}"
                        }
                    }
                }
            }
        ) {
            Text("Sync / Seed cards")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = goToList
        ) {
            Text("Go To List")
        }
    }
}

@Composable
private fun VersionFetchLessonScreen(
    versionCacheFactory: VersionCacheFactory?,
    versionCacheProvider: suspend () -> VersionCache? = { null }
) {
    val scope = rememberCoroutineScope()

    val api = remember { DeckamushiDataApi(createHttpClient()) }

    var status by remember { mutableStateOf("Idle") }
    var etag by remember { mutableStateOf<String?>(null) }
    var cardVersion by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("version.json download")
        Spacer(Modifier.height(12.dp))
        Text("Status: $status")
        Text("ETag: ${etag ?: "<none>"}")
        Text("cardsVersion: ${cardVersion ?: "<none>"}")
        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                scope.launch {
                    val cache = versionCacheFactory?.create()

                    etag = cache?.getVersionETag()
                    cardVersion = cache?.getCardsVersion()

                    status = "Fetching..."
                    when (val result = api.fetchVersion(etag = etag)) {
                        is RemoteResult.HttpError -> status =
                            "HTTP ${result.code}: ${result.message}"

                        is RemoteResult.NetworkError -> status = "Network error: ${result.message}"
                        is RemoteResult.NotModified -> {
                            status = "not Modified (304)"
                            etag = result.eTag
                            cache?.setVersionETag(etag)
                        }

                        is RemoteResult.Success -> {
                            status = "OK (200)"
                            etag = result.eTag
                            cardVersion = result.data.cardsVersion
                            cache?.apply {
                                setVersionETag(etag)
                                setCardsVersion(cardVersion ?: "")
                            }
                        }
                    }
                }
            }
        ) {
            Text("Fetch version.json")
        }

    }
}
