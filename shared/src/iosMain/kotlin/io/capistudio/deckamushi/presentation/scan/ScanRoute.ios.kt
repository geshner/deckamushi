package io.capistudio.deckamushi.presentation.scan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.capistudio.deckamushi.presentation.components.CollectEffects
import org.koin.compose.viewmodel.koinViewModel
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun ScanRoute(
    showSnackbar: (String) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToResults: (String) -> Unit,
) {

    val vm: ScanViewModel = koinViewModel()
    val state by vm.state.collectAsState()

    CollectEffects(vm.effects) { effect ->
        when (effect) {
            is ScanContract.Effect.NavigateToDetail ->
                onNavigateToDetail(effect.cardId)

            is ScanContract.Effect.NavigateToResults ->
                onNavigateToResults(effect.baseId)

            is ScanContract.Effect.ShowMessage ->
                showSnackbar(effect.text)

            ScanContract.Effect.RequestCameraPermission -> {
                val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
                when (status) {
                    AVAuthorizationStatusAuthorized -> {
                        vm.dispatch(ScanContract.Action.OnPermissionResult(true))
                    }

                    AVAuthorizationStatusNotDetermined -> {
                        AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
                            dispatch_async(dispatch_get_main_queue()) {
                                vm.dispatch(ScanContract.Action.OnPermissionResult(granted))
                            }
                        }
                    }

                    else -> {
                        vm.dispatch(ScanContract.Action.OnPermissionResult(false))
                    }
                }
            }
        }
    }

    ScanScreen(
        state = state,
        onAction = vm::dispatch,
        cameraPreview = { onTextDetected -> CameraPreview(onTextDetected = onTextDetected) }
    )
}