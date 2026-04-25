package io.capistudio.deckamushi.presentation.collection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import io.capistudio.deckamushi.domain.model.CardSummary
import io.capistudio.deckamushi.presentation.collection.CollectionContract.Action
import io.capistudio.deckamushi.presentation.components.CardGrid
import io.capistudio.deckamushi.presentation.components.CardGridItem
import io.capistudio.deckamushi.presentation.components.OwnedBadge
import io.capistudio.deckamushi.presentation.components.ReprintBanner
import io.capistudio.deckamushi.presentation.theme.DeckamushiPreview
import io.capistudio.deckamushi.presentation.theme.Dimensions.paddingLarge
import io.capistudio.deckamushi.presentation.theme.ThemePreviewsWithSystemUI
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf


@Composable
fun CollectionScreen(
    state: CollectionContract.State,
    pagingItems: LazyPagingItems<CardSummary>,
    onAction: (Action) -> Unit,
) {

    val gridState = rememberLazyGridState(
        initialFirstVisibleItemIndex = state.scrollIndex,
        initialFirstVisibleItemScrollOffset = state.scrollOffset
    )

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.firstVisibleItemIndex to gridState.firstVisibleItemScrollOffset }
            .distinctUntilChanged()
            .collect { (index, offset) ->
                onAction(Action.ScrollPositionChanged(index, offset))
            }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        //            OutlinedTextField(
        //                value = state.queryDraft,
        //                onValueChange = { onAction(Action.QueryChanged(it)) },
        //                label = { Text("Search by name") },
        //                singleLine = true,
        //                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        //                keyboardActions = KeyboardActions(
        //                    onSearch = {
        //                        onAction(Action.SearchClicked)
        //                    }
        //                ),
        //            )

        CardGrid(
            state = gridState,
            modifier = Modifier.weight(1f)
        ) {
            items(
                count = pagingItems.itemCount,
                key = pagingItems.itemKey { it.id }
            ) { index ->
                val card = pagingItems[index]
                card?.let {
                    CardGridItem(
                        imageUrl = it.imageUrl,
                        contentDescription = it.name,
                        onClick = { onAction(CollectionContract.Action.CardClicked(it.id)) }
                    ) {
                        OwnedBadge(
                            ownedCount = card.ownedCount.toInt(),
                            modifier = Modifier.align(Alignment.BottomEnd)
                        )

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

            // 2. Loading State (Initial)
            if (pagingItems.loadState.refresh is LoadState.Loading) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }

            // 3. Appending State (Bottom Loading)
            if (pagingItems.loadState.append is LoadState.Loading) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(paddingLarge),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            // 4. Error State
            if (pagingItems.loadState.refresh is LoadState.Error) {
                item {
                    Text("Error loading cards", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@ThemePreviewsWithSystemUI
@Composable
fun CollectionScreenPreview(
    @PreviewParameter(CollectionStateProvider::class) previewData: CollectionPreviewState
) {
    val pagingItems = flowOf(PagingData.from(previewData.cards)).collectAsLazyPagingItems()

    DeckamushiPreview {
        CollectionScreen(
            state = previewData.state,
            pagingItems = pagingItems,
            onAction = {}
        )
    }
}
