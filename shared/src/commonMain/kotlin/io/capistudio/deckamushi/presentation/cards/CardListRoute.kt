package io.capistudio.deckamushi.presentation.cards

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.capistudio.deckamushi.presentation.navigation.Screen
import org.koin.compose.koinInject

@Composable
fun CardListRoute(onNavigateToDetail: (String) -> Unit) {
    val vm: CardsBrowserViewModel = koinInject()
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.effects.collect { effect ->
            when (effect) {
                is CardsBrowserContract.Effect.NavigateToDetail ->
                    onNavigateToDetail(effect.id)
                is CardsBrowserContract.Effect.ShowMessage -> {
                    //will add snackbars later
                    TODO()
                }
            }
        }
    }

    CardBrowserScreen(
        state = state,
        onAction = vm::dispatch
    )
}