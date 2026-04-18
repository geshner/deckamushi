package io.capistudio.deckamushi.presentation.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * A base class for implementing the Model-View-Intent (MVI) architectural pattern.
 *
 * This class manages the state and side effects of a UI component, providing a unidirectional
 * data flow.
 *
 * @param S The type representing the UI state.
 * @param A The type representing UI actions or intents.
 * @param E The type representing one-time side effects (e.g., navigation, toasts).
 * @property initialState The starting state of the component.
 * @property scope The [CoroutineScope] in which MVI operations and processing occur.
 */
abstract class Mvi<S: Any, A: Any, E: Any>(
    initialState: S,
    protected val scope: CoroutineScope = MainScope(),
) {
    private val stateMutex = Mutex()

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    // replay = 0 because effects should not re-fire on recomposition/new collector.
    // extraBufferCapacity gives us a little burst tolerance (like rapid toasts).
    private val _effects = MutableSharedFlow<E>(
        replay = 0,
        extraBufferCapacity = 32
    )
    val effects: SharedFlow<E> = _effects.asSharedFlow()

    /** Single entry point for UI actions. */
    fun dispatch(action: A) {
        scope.launch {
            stateMutex.withLock {
                handleAction(action)
            }
        }
    }
    /** Handle actions sequentially (deterministic). */
    protected abstract suspend fun handleAction(action: A)

    /** Reducer helper. */
    protected fun setState(reducer: S.() -> S) {
        _state.update { it.reducer() }
    }

    /**
     * Effect helper (one-shot).
     * Using emit() means we don't drop effects under pressure.
     */
    protected suspend fun emitEffect(effect: E) {
        _effects.emit(effect)
    }

    /**
     * Non-suspending alternative if you ever want it:
     * returns false if buffer is full.
     */
    protected fun tryEmitEffect(effect: E): Boolean {
        return _effects.tryEmit(effect)
    }
}
