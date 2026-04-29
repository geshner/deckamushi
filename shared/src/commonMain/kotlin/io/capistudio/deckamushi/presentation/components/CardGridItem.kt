package io.capistudio.deckamushi.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.capistudio.deckamushi.presentation.theme.Dimensions.CARD_ASPECT_RATIO
import io.capistudio.deckamushi.presentation.theme.Dimensions.paddingSmall

@Composable
fun CardGridItem(
    imageUrl: String?,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    overlay: @Composable BoxScope.() -> Unit = {},
) {
    Box(modifier) {
        RemoteImage(
            url = imageUrl,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxWidth()
                .height(179.dp)
                .aspectRatio(CARD_ASPECT_RATIO)
                .clip(MaterialTheme.shapes.medium)
                .padding(paddingSmall)
                .clickable {onClick()}
        )
        overlay()
    }
}
