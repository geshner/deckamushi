package io.capistudio.deckamushi.presentation.theme

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Example Typography Components - Use these in your screens instead of
 * manually specifying TextStyle.
 *
 * This keeps your code DRY and ensures consistency across the app.
 */

@Composable
fun HeadingLarge(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = deckamushiTypography().headlineLarge,
    )
}

@Composable
fun HeadingMedium(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = deckamushiTypography().headlineMedium,
    )
}

@Composable
fun TitleLarge(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = deckamushiTypography().titleLarge,
    )
}

@Composable
fun BodyLarge(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = deckamushiTypography().bodyLarge,
    )
}

@Composable
fun BodyMedium(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = deckamushiTypography().bodyMedium,
    )
}

@Composable
fun LabelLarge(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = deckamushiTypography().labelLarge,
    )
}
