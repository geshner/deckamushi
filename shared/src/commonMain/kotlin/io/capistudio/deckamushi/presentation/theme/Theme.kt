package io.capistudio.deckamushi.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import deckamushi.shared.generated.resources.Res
import deckamushi.shared.generated.resources.SpaceGrotesk_Bold
import deckamushi.shared.generated.resources.SpaceGrotesk_Medium
import deckamushi.shared.generated.resources.SpaceGrotesk_Regular
import deckamushi.shared.generated.resources.SpaceGrotesk_SemiBold
import org.jetbrains.compose.resources.Font

// ── Color Schemes ────────────────────────────────────────────────────────────

val GrandLineDarkColorScheme = darkColorScheme(
    primary              = NoirPrimaryBlue,
    onPrimary            = NoirDeepNavy,
    primaryContainer     = NoirDeepHarbor,
    onPrimaryContainer   = NoirBrightHarbor,
    inversePrimary       = NoirPrimaryLight,

    secondary            = NoirSecondaryBlue,
    onSecondary          = NoirDeepNavy,
    secondaryContainer   = NoirCoralCave,
    onSecondaryContainer = NoirTidalMist,

    tertiary             = GrandGold,
    onTertiary           = NoirDeepNavy,
    tertiaryContainer    = NoirSunkenTreasure,
    onTertiaryContainer  = GrandGoldContainer,

    background           = NoirDeepNavy,
    onBackground         = NoirTextDark,

    surface                 = NoirSurfaceNavy,
    onSurface               = NoirTextDark,
    surfaceVariant          = NoirMistShroud,
    onSurfaceVariant        = NoirTextMuted,
    surfaceTint             = NoirPrimaryBlue,
    inverseSurface          = NoirTextDark,
    inverseOnSurface        = NoirDeepNavy,
    surfaceBright           = NoirSurfaceBrightDark,
    surfaceDim              = NoirSurfaceDimDark,
    surfaceContainer        = NoirSurfaceContainerDark,
    surfaceContainerHigh    = NoirSurfaceContainerHighDark,
    surfaceContainerHighest = NoirSurfaceContainerHighestDark,
    surfaceContainerLow     = NoirSurfaceContainerLowDark,
    surfaceContainerLowest  = NoirSurfaceContainerLowestDark,

    outline        = NoirIronAnchor,
    outlineVariant = NoirMistShroud,

    error              = GlossyRed,
    onError            = NoirSurfaceLight,
    errorContainer     = GlossyRedContainer,
    onErrorContainer   = NoirErrorContainerText,

    scrim = NoirScrim,
)

val GrandLineLightColorScheme = lightColorScheme(
    primary              = NoirPrimaryLight,
    onPrimary            = NoirSurfaceLight,
    primaryContainer     = NoirBrightHarbor,
    onPrimaryContainer   = NoirDeepNavy,
    inversePrimary       = NoirPrimaryBlue,

    secondary            = NoirSecondaryLight,
    onSecondary          = NoirSurfaceLight,
    secondaryContainer   = NoirTidalMist,
    onSecondaryContainer = NoirDeepNavy,

    tertiary             = NoirGoldSeal,
    onTertiary           = NoirSurfaceLight,
    tertiaryContainer    = GrandGoldContainer,
    onTertiaryContainer  = NoirAntiqueParchment,

    background           = NoirBackgroundLight,
    onBackground         = NoirTextLight,

    surface                 = NoirSurfaceLight,
    onSurface               = NoirTextLight,
    surfaceVariant          = NoirSurfaceContainerHighestLight,
    onSurfaceVariant        = NoirMistShroud,
    surfaceTint             = NoirPrimaryLight,
    inverseSurface          = NoirDeepNavy,
    inverseOnSurface        = NoirTextDark,
    surfaceBright           = NoirSurfaceBrightLight,
    surfaceDim              = NoirSurfaceDimLight,
    surfaceContainer        = NoirSurfaceContainerLight,
    surfaceContainerHigh    = NoirSurfaceContainerHighLight,
    surfaceContainerHighest = NoirSurfaceContainerHighestLight,
    surfaceContainerLow     = NoirSurfaceContainerLowLight,
    surfaceContainerLowest  = NoirSurfaceContainerLowestLight,

    outline        = NoirLightAnchor,
    outlineVariant = NoirOutlineVariantLight,

    error              = NoirErrorLight,
    onError            = NoirSurfaceLight,
    errorContainer     = NoirErrorContainerText,
    onErrorContainer   = NoirLightErrorContainer,

    scrim = NoirScrim,
)

