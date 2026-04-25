package io.capistudio.deckamushi.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

/**
 * A Composable that collects side effects from the given [effects] Flow and
 * executes the [onEffect] callback for each emitted value.
 *
 * This is typically used to handle one-time events (e.g., navigation, showing snackbars,
 * or triggering animations) within the UI layer, ensuring that the collection starts
 * when the component enters the composition and is canceled when it leaves.
 *
 * @param E The type of the effect.
 * @param effects The [Flow] of effects to be observed.
 */
@Composable
fun <E> CollectEffects(effects: Flow<E>, onEffect: (E) -> Unit) {
    LaunchedEffect(Unit) {
        effects.collect { effect ->
            onEffect(effect)
        }
    }
}
