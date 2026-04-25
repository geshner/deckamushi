package io.capistudio.deckamushi.presentation.collection

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.capistudio.deckamushi.domain.model.CardSummary
import io.capistudio.deckamushi.domain.model.OwnedCard

// 1. Data class for Preview
data class CollectionPreviewState(
    val state: CollectionContract.State,
    val cards: List<CardSummary>
)

// 2. Provider implementation
class CollectionStateProvider : PreviewParameterProvider<CollectionPreviewState> {
    override val values = sequenceOf(
        CollectionPreviewState(
            state = CollectionContract.State(totalCount = 2),
            cards = listOf(
                CardSummary(
                    id = "1",
                    name = "Luffy",
                    imageUrl = "",
                    ownedCount = 2,
                    variant = null
                ),
                CardSummary(
                    id = "2",
                    name = "Zoro",
                    imageUrl = "",
                    ownedCount = 1,
                    variant = "p1"
                ),
                CardSummary(
                    id = "3",
                    name = "Sanji",
                    imageUrl = "",
                    ownedCount = 1,
                    variant = "r1"
                )
            )
        ),
        CollectionPreviewState(
            state = CollectionContract.State(error = "Failed to load cards"),
            cards = emptyList()
        )
    )
}
