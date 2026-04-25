package io.capistudio.deckamushi.presentation.collection

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.capistudio.deckamushi.domain.model.OwnedCard

// 1. Data class for Preview
data class CollectionPreviewState(
    val state: CollectionContract.State,
    val cards: List<OwnedCard>
)

// 2. Provider implementation
class CollectionStateProvider : PreviewParameterProvider<CollectionPreviewState> {
    override val values = sequenceOf(
        CollectionPreviewState(
            state = CollectionContract.State(totalCount = 2),
            cards = listOf(
                OwnedCard(
                    id = "1",
                    baseId = "b1",
                    variant = null,
                    name = "Luffy",
                    imageUrl = null,
                    ownedQuantity = 2
                ),
                OwnedCard(
                    id = "2",
                    baseId = "b2",
                    variant = "Holo",
                    name = "Zoro",
                    imageUrl = null,
                    ownedQuantity = 1
                ),
                OwnedCard(
                    id = "3",
                    baseId = "b2",
                    variant = "Holo",
                    name = "Sanji",
                    imageUrl = null,
                    ownedQuantity = 1
                )
            )
        ),
        CollectionPreviewState(
            state = CollectionContract.State(error = "Failed to load cards"),
            cards = emptyList()
        )
    )
}
