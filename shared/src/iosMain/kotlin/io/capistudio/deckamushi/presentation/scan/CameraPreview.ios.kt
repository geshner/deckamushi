package io.capistudio.deckamushi.presentation.scan

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.AVFoundation.AVCaptureConnection
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureOutput
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureSessionPresetHigh
import platform.AVFoundation.AVCaptureVideoDataOutput
import platform.AVFoundation.AVCaptureVideoDataOutputSampleBufferDelegateProtocol
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVMediaTypeVideo
import platform.CoreMedia.CMSampleBufferRef
import platform.UIKit.UIView
import platform.Vision.VNImageRequestHandler
import platform.Vision.VNRecognizeTextRequest
import platform.Vision.VNRecognizedText
import platform.Vision.VNRecognizedTextObservation
import platform.Vision.VNRequestTextRecognitionLevelFast
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_global_queue
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_queue_create

@OptIn(ExperimentalForeignApi::class)
private class TextScanDelegate(
    private val onTextDetected: (String) -> Unit,
) : NSObject(), AVCaptureVideoDataOutputSampleBufferDelegateProtocol {

    override fun captureOutput(
        output: AVCaptureOutput,
        didOutputSampleBuffer: CMSampleBufferRef?,
        fromConnection: AVCaptureConnection,
    ) {
        val buffer = didOutputSampleBuffer ?: return

        val request = VNRecognizeTextRequest { req, _ ->
            @Suppress("UNCHECKED_CAST") val observations =
                req?.results as? List<VNRecognizedTextObservation> ?: return@VNRecognizeTextRequest

            val text = observations.filter { obs ->
                obs.boundingBox.useContents {
                    origin.x >= 0.15 &&
                            (origin.x + size.width) <= 0.85 &&
                            origin.y >= 0.35 &&
                            (origin.y + size.height) <= 0.65
                }
            }.mapNotNull { obs ->
                (obs.topCandidates(1u).firstOrNull() as? VNRecognizedText)?.string
            }
                .joinToString(" ")

            if (text.isNotBlank()) {
                dispatch_async(dispatch_get_main_queue()) {
                    onTextDetected(text)
                }
            }
        }
        request.recognitionLevel = VNRequestTextRecognitionLevelFast

        VNImageRequestHandler(
            cMSampleBuffer = buffer,
            options = emptyMap<Any?, Any?>()
        ).performRequests(listOf(request), null)
    }
}


@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun CameraPreview(
    modifier: Modifier,
    onTextDetected: (String) -> Unit,
) {
    val delegate = remember { TextScanDelegate(onTextDetected) }

    val session = remember {
        AVCaptureSession().also { session ->
            session.sessionPreset = AVCaptureSessionPresetHigh

            val device = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo) ?: return@also
            val input =
                AVCaptureDeviceInput.deviceInputWithDevice(device, null) ?: return@also

            if (session.canAddInput(input)) session.addInput(input)

            val output = AVCaptureVideoDataOutput().apply {
                alwaysDiscardsLateVideoFrames = true
                setSampleBufferDelegate(
                    delegate, dispatch_queue_create("scan.queue", null)
                )
            }
            if (session.canAddOutput(output)) session.addOutput(output)
        }
    }

    val previewLayer = remember {
        AVCaptureVideoPreviewLayer(session = session).apply {
            videoGravity = AVLayerVideoGravityResizeAspectFill
        }
    }

    DisposableEffect(Unit) {
        dispatch_async(dispatch_get_global_queue(0, 0u)) {
            session.startRunning()
        }
        onDispose {
            dispatch_async(dispatch_get_global_queue(0, 0u)) {
                session.stopRunning()
            }
        }
    }

    UIKitView(factory = {
            UIView().also { view ->
                view.layer.addSublayer(previewLayer)
            }
        }, modifier = modifier.fillMaxSize(), update = { view ->
            previewLayer.setFrame(view.bounds)
        }, properties = UIKitInteropProperties(isInteractive = true, isNativeAccessibilityEnabled = true))
}