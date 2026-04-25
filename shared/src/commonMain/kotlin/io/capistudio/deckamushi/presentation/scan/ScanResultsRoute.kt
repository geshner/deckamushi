package io.capistudio.deckamushi.presentation.scan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.capistudio.deckamushi.presentation.components.CollectEffects
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Route wrapper for scan-result disambiguation.
 *
 * `baseId` is enough to load all candidate variants after a scan, then this route only needs to
 * forward the selected concrete card id to detail navigation.
 */
@Composable
fun ScanResultsRoute(
    baseId: String,
    onNavigateToDetail: (String) -> Unit,
) {
    val vm: ScanResultsViewModel = koinViewModel(parameters = { parametersOf(baseId) })
    val state by vm.state.collectAsState()

    CollectEffects(vm.effects) { effect ->
        when (effect) {
            is ScanResultsContract.Effect.NavigateToDetail ->
                onNavigateToDetail(effect.cardId)
        }
    }

    ScanResultScreen(
        state = state,
        onAction = vm::dispatch
    )
}