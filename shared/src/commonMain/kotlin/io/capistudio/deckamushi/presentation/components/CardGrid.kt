package io.capistudio.deckamushi.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.capistudio.deckamushi.presentation.theme.Dimensions.CARD_GRID_COLUMNS
import io.capistudio.deckamushi.presentation.theme.Dimensions.paddingSmall

@Composable
fun CardGrid(
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    content: LazyGridScope.() -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(CARD_GRID_COLUMNS),
        contentPadding = PaddingValues(paddingSmall),
        state = state,
        content = content,
        modifier = modifier,
    )
}