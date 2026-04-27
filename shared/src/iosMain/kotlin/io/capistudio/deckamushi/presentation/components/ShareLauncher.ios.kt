package io.capistudio.deckamushi.presentation.components

import androidx.compose.runtime.Composable
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.create
import platform.Foundation.writeToURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow


@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@Composable
actual fun rememberShareLauncher(onComplete: () -> Unit): (String) -> Unit {
    return { content ->
        val fileName = "deckamushi_backup.json"
        val filePath = NSTemporaryDirectory() + fileName
        val fileURL = NSURL.fileURLWithPath(filePath)

        val bytes = content.encodeToByteArray()
        bytes.usePinned { pinned ->
            NSData.create(
                bytes = pinned.addressOf(0),
                length = bytes.size.toULong()
            )
        }.writeToURL(
            url = fileURL,
            atomically = true,
        )

        val controller = UIApplication.sharedApplication
            .windows.mapNotNull { it as? UIWindow }
            .firstOrNull { it.keyWindow }
            ?.rootViewController
        val activityVC = UIActivityViewController(
            activityItems = listOf(fileURL),
            applicationActivities = null
        )
        activityVC.completionWithItemsHandler = { _, completed, _, _ ->
            if (completed) onComplete()
        }

        controller?.presentViewController(activityVC, animated = true, completion = null)
    }
}