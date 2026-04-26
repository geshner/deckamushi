package io.capistudio.deckamushi.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.capistudio.deckamushi.presentation.theme.DeckamushiPreview
import io.capistudio.deckamushi.presentation.theme.Dimensions.paddingSmall
import io.capistudio.deckamushi.presentation.theme.ThemePreviews

@Composable
fun OwnedBadge(
    ownedCount: Int? = null,
    modifier: Modifier = Modifier,
) {
    if (ownedCount == null || ownedCount <= 0) return
    Surface(
        color = MaterialTheme.colorScheme.tertiary,
        shape = CircleShape,
        modifier = modifier.padding(paddingSmall),
        border = BorderStroke(
            1.5.dp,
            MaterialTheme.colorScheme.background.copy(alpha = 0.8f)
        ),
        contentColor = MaterialTheme.colorScheme.onTertiary,
        shadowElevation = 6.dp
    ) {

        Text(
            text = "x${ownedCount}",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@ThemePreviews
@Composable
fun OwnedBadgePreview() {
    DeckamushiPreview {
        OwnedBadge(ownedCount = 10)
    }
}
