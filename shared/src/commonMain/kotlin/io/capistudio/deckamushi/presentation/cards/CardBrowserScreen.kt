package io.capistudio.deckamushi.presentation.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CardBrowserScreen(
    viewModel: CardsBrowserViewModel,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadInitial()
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

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.cards, key = { it.id }) { card ->
                    Text(card.name)
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { viewModel.loadMore() },
                enabled = !state.isLoading,
            ) {
                Text("Load more")
            }
        }
    }
}