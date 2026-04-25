package io.capistudio.deckamushi.presentation.collection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.paging.compose.collectAsLazyPagingItems
import io.capistudio.deckamushi.presentation.components.CollectEffects
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CollectionRoute(
    showSnackbar: (String) -> Unit,
    onNavigateToDetail: (String) -> Unit,
) {
    val vm: CollectionViewModel = koinViewModel()
    val state by vm.state.collectAsState()

    CollectEffects(vm.effects) { effect ->
        when (effect) {
            is CollectionContract.Effect.ShowMessage -> showSnackbar(effect.message)
            is CollectionContract.Effect.NavigateToDetail -> onNavigateToDetail(effect.id)
        }
    }

    CollectionScreen(
        state = state,
        pagingItems = vm.ownedCardsPagingData.collectAsLazyPagingItems(),
        onAction = vm::dispatch
    )
}
