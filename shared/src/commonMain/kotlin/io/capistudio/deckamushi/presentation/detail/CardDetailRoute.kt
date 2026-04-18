package io.capistudio.deckamushi.presentation.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import io.capistudio.deckamushi.domain.usecase.GetCardByIdUseCase
import org.koin.compose.koinInject

@Composable
fun CardDetailRoute(
    cardId: String,
    onNavigateBack: () -> Unit,
) {
    val getCardByIdUseCase = koinInject<GetCardByIdUseCase>()
    val vm = remember(cardId) {
        CardDetailViewModel(cardId, getCardByIdUseCase)
    }

    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.effects.collect { effect ->
            when (effect) {
                CardDetailContract.Effect.NavigateBack -> onNavigateBack()
                is CardDetailContract.Effect.ShowMessage -> {
                    TODO("handle later the display of snackbars")
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