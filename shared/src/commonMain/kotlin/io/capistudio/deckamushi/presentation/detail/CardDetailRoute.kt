package io.capistudio.deckamushi.presentation.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun CardDetailRoute(
    cardId: String,
    showSnackbar: (String) -> Unit,
    onBack: () -> Unit,
) {
    val vm: CardDetailViewModel = koinInject { parametersOf(cardId) }

    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.effects.collect { effect ->
            when (effect) {
                CardDetailContract.Effect.NavigateBack -> onBack()
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