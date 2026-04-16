package io.capistudio.deckamushi.presentation.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CardBrowserScreen(
    viewModel: CardsBrowserViewModel,
    onCardClick: (String) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        // Override any restored position.
        listState.scrollToItem(0)
        viewModel.loadInitial()
    }

    LaunchedEffect(state.isLoading) {
        if (state.isLoading) listState.scrollToItem(0)
    }

    val shouldLoadMore by remember(state.cards.size, state.isLoading, state.isAppending, state.endReached) {
        derivedStateOf {
            if (state.isLoading || state.isAppending || state.endReached) return@derivedStateOf false
            if (state.cards.isEmpty()) return@derivedStateOf false

            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val lastCardIndex = state.cards.lastIndex
            val lastVisibleCardIndex = minOf(lastVisible, lastCardIndex) // ignore footer if visible

            val prefetchDistance = 10
            lastVisibleCardIndex >= (lastCardIndex - prefetchDistance).coerceAtLeast(0)
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) viewModel.loadMore()
    }

    Scaffold(
        modifier = Modifier.safeContentPadding(),
        topBar = {
            OutlinedTextField(
                value = state.query,
                onValueChange = { viewModel.onQueryChanged(it) },
                label = { Text("Search by name") },
                singleLine = true,
            )
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
                            .clickable(true){
                            onCardClick(card.id)
                        }
                    ) {
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = card.name
                        )
                    }
                }

                if (state.cards.isNotEmpty().or(state.isAppending)) {
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