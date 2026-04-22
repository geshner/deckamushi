package io.capistudio.deckamushi.presentation.scan

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Platform camera preview surface for the scanner feature.
 *
 * The platform implementation owns camera lifecycle and OCR integration, then reports recognized
 * text upward via `onTextDetected` so shared code can apply project-specific matching rules.
 */
@Composable
expect fun CameraPreview(
    modifier: Modifier = Modifier,
    onTextDetected: (String) -> Unit,
)
