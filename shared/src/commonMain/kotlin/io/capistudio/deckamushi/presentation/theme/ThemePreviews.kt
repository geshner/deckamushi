package io.capistudio.deckamushi.presentation.theme

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview


@Preview(name = "Light", showSystemUi = true, uiMode = UI_MODE_NIGHT_NO, device = "id:pixel_7_pro")
@Preview(name = "Dark", showSystemUi = true, uiMode = UI_MODE_NIGHT_YES, device = "id:pixel_7_pro")
annotation class ThemePreviewsWithSystemUI

@Preview(name = "Light", showSystemUi = false, uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Dark", showSystemUi = false, uiMode = UI_MODE_NIGHT_YES)
annotation class ThemePreviews

@Composable
fun DeckamushiPreview(content: @Composable () -> Unit) {
    GrandLineTheme {
        Surface {
            content()
        }
    }
}