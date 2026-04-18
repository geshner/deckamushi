package io.capistudio.deckamushi

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.capistudio.deckamushi.presentation.cards.CardListRoute
import io.capistudio.deckamushi.presentation.detail.CardDetailRoute
import io.capistudio.deckamushi.presentation.navigation.Screen
import io.capistudio.deckamushi.presentation.sync.SyncRoute


@Composable
fun App() {
    var screen by remember { mutableStateOf<Screen>(Screen.Sync) }
    MaterialTheme {

        when (val s = screen) {
            Screen.Sync -> key("sync") {
                SyncRoute {
                    screen = Screen.CardList
                }
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
