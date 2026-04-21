package io.capistudio.deckamushi.presentation.scan

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ScanScreen(
    state: ScanContract.State,
    onAction: (ScanContract.Action) -> Unit,
    cameraPreview: @Composable (onTextDetected: (String) -> Unit) -> Unit,
) {

    LaunchedEffect(Unit) {
        onAction(ScanContract.Action.OnStart)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isScanning) {
            //camera preview fills the whole background
            cameraPreview { text ->
                onAction(ScanContract.Action.OnRawTextDetected(text))
            }
        }

        // Crop overlay hint
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .aspectRatio(4f)
                .align(Alignment.Center)
                .border(
                    width = 2.dp,
                    color = Color.White,
                    shape = MaterialTheme.shapes.medium
                )
        )

        if (state.isProcessing) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        if (!state.permissionGranted) {
            Text(
                text = "Camera permission required",
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        }
    }

}