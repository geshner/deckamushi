package io.capistudio.deckamushi.presentation.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import io.capistudio.deckamushi.domain.model.Card
import io.capistudio.deckamushi.presentation.cards.CardsListContract.Action
import io.capistudio.deckamushi.presentation.components.CardGrid
import io.capistudio.deckamushi.presentation.components.CardGridItem
import io.capistudio.deckamushi.presentation.theme.Dimensions.paddingLarge
import io.capistudio.deckamushi.presentation.theme.Dimensions.paddingSmall
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun CardListScreen(
    state: CardsListContract.State,
    onAction: (Action) -> Unit,
    pagingItems: LazyPagingItems<Card>,
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
        modifier = Modifier.fillMaxSize().padding(horizontal = paddingSmall)
    ) {
        OutlinedTextField(
            value = state.queryDraft,
            onValueChange = { onAction(Action.QueryChanged(it)) },
            label = { Text("Search by name") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onAction(Action.SearchClicked)
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(paddingLarge))

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
                        onClick = { onAction(Action.CardClicked(it.id)) }
                    )
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
