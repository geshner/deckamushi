package io.capistudio.deckamushi.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.capistudio.deckamushi.presentation.components.RemoteImage
import io.capistudio.deckamushi.presentation.detail.CardDetailContract.Action
import io.capistudio.deckamushi.presentation.theme.Dimensions.CARD_ASPECT_RATIO
import io.capistudio.deckamushi.presentation.theme.Dimensions.cardImageHeight
import io.capistudio.deckamushi.presentation.theme.Dimensions.paddingXLarge

@Composable
fun CardDetailScreen(
    state: CardDetailContract.State,
    onAction: (Action) -> Unit,
) {
    Column(
        modifier = Modifier.safeContentPadding()
    ) {
        when {
            state.isLoading -> Text("Loading...")
            state.error != null -> Text("Error: ${state.error}")
            state.card == null -> Text("Card not found")
            else -> {
                val c = state.card
                Box(
                    Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    RemoteImage(
                        url = c.imageUrl,
                        contentDescription = c.name,
                        modifier = Modifier
                            .height(cardImageHeight)
                            .aspectRatio(CARD_ASPECT_RATIO)
                            .clip(MaterialTheme.shapes.medium)
                    )
                }
                Text("Name: ${c.name}")
                Text("ID: ${c.id}")
                Text("BaseID: ${c.baseId}")
                Text("Variant: ${c.variant ?: "<none>"}")
                Text("Image URL: ${c.imageUrl ?: "<none>"}")

                Spacer(Modifier.height(paddingXLarge))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        enabled = state.ownedQuantity > 0,
                        onClick = { onAction(Action.DecrementOwnedClick) }) { Text("-") }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Owned: ${state.ownedQuantity}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.width(12.dp))
                    Button(onClick = { onAction(Action.IncrementOwnedClick) }) { Text("+") }

                }
            }
        }
    }
}