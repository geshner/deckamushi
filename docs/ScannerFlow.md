# Scanner Flow

This document describes the **current** scanner implementation in Deckamushi.

It is implementation-focused and based on the current Android/iOS scanner code paths.

## Summary

The scanner flow is currently Android-first and works like this:

1. User opens `Scanner` from `Home`
2. Camera permission is requested on Android
3. Camera frames are analyzed with ML Kit text recognition
4. OCR text is normalized into a candidate base id
5. The same id must be detected in 3 consecutive matches
6. The app looks up variants by `base_id`
7. Navigation outcome:
   - one variant -> `CardDetail`
   - multiple variants -> `ScanResults`
   - no match -> snackbar message

## Entry Points and Routes

Current scanner-related routes in shared navigation:

- `Screen.Scanner`
- `Screen.ScanResults(baseId: String)`

Current entry point:

- `Home -> Scanner`

Current scanner outcomes:

- `Scanner -> CardDetail` (single matching variant)
- `Scanner -> ScanResults -> CardDetail` (multiple variants)

## Platform Status

### Android
Scanner is implemented.

- Camera permission is requested at runtime
- Camera preview and analysis run via CameraX
- OCR runs with ML Kit Text Recognition
- Results are normalized and matched in `ScanViewModel`

### iOS
Scanner route exists but is currently placeholder behavior.

- `ScanRoute.ios.kt` renders `CameraPreview`
- `CameraPreview.ios.kt` shows: `Scanner not available on this platform`
- No iOS camera/OCR pipeline is implemented yet

## Permission Flow (Android)

Permission behavior is driven by `ScanContract` + `ScanRoute.android.kt`.

1. `ScanScreen` dispatches `Action.OnStart`
2. `ScanViewModel` emits `Effect.RequestCameraPermission` if permission is not granted
3. `ScanRoute.android.kt` calls `rememberPermissionState` and launches the permission request
4. Permission result dispatches `Action.OnPermissionResult(granted)`
5. If granted, scanner state enables live scanning

Manifest declarations used:

- `android.permission.CAMERA`
- `uses-feature android.hardware.camera` with `required=false`

## Android OCR Pipeline

The current Android pipeline is implemented in `CameraPreview.android.kt`:

1. CameraX binds `Preview` + `ImageAnalysis` together on the back camera
2. Analyzer processes latest frames (`STRATEGY_KEEP_ONLY_LATEST`)
3. Each frame is sent to ML Kit (`TextRecognition`)
4. Text blocks are filtered to a centered crop zone
5. Cropped text is passed to `onTextDetected(...)`
6. `ScanScreen` forwards text to `Action.OnRawTextDetected`

Additional behavior:

- A centered overlay box on `ScanScreen` visually indicates the intended read zone
- `isProcessing` shows progress indicator while DB lookup is in progress
- Scanning pauses during cooldown to reduce duplicate triggers

## OCR Normalization and Matching Rules

Normalization and matching are handled in `ScanViewModel.extractAndNormalize(...)`.

### Candidate extraction

Current loose regex:

- `[A-Z]{1,3}\\d{0,2}-\\d{3}`

Input text is uppercased and sanitized to alphanumeric plus `-` before regex search.

### Supported base id patterns

Current logic is designed for:

- `LLDD-DDD`
- `L-DDD`
- `LLLDD-DDD`

### Position-aware normalization

Current correction rules are position-based:

- Post-dash section is always digits: `O -> 0`, `I -> 1`
- Pre-dash section:
  - length `== 1` (L-DDD): letter-only correction (`0 -> O`, `1 -> I`)
  - otherwise (LLDD or LLLDD):
    - leading part treated as letters (`0 -> O`, `1 -> I`)
    - last two chars treated as digits (`O -> 0`, `I -> 1`)

This avoids global replacements that could corrupt valid ids.

## Debounce and Cooldown

The scanner uses two stabilization steps in `ScanViewModel`:

### 3-frame threshold

A candidate id must match the previous normalized id for 3 consecutive detections before lookup starts.

### 1.5 second cooldown

After a lookup result is emitted, scanner enters cooldown:

- `isScanning = false` immediately
- delay `1500ms`
- scanner resumes with `isScanning = true`

This reduces repeated navigations and duplicate feedback.

## Result Routing

After threshold passes, `lookupCard(baseId)` calls `GetCardsByBaseIdUseCase`.

Current routing behavior:

- `cards.isEmpty()` -> `Effect.ShowMessage("Card not found: <baseId>")`
- `cards.size == 1` -> `Effect.NavigateToDetail(cardId)`
- `cards.size > 1` -> `Effect.NavigateToResults(baseId)`

## Scan Results Screen

`ScanResultsRoute` loads all variants by `baseId` and renders `ScanResultScreen`.

Current behavior:

- fetch variants in `OnStart`
- render card images in a 3-column grid
- tap a card to navigate to `CardDetail`

This is the disambiguation step for shared base ids with multiple variants.

## Back Behavior After Scanning

When `CardDetail` is opened from scan flow, it receives `fromScan = true`.

`CardDetailViewModel` tracks `quantityChanged`.

Current back behavior:

- if `fromScan == true` and quantity changed:
  - emits `NavigateBackSkipScanResults`
  - navigation pops directly back to `Scanner`
- otherwise:
  - normal back behavior

This works for:

- app bar back button
- system/gesture back on Android (via `PlatformBackHandler`)

## Known Limitations

Current scanner-specific limitations:

- iOS scanner is placeholder-only (no camera/OCR pipeline yet)
- OCR quality can still vary by lighting, angle, and print quality
- matching depends on currently supported base id patterns
- scanner flow is tuned for speed + practical accuracy, not guaranteed perfect recognition

## Scope of This Document

Update this document whenever any of these change:

- scanner routes or entry points
- permission behavior
- OCR pipeline implementation
- normalization rules or supported id patterns
- debounce/cooldown timings
- scan result routing
- iOS scanner implementation status

