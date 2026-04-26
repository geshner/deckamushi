package io.capistudio.deckamushi.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * GRAND LINE NOIR - COLOR PALETTE
 *
 * This file contains the primitive color definitions for the Sovereign Tide design system.
 */

// --- DARK THEME PRIMITIVES (The Sovereign Tide) ---
val NoirDeepNavy = Color(0xFF001428)      // The Great Abyss (Root Background)
val NoirSurfaceNavy = Color(0xFF001F3D)   // Deck Planking (Elevated Surfaces)
val NoirPrimaryBlue = Color(0xFFa7c8ff)    // Beacon Blue (Active/Primary Text)
val NoirSecondaryBlue = Color(0xFF60BFF5)  // Tidal Azure (Sub-navigation & Accents)

// --- LIGHT THEME PRIMITIVES (The Coastal Mist) ---
val NoirBackgroundLight = Color(0xFFF0F4F8)
val NoirSurfaceLight = Color(0xFFFFFFFF)
val NoirPrimaryLight = Color(0xFF005FB0)
val NoirSecondaryLight = Color(0xFF00668B)

// --- SHARED ACCENTS & FUNCTIONAL COLORS ---
val GrandGold = Color(0xFFFFD700)          // Pirate's Gold (High-value Accents)
val GrandGoldContainer = Color(0xFFFFE08D)
val GlossyRed = Color(0xFFD70000)          // Blood Tide (Error / Destruction)
val GlossyRedContainer = Color(0xFF930002)

// --- CONTAINER & VARIANT SLOTS (Dark) ---
val NoirDeepHarbor = Color(0xFF004786)      // Primary Container
val NoirBrightHarbor = Color(0xFFD3E4FF)    // On Primary Container
val NoirCoralCave = Color(0xFF004A77)       // Secondary Container
val NoirMistShroud = Color(0xFF43474E)      // Surface Variant
val NoirIronAnchor = Color(0xFF8D9199)      // Outline

// --- NEUTRAL TEXT SCALE ---
val NoirTextDark = Color(0xFFE2E2E6)
val NoirTextMuted = Color(0xFFC3C7CF)
val NoirTextLight = Color(0xFF001428)

// --- NAMED CONSTANTS (extracted from inline theme values) ---
val NoirSunkenTreasure    = Color(0xFF594400)   // tertiaryContainer Dark
val NoirTidalMist         = Color(0xFFC2E8FF)   // onSecondaryContainer Dark
val NoirGoldSeal          = Color(0xFF765B00)   // tertiary Light
val NoirAntiqueParchment  = Color(0xFF251A00)   // onTertiaryContainer Light

// --- SURFACE CONTAINER HIERARCHY (Dark) ---
val NoirSurfaceContainerLowestDark  = Color(0xFF001021)
val NoirSurfaceContainerLowDark     = Color(0xFF00172E)
val NoirSurfaceContainerDark        = Color(0xFF001F3D)
val NoirSurfaceContainerHighDark    = Color(0xFF00274D)
val NoirSurfaceContainerHighestDark = Color(0xFF00305C)
val NoirSurfaceBrightDark           = Color(0xFF00407A)
val NoirSurfaceDimDark              = Color(0xFF001224)

// --- SURFACE CONTAINER HIERARCHY (Light) ---
val NoirSurfaceContainerLowestLight  = Color(0xFFFFFFFF)
val NoirSurfaceContainerLowLight     = Color(0xFFF7F9FC)
val NoirSurfaceContainerLight        = Color(0xFFF0F4F8)
val NoirSurfaceContainerHighLight    = Color(0xFFE8EDF4)
val NoirSurfaceContainerHighestLight = Color(0xFFDFE6EF)
val NoirSurfaceBrightLight           = Color(0xFFFBFDFF)
val NoirSurfaceDimLight              = Color(0xFFD5DEE8)

// --- LIGHT MODE ADDITIONS ---
val NoirLightAnchor         = Color(0xFF73777F)   // Outline (light)
val NoirOutlineVariantLight = Color(0xFFC3CAD4)
val NoirErrorLight          = Color(0xFFB00020)   // Admiral's Warning — WCAG AA on light surfaces
val NoirErrorContainerText  = Color(0xFFFFDAD6)   // onErrorContainer (dark) / errorContainer (light)
val NoirLightErrorContainer = Color(0xFF410002)   // onErrorContainer (light)
val NoirScrim               = Color(0xFF000000)
