# One Piece Card Collection App — Build Steps

## Phase 1 — Gradle Scaffold
- [ ] Create `settings.gradle.kts` (project name, module include)
- [ ] Create root `build.gradle.kts` (plugin declarations)
- [ ] Create `gradle/libs.versions.toml` (all dependency versions)
- [ ] Create `composeApp/build.gradle.kts` (KMP targets, dependencies, SQLDelight config)
- [ ] Verify Android + iOS targets resolve without errors

## Phase 2 — SQLDelight Schema
- [ ] Create `Card.sq` — cards table + queries (search, filter by color, filter by rarity, get by variant_id, get all variants by base_id)
- [ ] Create `Collection.sq` — collection table + queries (upsert, delete, get quantity, get all owned)
- [ ] Run code generation and verify Kotlin classes are produced

## Phase 3 — Data Layer
- [ ] Define `CardColor` enum with bitmask values
- [ ] Define `Rarity` enum with id values
- [ ] Define `Card` Kotlin data class (all fields, uses enums)
- [ ] Define `CollectionEntry` Kotlin data class
- [ ] Create `DatabaseDriverFactory` (`expect/actual` — `AndroidSqliteDriver` / `NativeSqliteDriver`)
- [ ] Create `CardRepository` (search, filter by color bitmask, filter by rarity, get by variant_id, get variants by base_id)
- [ ] Create `CollectionRepository` (add, remove, update quantity, get all owned, get quantity for variant)
- [ ] Create `JsonSeeder` — reads bundled JSON on first launch, normalizes rarity and color, inserts via `INSERT OR IGNORE`

## Phase 4 — Scanner Pipeline
- [ ] Create `expect/actual` `CameraController` interface
- [ ] Implement Android `CameraController` using CameraX
- [ ] Implement iOS `CameraController` using AVFoundation
- [ ] Create ROI crop utility (bottom-right 20% of frame)
- [ ] Create `expect/actual` `OcrEngine` interface
- [ ] Implement Android `OcrEngine` using `tesseract4android` (whitelist + PSM 7)
- [ ] Implement iOS `OcrEngine` using `SwiftyTesseract` (whitelist + PSM 7)
- [ ] Bundle `eng.traineddata` as a resource
- [ ] Create `CardIdExtractor` — regex `[A-Z][A-Z0-9]*-\d{3}` applied to OCR output
- [ ] Create `FuzzyMatcher` — pure Kotlin Levenshtein distance against all `base_id` values

## Phase 5 — DI (Koin)
- [ ] Create shared Koin module (repositories, ViewModels, FuzzyMatcher)
- [ ] Create Android Koin module (DB driver, OcrEngine, CameraController)
- [ ] Create iOS Koin module (DB driver, OcrEngine, CameraController)

## Phase 6 — ViewModels (MVI)
- [ ] Define `CardListState`, `CardListIntent`, implement `CardListViewModel`
- [ ] Define `CollectionState`, `CollectionIntent`, implement `CollectionViewModel`
- [ ] Define `ScannerState`, `ScannerIntent`, implement `ScannerViewModel`

## Phase 7 — UI Screens
- [ ] Create `CardListScreen` — image grid, search bar + button, color/rarity/type filter chips, clear button
- [ ] Create `CardDetailScreen` — full card info, owned count, variant list, "Add to collection" button
- [ ] Create `CollectionScreen` — owned grid, quantity controls, summary row, search/filter
- [ ] Create `ScannerScreen` — camera preview, ROI overlay, horizontal variant image strip, "No match" message, link to browser
- [ ] Create bottom navigation (Browser, Collection, Scanner)
- [ ] Wire navigation graph

## Phase 8 — Platform Glue
- [ ] Android: `MainActivity.kt` (start Koin, set content)
- [ ] Android: `AndroidManifest.xml` (camera permission, app metadata)
- [ ] iOS: `MainViewController.kt` (entry point for SwiftUI host)
- [ ] iOS: `Info.plist` (camera usage description string)
- [ ] iOS: `iOSApp.swift` (SwiftUI app entry, init Koin)
