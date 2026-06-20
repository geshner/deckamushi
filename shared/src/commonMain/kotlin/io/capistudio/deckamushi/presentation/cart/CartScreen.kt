package io.capistudio.deckamushi.presentation.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.capistudio.deckamushi.presentation.cart.CartContract.Action as CartAction

@Composable
fun CartScreen(
    state: CartContract.State,
    onAction: (CartAction) -> Unit
) {

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isCartEmpty) {
            EmptyCart()
        } else {
            LazyColumn {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "${state.totalCount} items",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }

                items(state.items, key = { it.id }) { item ->
                    CartItemRow(
                        item = item,
                        onIncrement = { onAction(CartAction.SetCartQuantity(item.id, item.cartQuantity + 1)) },
                        onDecrement = { onAction(CartAction.SetCartQuantity(item.id, item.cartQuantity - 1)) },
                        onRemove = { onAction(CartAction.RemoveItem(item.id)) },
                    )
                }

                item {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Button(
                            modifier = Modifier.fillMaxWidth().padding(all = 16.dp),
                            onClick = { onAction(CartAction.RequestCompletePurchase) }) {
                            Text("Complete purchase")
                        }
                        TextButton(onClick = { onAction(CartAction.RequestClearCart) }) {
                            Text("Clear cart")
                        }
                    }
                }

            }
        }

        FloatingActionButton(
            onClick = { onAction(CartAction.ScanCard) },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = "Scan card")
        }
    }

    // Clear confirmation dialog
    if (state.showClearConfirmDialog) {
        AlertDialog(
            title = { Text("Clear cart?") },
            text = { Text("This will not add any cards to your collection.") },
            confirmButton = { TextButton(onClick = { onAction(CartAction.ConfirmClearCart) }) { Text("Yes") } },
            dismissButton = { TextButton(onClick = { onAction(CartAction.DismissDialog) }) { Text("Cancel") } },
            onDismissRequest = { onAction(CartAction.DismissDialog) },
        )
    }

    // Complete purchase confirmation dialog
    if (state.showCompletePurchaseDialog) {
        AlertDialog(
            title = { Text("Complete purchase?") },
            text = { Text("Add ${state.totalCount} cards to your collection?") },
            onDismissRequest = { onAction(CartAction.DismissDialog) },
            confirmButton = { TextButton(onClick = { onAction(CartAction.ConfirmCompletePurchase) }){ Text("Add") }},
            dismissButton = { TextButton(onClick = { onAction(CartAction.DismissDialog) }){ Text("Cancel") }},
        )
    }

}

@Composable
private fun EmptyCart() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Your cart is empty",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "Tap the camera button to scan a card",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        )
    }
}
