package io.capistudio.deckamushi.presentation.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberFilePicker(onResult: (String) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        val content = context.contentResolver
            .openInputStream(uri)
            ?.bufferedReader()
            ?.readText() ?: return@rememberLauncherForActivityResult
        onResult(content)
    }
    return { launcher.launch(arrayOf("application/json", "text/plain")) }
}