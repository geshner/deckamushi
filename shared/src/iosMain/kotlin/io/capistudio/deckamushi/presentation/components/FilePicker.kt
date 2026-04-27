package io.capistudio.deckamushi.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithContentsOfURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerMode
import platform.UIKit.UIDocumentPickerViewController
import platform.darwin.NSObject

@Composable
actual fun rememberFilePicker(onResult: (String) -> Unit): () -> Unit {
    val delegate = remember { DocumentPickerDelegate(onResult) }
    return {
        val controller = UIApplication.sharedApplication.keyWindow?.rootViewController
        val picker = UIDocumentPickerViewController(
            documentTypes = listOf("public.json", "public.plain-text"),
            inMode = UIDocumentPickerMode.UIDocumentPickerModeOpen
        ).apply {
            this.delegate = delegate
            allowsMultipleSelection = false
        }
        controller?.presentViewController(picker, animated = true, completion = null)
    }
}

class DocumentPickerDelegate(
    private val onResult: (String) -> Unit
) : NSObject(), UIDocumentPickerDelegateProtocol {

    @OptIn(ExperimentalForeignApi::class)
    override fun documentPicker(
        controller: UIDocumentPickerViewController,
        didPickDocumentsAtURLs: List<*>
    ) {
        val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL ?: return
        url.startAccessingSecurityScopedResource()
        val content = NSString.stringWithContentsOfURL(
            url = url,
            encoding = NSUTF8StringEncoding,
            error = null
        )
        url.stopAccessingSecurityScopedResource()
        content?.let { onResult(it) }
    }

    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {}
}