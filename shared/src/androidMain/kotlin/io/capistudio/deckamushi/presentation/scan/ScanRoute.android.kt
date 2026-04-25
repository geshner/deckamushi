package io.capistudio.deckamushi.presentation.scan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import io.capistudio.deckamushi.presentation.components.CollectEffects
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun ScanRoute(
    showSnackbar: (String) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToResults: (String) -> Unit,
) {
    val vm: ScanViewModel = koinViewModel()
    val state by vm.state.collectAsState()

    val cameraPermission = rememberPermissionState(
        android.Manifest.permission.CAMERA
    ) { granted ->
        vm.dispatch(ScanContract.Action.OnPermissionResult(granted))
    }


    CollectEffects(vm.effects) { effect ->
        when (effect) {
            is ScanContract.Effect.NavigateToDetail ->
                onNavigateToDetail(effect.cardId)

            is ScanContract.Effect.NavigateToResults ->
                onNavigateToResults(effect.baseId)

            is ScanContract.Effect.ShowMessage ->
                showSnackbar(effect.text)

            is ScanContract.Effect.RequestCameraPermission -> {
                if (cameraPermission.status.isGranted) {
                    vm.dispatch(ScanContract.Action.OnPermissionResult(true))
                } else {
                    cameraPermission.launchPermissionRequest()
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