package io.capistudio.deckamushi.presentation.scan

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.capistudio.deckamushi.presentation.components.CardGrid
import io.capistudio.deckamushi.presentation.components.CardGridItem
import io.capistudio.deckamushi.presentation.components.ReprintBanner

@Composable
fun ScanResultScreen(
    state: ScanResultsContract.State,
    onAction: (ScanResultsContract.Action) -> Unit,
) {
    when {
        state.isLoading -> CircularProgressIndicator()
        state.error != null -> Text(
            text = state.error,
            color = MaterialTheme.colorScheme.error
        )

        else -> {
            CardGrid(modifier = Modifier.fillMaxSize()) {
                items(state.cards, key = { it.id }) { card ->
                    CardGridItem(
                        imageUrl = card.imageUrl,
                        contentDescription = card.name,
                        onClick = { onAction(ScanResultsContract.Action.CardClicked(card.id)) },
                    ) {
                        if (card.isReprint) {
                            ReprintBanner(
                                originalCardBaseId = card.id,
                                compact = true,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 24.dp)
                            )
                        }
                    }
                }
            }
        }
    }

}