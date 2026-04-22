# Deckamushi 🃏

Deckamushi is a **Kotlin Multiplatform** One Piece Card Game collection tracker.

The app currently focuses on an **Android-first MVP** built from a shared codebase. It can sync a remote card database, browse cards, track owned quantities, and scan physical cards on Android using OCR.

## Current MVP Features

- 🔄 **Remote data sync** from a GitHub-hosted `cards.json`
- 📚 **All cards browser**
- 🗂️ **Collection screen** for owned cards
- 🃏 **Card detail** with owned quantity controls
- 📷 **Android card scanner** for reading printed card IDs from physical cards
- 🎴 **Variant picker** when a base card ID has multiple print variants

## Platform Status

- **Android:** main supported platform today
- **iOS:** shared app structure exists, but scanner behavior is currently a placeholder and not feature-complete

## Tech Stack

| Concern | Technology |
|---|---|
| Language | Kotlin Multiplatform (KMP) |
| UI | Compose Multiplatform |
| Navigation | Navigation Compose KMP |
| Architecture | MVI |
| Dependency Injection | Koin |
| Database | SQLDelight |
| Networking | Ktor |
| Serialization | kotlinx.serialization |
| Image loading | Coil 3 |
| Logging | Kermit |
| Paging | Paging 3 KMP |
| Android scanner | CameraX + ML Kit Text Recognition |

## Project Structure

```text
Deckamushi/
  androidApp/   Android launcher app
  shared/       Shared KMP code: UI, navigation, viewmodels, domain, data, SQLDelight
  iosApp/       Xcode project / iOS host app
  docs/         Planning notes, architecture decisions, and implementation history
```

### Important modules

- `shared/`
  - Shared UI and navigation
  - Domain use cases and repositories
  - SQLDelight database access
  - Platform-specific implementations via `androidMain` / `iosMain`
- `androidApp/`
  - Android entry point and app packaging
- `iosApp/`
  - iOS host application for the shared UI

## App Navigation

The current shared app includes these main routes:

- `Home`
- `CardList`
- `Collection`
- `Sync`
- `Scanner`
- `ScanResults`
- `CardDetail`

## Local Configuration

The app expects GitHub data source values in `local.properties`.

Required keys:

```properties
GITHUB_PAT=your_github_token
GITHUB_DATA_OWNER=your_github_username_or_org
GITHUB_DATA_REPO=your_repo_name
```

These values are used by `BuildKonfig` in `shared/build.gradle.kts` to build the GitHub API base URL and authenticate requests for card data.

## Build & Run

### Android

Build the Android app:

```powershell
.\gradlew.bat :androidApp:assembleDebug
```

If you just want to verify the shared Android source set compiles:

```powershell
.\gradlew.bat :shared:compileAndroidMain
```

### iOS

Open `iosApp/iosApp.xcodeproj` in Xcode on macOS and run the app from there.

## Notes About Sync

- Card data is seeded from remote JSON into the local SQLDelight database
- Existing cards are updated with insert/replace behavior during sync
- New cards are added on sync
- The current sync flow does **not** wipe the full cards table before inserting

## Documentation Status

The documentation is being refreshed to match the current implementation.

Use these documents as the current entry points:

- [`docs/README.md`](docs/README.md) — documentation index and reading order
- [`docs/CurrentState.md`](docs/CurrentState.md) — what is implemented today
- [`docs/Architecture.md`](docs/Architecture.md) — current architecture and module boundaries
- [`docs/Setup.md`](docs/Setup.md) — setup, build, and run instructions
- [`docs/DataSync.md`](docs/DataSync.md) — current sync pipeline
- [`docs/ScannerFlow.md`](docs/ScannerFlow.md) — current scanner behavior

The `docs/` folder still contains some historical planning and lesson files, so if a historical doc conflicts with current behavior, prefer:

1. the code
2. the current docs above
3. older planning/history notes

This root `README.md` is the high-level project entry point.

## Current Status

Deckamushi currently has a working MVP around these core loops:

1. Sync card data
2. Browse cards
3. Track owned quantities
4. Scan cards on Android and resolve variants

The main development focus is currently Android-first, while keeping the shared architecture ready for iOS expansion.
