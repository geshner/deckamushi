package io.capistudio.deckamushi.presentation.scan

import androidx.lifecycle.viewModelScope
import io.capistudio.deckamushi.domain.usecase.GetCardsByBaseIdUseCase
import io.capistudio.deckamushi.domain.util.DomainResult
import io.capistudio.deckamushi.presentation.mvi.Mvi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScanViewModel(
    private val getCardsByBaseIdUseCase: GetCardsByBaseIdUseCase,
) : Mvi<ScanContract.State, ScanContract.Action, ScanContract.Effect>(
    initialState = ScanContract.State()
) {

    private var lastMatchedId: String? = null
    private var consecutiveCount: Int = 0
    private var cooldownActive: Boolean = false

    // Loose — finds candidates in noisy OCR text (O and I treated as letters)
    private val idRegex = Regex("[A-Z]{1,3}\\d{0,2}-\\d{3}")

    override suspend fun handleAction(action: ScanContract.Action) {
        when (action) {
            ScanContract.Action.OnStart -> {
                if (!state.value.permissionGranted) {
                    emitEffect(ScanContract.Effect.RequestCameraPermission)
                } else {
                    setState { copy(isScanning = true) }
                }
            }

            is ScanContract.Action.OnPermissionResult -> {
                setState { copy(permissionGranted = action.granted, isScanning = action.granted) }
            }

            is ScanContract.Action.OnRawTextDetected -> {
                if (cooldownActive || state.value.isProcessing) return
                processRawText(action.text)
            }

            ScanContract.Action.BackClicked -> {
                setState { copy(isScanning = false) }
            }
        }
    }

    private suspend fun processRawText(text: String) {
        val normalized = extractAndNormalize(text) ?: run {
            resetThreshold()
            return
        }
        if (normalized == lastMatchedId) {
            consecutiveCount++
        } else {
            lastMatchedId = normalized
            consecutiveCount = 1
        }
        if (consecutiveCount >= 3) {
            resetThreshold()
            lookupCard(normalized)
        }
    }

    private suspend fun lookupCard(baseId: String) {
        setState { copy(isProcessing = true) }

        when (val result = getCardsByBaseIdUseCase(baseId)) {
            is DomainResult.Success -> {
                val cards = result.data
                when {
                    cards.isEmpty() -> {
                        applyCooldown()
                        emitEffect(ScanContract.Effect.ShowMessage("Card not found: $baseId"))
                    }

                    cards.size == 1 -> {
                        applyCooldown()
                        emitEffect(ScanContract.Effect.NavigateToDetail(cards[0].id))
                    }

                    else -> {
                        applyCooldown()
                        emitEffect(ScanContract.Effect.NavigateToResults(baseId))
                    }
                }
            }

            is DomainResult.Error -> {
                applyCooldown()
                emitEffect(ScanContract.Effect.ShowMessage("Error: ${result.message}"))
            }

            is DomainResult.Loading -> Unit
        }
        setState { copy(isProcessing = false) }
    }


    private fun extractAndNormalize(raw: String): String? {
        val upper = raw.uppercase().trim()
            .filter { it.isLetterOrDigit() || it == '-' }

        val candidate = idRegex.find(upper)?.value ?: return null

        val dashIdx = candidate.indexOf('-')
        if (dashIdx == -1) return null

        val prefix = candidate.substring(0, dashIdx)
        val suffix = candidate.substring(dashIdx + 1)

        // Post-dash: always digits — fix O→0, I→1
        val fixedSuffix = suffix.map { c ->
            when (c) {
                'O' -> '0'; 'I' -> '1'; else -> c
            }
        }.joinToString("")

        // Pre-dash: split by pattern
        val fixedPrefix = if (prefix.length == 1) {
            // L-DDD pattern: single letter, no digit zone
            prefix.map { c ->
                when (c) {
                    '0' -> 'O'; '1' -> 'I'; else -> c
                }
            }.joinToString("")
        } else {
            // LLDD or LLLDD: leading chars are letters, last 2 are digits
            val letterPart = prefix.dropLast(2)
            val digitPart = prefix.takeLast(2)
            val fixedLetters = letterPart.map { c ->
                when (c) {
                    '0' -> 'O'; '1' -> 'I'; else -> c
                }
            }.joinToString("")
            val fixedDigits = digitPart.map { c ->
                when (c) {
                    'O' -> '0'; 'I' -> '1'; else -> c
                }
            }.joinToString("")
            "$fixedLetters$fixedDigits"
        }

        return "$fixedPrefix-$fixedSuffix"
    }

    private fun resetThreshold() {
        lastMatchedId = null
        consecutiveCount = 0
    }

    private fun applyCooldown() {
        cooldownActive = true
        setState { copy(isScanning = false) } // <- stops camera immediately on match
        viewModelScope.launch {
            delay(1500L)
            cooldownActive = false
            setState { copy(isScanning = true) } // <- restores camera after cooldown
        }
    }
}