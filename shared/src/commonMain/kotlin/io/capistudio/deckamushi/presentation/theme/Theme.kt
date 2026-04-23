package io.capistudio.deckamushi.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

//Grand Line Noir Dark Theme
val DarkColorScheme = darkColorScheme(
    primary = NoirPrimaryBlue,              // "Beacon Blue"
    onPrimary = NoirDeepNavy,

    primaryContainer = NoirDeepHarbor,      // "Deep Harbor"
    onPrimaryContainer = NoirBrightHarbor,

    secondary = NoirSecondaryBlue,          // "Tidal Azure"
    onSecondary = NoirDeepNavy,

    secondaryContainer = NoirCoralCave,     // "Coral Cave"
    onSecondaryContainer = Color(0xFFC2E8FF),

    tertiary = GrandGold,                   // "Pirate's Gold"
    onTertiary = NoirDeepNavy,

    tertiaryContainer = NoirSunkenTreasure, // "Sunken Treasure"
    onTertiaryContainer = GrandGoldContainer,

    background = NoirDeepNavy,              // "The Great Abyss"
    onBackground = NoirTextDark,

    surface = NoirSurfaceNavy,              // "Deck Planking"
    onSurface = NoirTextDark,

    surfaceVariant = NoirMistShroud,        // "Mist Shroud"
    onSurfaceVariant = Color(0xFFC3C7CF),

    outline = NoirIronAnchor,               // "Iron Anchor"

    error = GlossyRed,                      // "Blood Tide"
    onError = Color.White
)

//Grand Line Noir Light Theme
val LightColorScheme = lightColorScheme(
    primary = NoirPrimaryLight,
    onPrimary = Color.White,

    primaryContainer = NoirBrightHarbor,
    onPrimaryContainer = NoirTidalAbyss,

    secondary = NoirSecondaryLight,
    onSecondary = Color.White,

    tertiary = GrandGoldLight,
    onTertiary = Color.White,

    background = NoirBackgroundLight,
    onBackground = NoirTextLight,

    surface = NoirSurfaceLight,
    onSurface = NoirTextLight,

    surfaceVariant = NoirMistMist,
    onSurfaceVariant = NoirMistShroud,

    outline = NoirLightAnchor,

    error = GlossyRed,
    onError = Color.White
)
@Composable
fun DeckamushiTheme(
    content: @Composable () -> Unit,
) {
    val colorScheme = if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = deckamushiTypography(),
        content = content
    )
}
