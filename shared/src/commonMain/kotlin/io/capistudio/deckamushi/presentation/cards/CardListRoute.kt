package io.capistudio.deckamushi.presentation.cards

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.paging.compose.collectAsLazyPagingItems
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CardListRoute(
    onNavigateToDetail: (String) -> Unit,
    showSnackbar: (String) -> Unit,
) {
    val vm: CardsBrowserViewModel = koinViewModel()
    val state by vm.state.collectAsState()
    val pagingItems = vm.cardsPagingData.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        vm.effects.collect { effect ->
            when (effect) {
                is CardsBrowserContract.Effect.NavigateToDetail ->
                    onNavigateToDetail(effect.id)
                is CardsBrowserContract.Effect.ShowMessage -> {
                    showSnackbar(effect.message)
                }
            }
        }
    }

    CardListScreen(
        state = state,
        onAction = vm::dispatch,
        pagingItems = pagingItems
    )
}