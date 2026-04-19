package io.capistudio.deckamushi.presentation.collection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.koinInject

@Composable
fun CollectionRoute(
    showSnackbar: (String) -> Unit,
    onNavigateToDetail: (String) -> Unit,
) {
    val vm: CollectionViewModel = koinInject()
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.effects.collect { effect ->
            when (effect) {
                is CollectionContract.Effect.ShowMessage -> showSnackbar(effect.message)
                is CollectionContract.Effect.NavigateToDetail -> onNavigateToDetail(effect.id)
            }
        }
    }

    LaunchedEffect(Unit) {
        vm.dispatch(CollectionContract.Action.OnStart)
    }

    CollectionScreen(
        state = state,
        onAction = vm::dispatch
    )
}
