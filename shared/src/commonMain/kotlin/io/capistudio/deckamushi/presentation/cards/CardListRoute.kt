package io.capistudio.deckamushi.presentation.cards

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.paging.compose.collectAsLazyPagingItems
import io.capistudio.deckamushi.presentation.components.CollectEffects
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CardListRoute(
    onNavigateToDetail: (String) -> Unit,
    showSnackbar: (String) -> Unit,
) {
    val vm: CardListViewModel = koinViewModel()
    val state by vm.state.collectAsState()
    val pagingItems = vm.cardsPagingData.collectAsLazyPagingItems()

    CollectEffects(vm.effects) { effect ->
        when (effect) {
            is CardsListContract.Effect.NavigateToDetail ->
                onNavigateToDetail(effect.id)

            is CardsListContract.Effect.ShowMessage -> {
                showSnackbar(effect.message)
            }
        }
    }

    CardListScreen(
        state = state,
        onAction = vm::dispatch,
        pagingItems = pagingItems
    )
}