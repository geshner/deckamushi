# Architecture

This document describes the **current implemented architecture** of Deckamushi.

It is intentionally practical: it explains the structure that exists in code today.

## High-Level Shape

Deckamushi is a Kotlin Multiplatform app with:

- `shared/` for shared UI, domain logic, data access, and feature state
- `androidApp/` as the Android host app
- `iosApp/` as the iOS host app

The project follows a shared-first approach, with platform-specific code in `androidMain` and `iosMain` only where needed.

## Module Boundaries

### `shared/`
Contains the main architecture layers:

- app shell and typed navigation routes
- feature contracts, routes, and viewmodels
- domain use cases and result type (`DomainResult`)
- repository interfaces + implementations
- SQLDelight database access and queries
- network client + remote API
- platform seams via `expect/actual`

### `androidApp/`
Android launcher module.

Responsibilities:
- starts Koin with Android context
- hosts the shared Compose UI (`App()`)
- provides Android manifest declarations (including camera permission)

### `iosApp/`
iOS host (SwiftUI + UIKit bridge).

Responsibilities:
- embeds shared `MainViewController()` into SwiftUI (`ContentView.swift`)
- hosts shared UI from the KMP framework

## App Shell and Navigation

The app shell is defined in `shared/src/commonMain/kotlin/io/capistudio/deckamushi/App.kt`.

Core responsibilities:
- owns `NavHost`
- sets top app bar title based on current destination
- owns app-level snackbar host
- wires route callbacks to typed navigation
- provides scan-specific back override behavior for detail screens

Typed routes are defined in `Screen.kt` as serializable route models:

- `Home`
- `CardList`
- `Collection`
- `Sync`
- `CardDetail(id, fromScan)`
- `Scanner`
- `ScanResults(baseId)`

This keeps navigation strongly typed and shared across platforms.

## Presentation Pattern (MVI)

Feature state management uses a base `Mvi<S, A, E>` class.

Pattern used across features:

- `State`: full render state for the screen
- `Action`: user/system inputs to the viewmodel
- `Effect`: one-shot outputs (navigation, messages)

`Mvi` provides:

- `state: StateFlow<S>`
- `effects: SharedFlow<E>`
- `dispatch(action)` as the single action entry point
- mutex-guarded sequential action handling

Most features follow this structure:

- `FeatureContract.kt` (state/action/effect)
- `FeatureViewModel.kt`
- `FeatureRoute.kt` (collect effects + route integration)
- `FeatureScreen.kt` (UI rendering)

## Dependency Injection

DI uses Koin.

### Shared DI
In `KoinModules.kt`, shared bindings provide:

- network API client (`DeckamushiDataApi`)
- repository binding (`CardRepositoryImpl` -> `CardRepository`)
- use cases (`GetCardByIdUseCase`, `UpdateCardDataUseCase`, etc.)
- feature viewmodels (`CardListViewModel`, `SyncViewModel`, `CollectionViewModel`, `ScanViewModel`)

Parameterized viewmodels are used where route arguments are needed:

- `CardDetailViewModel(cardId, fromScan, ...)`
- `ScanResultsViewModel(baseId, ...)`

### Platform DI
`platformModule()` is `expect/actual` and supplies platform-specific dependencies.

- Android platform module currently provides:
  - `VersionCache` as `AndroidVersionCache`
  - `AppDatabaseProvider(DatabaseDriverFactory(context))`
- iOS platform module currently exists but is empty in code at this time

## Data Layer

### Database
Persistence uses SQLDelight.

Schema/query files:

- `Card.sq` (`cards` table + paging/search/by-id/by-base-id queries)
- `Collection.sq` (`collection` table + quantity operations)

Relationship detail:

- `collection.card_id` references `cards.id` with `ON DELETE CASCADE`

Access pattern:

- `AppDatabaseProvider` wraps `AppDatabase`
- repositories and use cases use generated query APIs

### Repository
`CardRepository` is the current domain-facing abstraction for card and collection operations.

`CardRepositoryImpl` handles:

- card reads (by id, paged, search)
- owned quantity increment/decrement logic
- owned cards paging source
- scanner lookup by `base_id`

### Networking + Sync

Remote access uses Ktor with shared `commonConfig()` and platform engines:

- Android: OkHttp
- iOS: Darwin

Sync pipeline today:

- `DeckamushiDataApi` fetches `version.json` and `cards.json`
- `UpdateCardDataUseCase` applies ETag/version checks
- cards are written with SQL `INSERT OR REPLACE`
- sync is triggered from the Sync screen via `SyncViewModel`

`DomainResult<T>` is used as the standard use case result wrapper in domain/presentation interactions.

## Platform Seams (`expect/actual`)

Current explicit platform boundaries include:

- `createHttpClient()`
- `DatabaseDriverFactory`
- scanner UI route (`ScanRoute`)
- camera preview component (`CameraPreview`)
- image component (`RemoteImage`)
- back-handler bridge (`PlatformBackHandler`)

This keeps shared feature logic in `commonMain` while isolating hardware/runtime differences per platform.

## Scanner Architecture Notes

Scanner is a good example of this split:

- shared scanner state machine and matching rules in `ScanViewModel`
- Android camera + OCR pipeline in `CameraPreview.android.kt`
- iOS scanner route present but currently placeholder-only

Navigation behavior in scan flows is coordinated through:

- `fromScan` route arg on `CardDetail`
- `quantityChanged` state in detail viewmodel
- special back effect that can skip `ScanResults` and return to `Scanner`

## Known Architecture Gaps / Current Constraints

These are visible in current code and important for maintenance:

- iOS scanner implementation is not feature-complete yet
- iOS `RemoteImage` is currently a placeholder (gray box)
- iOS platform DI module is currently empty
- `MainViewController.kt` currently calls `initKoin(cache, dbProvider, api)` while shared `initKoin` currently exposes a config-lambda signature; this path should be aligned when iOS implementation work resumes

## Architecture Guardrails (Current Project Conventions)

Current conventions used across implemented features:

- shared-first: keep business logic and feature state in `commonMain`
- keep screens UI-focused and route/viewmodel-driven
- keep one-shot behavior in `Effect`, not in persistent `State`
- use typed navigation routes instead of raw string routes
- use `DomainResult` for use case outcomes
- prefer platform seams only where runtime/hardware truly differs

## Scope of This Document

Update this file when any of these change:

- module boundaries
- navigation model
- MVI base pattern
- DI wiring strategy
- repository/data ownership boundaries
- platform seam decisions (`expect/actual` vs interface)

