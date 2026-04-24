package io.capistudio.deckamushi.presentation.scan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.capistudio.deckamushi.presentation.components.RemoteImage
import io.capistudio.deckamushi.presentation.theme.Dimensions.CARD_ASPECT_RATIO
import io.capistudio.deckamushi.presentation.theme.Dimensions.CARD_GRID_COLUMNS
import io.capistudio.deckamushi.presentation.theme.Dimensions.paddingSmall

@Composable
fun ScanResultScreen(
    state: ScanResultsContract.State,
    onAction: (ScanResultsContract.Action) -> Unit,
) {

    LaunchedEffect(Unit) {
        onAction(ScanResultsContract.Action.OnStart)
    }

    when {
        state.isLoading -> CircularProgressIndicator()
        state.error != null -> Text(
            text = state.error,
            color = MaterialTheme.colorScheme.error
        )
        else -> LazyVerticalGrid(
            columns = GridCells.Fixed(CARD_GRID_COLUMNS),
            contentPadding = PaddingValues(paddingSmall),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                state.cards,
                key = { it.id }
            ) { card ->
                RemoteImage(
                    url = card.imageUrl,
                    contentDescription = card.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(CARD_ASPECT_RATIO)
                        .clip(MaterialTheme.shapes.medium)
                        .padding(paddingSmall)
                        .clickable(true) {
                            onAction(ScanResultsContract.Action.CardClicked(card.id))
                        }
                )
            }
        }
    }

}