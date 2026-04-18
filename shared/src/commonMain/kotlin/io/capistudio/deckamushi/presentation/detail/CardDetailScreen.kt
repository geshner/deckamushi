package io.capistudio.deckamushi.presentation.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.capistudio.deckamushi.presentation.detail.CardDetailContract.Action

@Composable
fun CardDetailScreen(
    state: CardDetailContract.State,
    onAction: (Action) -> Unit,
) {

    LaunchedEffect(Unit) {
        onAction(Action.OnStart)
    }

    Column(
        modifier = Modifier.safeContentPadding()
    ) {
        Button(onClick = { onAction(Action.BackClicked) }) { Text("Back") }
        Spacer(Modifier.height(12.dp))

        when {
            state.isLoading -> Text("Loading...")
            state.error != null -> Text("Error: ${state.error}")
            state.card == null -> Text("Card not found")
            else -> {
                val c = state.card!!
                Text("Name: ${c.name}")
                Text("ID: ${c.id}")
                Text("BaseID: ${c.baseId}")
                Text("Variant: ${c.variant ?: "<none>"}")
                Text("Image URL: ${c.imageUrl ?: "<none>"}")
            }
        }
    }
}