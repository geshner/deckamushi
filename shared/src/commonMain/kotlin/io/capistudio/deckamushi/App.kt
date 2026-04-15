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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.capistudio.deckamushi.core.network.createHttpClient
import io.capistudio.deckamushi.data.local.VersionCache
import io.capistudio.deckamushi.data.local.VersionCacheFactory
import io.capistudio.deckamushi.data.remote.DeckamushiDataApi
import io.capistudio.deckamushi.data.remote.RemoteResult
import io.capistudio.deckamushi.di.AppDependencies
import io.capistudio.deckamushi.domain.usecase.UpdateCardDataUseCase
import io.capistudio.deckamushi.presentation.cards.CardBrowserScreen
import kotlinx.coroutines.launch


@Composable
fun App(
    deps: AppDependencies? = null
) {
    MaterialTheme {
        CardBrowserScreen(deps!!.cardsBrowserViewModel)

//        DebugSyncScreen(deps)
    }
}

@Composable
private fun DebugSyncScreen(
    deps: AppDependencies?
) {
    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf("Idle") }
    var lastSeededVersion by remember { mutableStateOf<String?>(null)}
    var lastSeededCount by remember { mutableStateOf<Int?>(null) }

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
            enabled = deps != null && status != "Working...",
            onClick = {
                scope.launch {
                    val useCase = deps?.updateCardDataUseCase
                    if (useCase == null) {
                        status = "Deps not provided"
                        return@launch
                    }

                    status = "Working..."
                    when (val result = useCase.run()) {
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
                        is RemoteResult.HttpError -> status = "HTTP ${result.code}: ${result.message}"
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