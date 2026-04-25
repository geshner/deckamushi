package io.capistudio.deckamushi.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import deckamushi.shared.generated.resources.Res
import deckamushi.shared.generated.resources.label_reprint_of
import io.capistudio.deckamushi.presentation.theme.DeckamushiPreview
import io.capistudio.deckamushi.presentation.theme.Dimensions.paddingSmall
import io.capistudio.deckamushi.presentation.theme.ThemePreviews
import org.jetbrains.compose.resources.stringResource

@Composable
fun ReprintBanner(
    originalCardBaseId: String,
    compact: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(paddingSmall))
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(paddingSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(paddingSmall, Alignment.CenterHorizontally)
    ) {
        Icon(
            imageVector = Icons.Default.Repeat,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onTertiary,
            modifier = if (compact) Modifier.size(16.dp) else Modifier
        )

        if (!compact) {
            Text(
                text = stringResource(Res.string.label_reprint_of, originalCardBaseId),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onTertiary
            )
        }
    }
}

@ThemePreviews
@Composable
fun ReprintBannerPreview(modifier: Modifier = Modifier) {
    DeckamushiPreview {
        Column {
            ReprintBanner("OB01-001")

            Spacer(Modifier.height(16.dp))
            ReprintBanner("OB01-001", true)
        }

    }
}