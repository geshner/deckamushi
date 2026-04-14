package io.capistudio.deckamushi

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import deckamushi.shared.generated.resources.Res
import deckamushi.shared.generated.resources.compose_multiplatform
import io.capistudio.deckamushi.core.network.createHttpClient
import io.capistudio.deckamushi.data.remote.DeckamushiDataApi
import io.capistudio.deckamushi.data.remote.RemoteResult
import io.capistudio.deckamushi.data.remote.dto.VersionDto
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource


@Composable
@Preview
fun App() {
    MaterialTheme {
        VersionFetchLessonScreen()
        return@MaterialTheme
        var showContent by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
            AnimatedVisibility(showContent) {
                val greeting = remember { Greeting().greet() }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $greeting")
                }
            }
        }
    }
}

@Composable
private fun VersionFetchLessonScreen() {
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
                    status = "Fetching..."
                    when (val result = api.fetchVersion(etag = null)) {
                        is RemoteResult.HttpError -> status = "HTTP ${result.code}: ${result.message}"
                        is RemoteResult.NetworkError -> status = "Network error: ${result.message}"
                        is RemoteResult.NotModified -> {
                            status = "not Modified (304)"
                            etag = result.eTag
                        }
                        is RemoteResult.Success<*> -> {
                            status = "OK (200)"
                            etag = result.eTag
                            cardVersion = (result.data as VersionDto).cardsVersion
                        }
                    }
                }
            }
        ) {
            Text("Fetch version.json")
        }

    }
}