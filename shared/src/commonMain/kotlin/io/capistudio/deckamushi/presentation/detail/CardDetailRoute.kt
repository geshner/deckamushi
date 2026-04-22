package io.capistudio.deckamushi.presentation.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.capistudio.deckamushi.presentation.components.PlatformBackHandler
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun CardDetailRoute(
    cardId: String,
    fromScan: Boolean = false,
    showSnackbar: (String) -> Unit,
    onRegisterBackOverride: ((() -> Unit)?) -> Unit = {},
    onBack: () -> Unit,
    onBackSkipScanResults: () -> Unit = onBack,
) {
    val vm: CardDetailViewModel = koinInject { parametersOf(cardId, fromScan) }
    val state by vm.state.collectAsState()

    // Register our ViewModel-aware back handler into the top bar, clean up on leave
    DisposableEffect(Unit) {
      onRegisterBackOverride { vm.dispatch(CardDetailContract.Action.BackClicked) }
      onDispose { onRegisterBackOverride(null) }
    }

    PlatformBackHandler {
        vm.dispatch(CardDetailContract.Action.BackClicked)
    }

    LaunchedEffect(Unit) {
        vm.effects.collect { effect ->
            when (effect) {
                CardDetailContract.Effect.NavigateBack -> onBack()
                CardDetailContract.Effect.NavigateBackSkipScanResults -> onBackSkipScanResults()
                is CardDetailContract.Effect.ShowMessage -> {
                    showSnackbar(effect.message)
                }
            }
        }
    }

    LaunchedEffect(cardId) {
        vm.dispatch(CardDetailContract.Action.OnStart)
    }

    CardDetailScreen(
        state = state,
        onAction = vm::dispatch
    )
}