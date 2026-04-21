package io.capistudio.deckamushi.presentation.scan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ScanResultsRoute(
    baseId: String,
    onNavigateToDetail: (String) -> Unit,
) {
    val vm: ScanResultsViewModel = koinViewModel(parameters = { parametersOf(baseId) })
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.effects.collect { effect ->
            when (effect) {
                is ScanResultsContract.Effect.NavigateToDetail ->
                    onNavigateToDetail(effect.cardId)
            }
        }
    }

    ScanResultScreen(
        state = state,
        onAction = vm::dispatch
    )
}