// ── Shapes ───────────────────────────────────────────────────────────────────

val GrandLineShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small      = RoundedCornerShape(8.dp),
    medium     = RoundedCornerShape(12.dp),  // buttons, cards
    large      = RoundedCornerShape(16.dp),  // containers
    extraLarge = RoundedCornerShape(28.dp),  // modals, bottom sheets
    // full = CircleShape — use directly for chips and pill buttons
)

// ── Typography ───────────────────────────────────────────────────────────────
// Display/Headlines: Space Grotesk Bold + Italic (nautical forward momentum)
// Titles/Body:       Manrope — add Manrope_*.ttf to composeResources/font/
//                    then swap `bodyFont` below to grandLineManropeFamily()

@Composable
private fun grandLineSpaceGroteskFamily() = FontFamily(
    Font(Res.font.SpaceGrotesk_Regular, FontWeight.Normal),
    Font(Res.font.SpaceGrotesk_Medium, FontWeight.Medium),
    Font(Res.font.SpaceGrotesk_SemiBold, FontWeight.SemiBold),
    Font(Res.font.SpaceGrotesk_Bold, FontWeight.Bold),
)

// Uncomment once Manrope font files are in composeResources/font/:
// @Composable
// private fun grandLineManropeFamily() = FontFamily(
//     Font(Res.font.Manrope_Regular, FontWeight.Normal),
//     Font(Res.font.Manrope_Medium, FontWeight.Medium),
//     Font(Res.font.Manrope_SemiBold, FontWeight.SemiBold),
//     Font(Res.font.Manrope_Bold, FontWeight.Bold),
// )

@Composable
fun grandLineTypography(): Typography {
    val displayFont = grandLineSpaceGroteskFamily()
    val bodyFont    = displayFont // swap to grandLineManropeFamily() once fonts are added

    return Typography(
        displayLarge = TextStyle(
            fontFamily = displayFont, fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic, fontSize = 57.sp, lineHeight = 64.sp, letterSpacing = (-0.25).sp
        ),
        displayMedium = TextStyle(
            fontFamily = displayFont, fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic, fontSize = 45.sp, lineHeight = 52.sp, letterSpacing = 0.sp
        ),
        displaySmall = TextStyle(
            fontFamily = displayFont, fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic, fontSize = 36.sp, lineHeight = 44.sp, letterSpacing = 0.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = displayFont, fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic, fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = displayFont, fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic, fontSize = 28.sp, lineHeight = 36.sp, letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = displayFont, fontWeight = FontWeight.SemiBold,
            fontStyle = FontStyle.Italic, fontSize = 24.sp, lineHeight = 32.sp, letterSpacing = 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily = bodyFont, fontWeight = FontWeight.Medium,
            fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = bodyFont, fontWeight = FontWeight.Medium,
            fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.15.sp
        ),
        titleSmall = TextStyle(
            fontFamily = bodyFont, fontWeight = FontWeight.Medium,
            fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = bodyFont, fontWeight = FontWeight.Normal,
            fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = bodyFont, fontWeight = FontWeight.Normal,
            fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = bodyFont, fontWeight = FontWeight.Normal,
            fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp
        ),
        labelLarge = TextStyle(
            fontFamily = displayFont, fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = displayFont, fontWeight = FontWeight.Medium,
            fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily = displayFont, fontWeight = FontWeight.Medium,
            fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp
        ),
    )
}

// ── Theme Entry Point ────────────────────────────────────────────────────────

@Composable
fun GrandLineTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) GrandLineDarkColorScheme else GrandLineLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = grandLineTypography(),
        shapes      = GrandLineShapes,
        content     = content,
    )
}