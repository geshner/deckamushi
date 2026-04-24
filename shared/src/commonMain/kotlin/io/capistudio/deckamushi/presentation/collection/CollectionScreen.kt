package io.capistudio.deckamushi.presentation.collection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import io.capistudio.deckamushi.domain.model.OwnedCard
import io.capistudio.deckamushi.presentation.collection.CollectionContract.Action
import io.capistudio.deckamushi.presentation.components.RemoteImage
import io.capistudio.deckamushi.presentation.theme.Dimensions.CARD_ASPECT_RATIO
import io.capistudio.deckamushi.presentation.theme.Dimensions.CARD_GRID_COLUMNS
import io.capistudio.deckamushi.presentation.theme.Dimensions.paddingLarge
import io.capistudio.deckamushi.presentation.theme.Dimensions.paddingSmall
import kotlinx.coroutines.flow.distinctUntilChanged


@Composable
fun CollectionScreen(
    state: CollectionContract.State,
    pagingItems: LazyPagingItems<OwnedCard>,
    onAction: (Action) -> Unit,
) {

    val gridState = rememberLazyGridState(
        initialFirstVisibleItemIndex = state.scrollIndex,
        initialFirstVisibleItemScrollOffset = state.scrollOffset
    )

    LaunchedEffect(Unit) {
        onAction(Action.OnStart)
    }

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

        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(CARD_GRID_COLUMNS),
            contentPadding = PaddingValues(paddingSmall),
            modifier = Modifier.weight(1f)
        ) {
            items(
                count = pagingItems.itemCount,
                key = pagingItems.itemKey { it.id }
            ) { index ->
                val card = pagingItems[index]
                if (card != null) {
                    Box {

                        RemoteImage(
                            url = card.imageUrl,
                            contentDescription = card.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(CARD_ASPECT_RATIO)
                                .clip(MaterialTheme.shapes.medium)
                                .padding(paddingSmall)
                                .clickable(true) {
                                    onAction(Action.CardClicked(card.id))
                                }
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                            shape = CircleShape,
                            modifier = Modifier.align(Alignment.BottomEnd)
                                .padding(paddingSmall)
                        ) {

                            Text(
                                text = "x${card.ownedQuantity}",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // 2. Loading State (Initial)
            if (pagingItems.loadState.refresh is LoadState.Loading) {
                item {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }

            // 3. Appending State (Bottom Loading)
            if (pagingItems.loadState.append is LoadState.Loading) {
                item {
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
