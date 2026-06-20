package io.capistudio.deckamushi.presentation.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.capistudio.deckamushi.domain.model.CartItemSummary
import io.capistudio.deckamushi.presentation.components.RemoteImage
import io.capistudio.deckamushi.presentation.theme.DeckamushiPreview
import io.capistudio.deckamushi.presentation.theme.Dimensions.CARD_ASPECT_RATIO
import io.capistudio.deckamushi.presentation.theme.Dimensions.paddingMedium
import io.capistudio.deckamushi.presentation.theme.Dimensions.paddingSmall
import io.capistudio.deckamushi.presentation.theme.ThemePreviews

private val ThumbnailWidth = 56.dp
private val QuantityButtonSize = 32.dp

@Composable
fun CartItemRow(
    item: CartItemSummary,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = paddingMedium, vertical = paddingSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(paddingMedium),
    ) {
        RemoteImage(
            url = item.imageUrl,
            contentDescription = item.name,
            modifier = Modifier
                .width(ThumbnailWidth)
                .aspectRatio(CARD_ASPECT_RATIO)
                .clip(MaterialTheme.shapes.small),
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(paddingSmall),
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(paddingSmall),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                item.variant?.let { variant ->
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        shape = MaterialTheme.shapes.extraSmall,
                    ) {
                        Text(
                            text = variant,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = paddingSmall, vertical = 2.dp),
                        )
                    }
                }

                Surface(
                    color = if (item.isNew) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    contentColor = if (item.isNew) {
                        MaterialTheme.colorScheme.onTertiary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    shape = MaterialTheme.shapes.extraSmall,
                ) {
                    Text(
                        text = if (item.isNew) "NEW" else "Own: ${item.ownedQuantity}",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = paddingSmall, vertical = 2.dp),
                    )
                }
            }
        }

        IconButton(
            onClick = onDecrement,
            modifier = Modifier
                .size(QuantityButtonSize)
                .background(MaterialTheme.colorScheme.tertiary, CircleShape),
        ) {
            Icon(
                Icons.Default.Remove,
                contentDescription = "Decrease quantity",
                tint = MaterialTheme.colorScheme.onTertiary,
                modifier = Modifier.size(18.dp),
            )
        }

        Text(
            text = "${item.cartQuantity}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = paddingSmall),
        )

        IconButton(
            onClick = onIncrement,
            modifier = Modifier
                .size(QuantityButtonSize)
                .background(MaterialTheme.colorScheme.tertiary, CircleShape),
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Increase quantity",
                tint = MaterialTheme.colorScheme.onTertiary,
                modifier = Modifier.size(18.dp),
            )
        }

        IconButton(onClick = onRemove) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Remove from cart",
                tint = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@ThemePreviews
@Composable
private fun CartItemRowPreview() {
    DeckamushiPreview {
        Column {
            CartItemRow(
                item = CartItemSummary(
                    id = "OP01-001",
                    variant = "P1",
                    name = "Monkey D. Luffy",
                    imageUrl = null,
                    cartQuantity = 2,
                    ownedQuantity = 0,
                ),
                onIncrement = {},
                onDecrement = {},
                onRemove = {},
            )
            CartItemRow(
                item = CartItemSummary(
                    id = "OP01-025",
                    variant = "C",
                    name = "Roronoa Zoro, Pirate Hunter",
                    imageUrl = null,
                    cartQuantity = 1,
                    ownedQuantity = 3,
                ),
                onIncrement = {},
                onDecrement = {},
                onRemove = {},
            )
        }
    }
}