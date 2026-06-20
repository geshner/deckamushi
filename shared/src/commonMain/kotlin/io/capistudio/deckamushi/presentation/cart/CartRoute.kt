package io.capistudio.deckamushi.presentation.cart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.capistudio.deckamushi.presentation.components.CollectEffects
import io.capistudio.deckamushi.presentation.cart.CartContract.Action as CartAction

@Composable
fun CartRoute(
    vm: CartViewModel, // graph-scoped, resolved in App.k
    onNavigateToScan: () -> Unit,
    onBack: () -> Unit,
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.dispatch(CartAction.OnStart)
    }

    CollectEffects(vm.effects) { effect ->
        when (effect) {
            CartContract.Effect.NavigateToScan -> onNavigateToScan()
            CartContract.Effect.PurchaseComplete -> onBack()
        }
    }

    CartScreen(
        state = state,
        onAction = vm::dispatch
    )
}
