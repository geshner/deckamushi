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
import kotlinx.coroutines.launch


@Composable
@Preview
fun App(
    versionCacheFactory: VersionCacheFactory? = null
) {
    MaterialTheme {
        VersionFetchLessonScreen(versionCacheFactory)
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