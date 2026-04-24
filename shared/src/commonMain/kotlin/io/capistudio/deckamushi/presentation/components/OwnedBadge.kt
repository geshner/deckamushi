package io.capistudio.deckamushi.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.capistudio.deckamushi.presentation.theme.DeckamushiTheme
import io.capistudio.deckamushi.presentation.theme.Dimensions.paddingSmall

@Composable
fun OwnedBadge(
    ownedQuantity: Int? = null,
    modifier: Modifier = Modifier,
) {
    ownedQuantity ?: return
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.9f),
        shape = CircleShape,
        modifier = modifier.padding(paddingSmall)
    ) {

        Text(
            text = "x${ownedQuantity}",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Preview
@Composable
fun OwnedBadgePreview() {
    DeckamushiTheme {
        OwnedBadge(ownedQuantity = 10)
    }
}
