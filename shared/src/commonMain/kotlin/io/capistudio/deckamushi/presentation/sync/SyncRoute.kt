package io.capistudio.deckamushi.presentation.sync

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.capistudio.deckamushi.presentation.sync.SyncContract.Effect
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SyncRoute(
    showSnackbar: (String) -> Unit,
    onNavigateToList: () -> Unit,
) {
    val vm: SyncViewModel = koinViewModel()
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.effects.collect { effect ->
            when (effect) {
                Effect.NavigateToList -> onNavigateToList()
                is Effect.ShowMessage -> showSnackbar(effect.message)
            }
        }
    }

    SyncScreen(
        state = state,
        onAction = vm::dispatch,
    )
}