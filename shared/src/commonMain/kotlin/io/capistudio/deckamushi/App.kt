package io.capistudio.deckamushi

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.capistudio.deckamushi.presentation.cards.CardListRoute
import io.capistudio.deckamushi.presentation.detail.CardDetailRoute
import io.capistudio.deckamushi.presentation.navigation.Screen
import io.capistudio.deckamushi.presentation.sync.SyncRoute
import kotlinx.coroutines.launch


@Composable
fun App() {
    var screen by remember { mutableStateOf<Screen>(Screen.Sync) }
    val showBottomBar = screen !is Screen.CardDetail

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val showSnackbar: (String) -> Unit = { message ->
        scope.launch { snackbarHostState.showSnackbar(message) }
    }

    MaterialTheme {
        Scaffold(
            snackbarHost = {SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        NavigationBarItem(
                            selected = screen is Screen.CardList,
                            onClick = { screen = Screen.CardList },
                            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Cards") },
                            label = { Text("Cards")}
                        )
                        NavigationBarItem(
                            selected = screen is Screen.Sync,
                            onClick = { screen = Screen.Sync },
                            icon = { Icon(Icons.Default.Sync, contentDescription = "Sync") },
                            label = { Text("Sync")}
                        )
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier.padding(padding)
            ) {
                when (val s = screen) {
                    Screen.Sync -> key("sync") {
                        SyncRoute(showSnackbar) {
                            screen = Screen.CardList
                        }
                    }

                    Screen.CardList -> key("list") {
                        CardListRoute(
                            showSnackbar = showSnackbar,
                            onNavigateToDetail = { id -> screen = Screen.CardDetail(id) }
                        )
                    }

                    is Screen.CardDetail -> key("detail") {
                        CardDetailRoute(s.id, showSnackbar) {
                            screen = Screen.CardList
                        }
                    }
                }
            }
        }
    }
}
