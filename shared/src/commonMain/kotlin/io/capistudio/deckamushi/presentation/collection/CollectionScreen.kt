package io.capistudio.deckamushi.presentation.collection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import io.capistudio.deckamushi.presentation.collection.CollectionContract.Action
import io.capistudio.deckamushi.presentation.components.RemoteImage


@Composable
fun CollectionScreen(
    state: CollectionContract.State,
    onAction: (Action) -> Unit,
) {
    val listState = rememberLazyListState()
    val lastLoadMoreRequestedAtSize = rememberSaveable { mutableIntStateOf(-1) }


    LaunchedEffect(Unit) {
        // Override any restored position.
        listState.scrollToItem(0)
        onAction(Action.OnStart)
    }

//    LaunchedEffect(state.isSearching) {
//        if (state.isSearching) listState.scrollToItem(0)
//    }

    val shouldLoadMore by remember {
        derivedStateOf {
//            if (state.isSearching || state.isAppending || state.endReached) return@derivedStateOf false
            if (state.isAppending || state.endReached) return@derivedStateOf false
            if (state.error != null) return@derivedStateOf false
            if (state.cards.isEmpty()) return@derivedStateOf false

            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                ?: return@derivedStateOf false
            val lastCardIndex = state.cards.lastIndex
            val lastVisibleCardIndex = minOf(lastVisible, lastCardIndex) // ignore footer if visible

            val prefetchDistance = 10
            lastVisibleCardIndex >= (lastCardIndex - prefetchDistance).coerceAtLeast(0)
        }
    }

    LaunchedEffect(shouldLoadMore, state.cards.size) {
        if (!shouldLoadMore) return@LaunchedEffect
        if (lastLoadMoreRequestedAtSize.intValue == state.cards.size) return@LaunchedEffect

        lastLoadMoreRequestedAtSize.intValue = state.cards.size
        onAction(Action.LoadMore)
    }

    Scaffold(
        modifier = Modifier.safeContentPadding(),
        topBar = {
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
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = "Cards ${state.cards.size}${state.totalCount?.let { " / $it" } ?: ""}",
                style = MaterialTheme.typography.titleMedium,
            )

            state.error?.let {
                Text("Error: $it", color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                state = listState
            ) {
                items(state.cards, key = { it.id }) { card ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable(true) {
                                onAction(Action.CardClicked(card.id))
                            }
                    ) {
                        RemoteImage(
                            url = card.imageUrl,
                            contentDescription = card.name,
                            modifier = Modifier
                                .height(70.dp)
                                .aspectRatio(0.716f)
                                .clip(MaterialTheme.shapes.medium)
                        )
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = card.name
                        )
                    }
                }

                if (state.cards.isNotEmpty() || state.isAppending) {
                    item(key = "footer") {
                        Spacer(Modifier.height(8.dp))

                        if (state.isAppending) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.width(64.dp),
                                    color = MaterialTheme.colorScheme.secondary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}
