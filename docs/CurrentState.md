# Current State

This document describes the current implemented state of Deckamushi.

It is intentionally focused on **what exists today** in the codebase, especially the shared navigation flow and the Android-first MVP.

## Summary

Deckamushi currently supports these core loops:

1. Sync the card database from a remote GitHub-hosted JSON source
2. Browse the full card catalog
3. View owned cards in the collection
4. Open card details and change owned quantity
5. Scan physical cards on Android and resolve print variants when needed
6. Export owned collection to a JSON backup file via share sheet
7. Import a previously exported backup, with Overwrite or Merge conflict resolution

The project is built as a Kotlin Multiplatform app with a shared UI and shared domain/data layers. The scanner experience is currently **Android-first**.

## Platform Status

### Android
Android is the main supported platform today.

Implemented:
- Shared app shell and navigation
- Remote sync flow
- All cards browser
- Collection browser
- Card detail with quantity controls
- Scanner using CameraX + ML Kit OCR
- Variant selection screen after scan when multiple variants exist
- Export collection via share sheet (writes `.json` file, uses FileProvider)
- Import collection from a `.json` file (file picker, validation, conflict resolution)

### iOS
iOS project structure exists and the shared app can be hosted there, but feature parity has not been fully tested.

Current status:
- Shared navigation and shared UI structure work
- Settings screen, export, and import routes exist
- Share sheet (export) uses `UIActivityViewController` with `NSData`-backed file sharing
- File picker (import) uses `UIDocumentPickerViewController`
- Scanner is implemented using AVFoundation (capture session) + Vision framework (VNRecognizeTextRequest) — not yet tested on a device

## Main Screens

### Home
The home screen is a 2+1 navigation hub:

```
[ All Cards ] [ Collection ]
[       Scanner            ]
```

- Two square tiles (All Cards, My Collection) in the top row
- Scanner tile spans the full bottom row with a 3:1 aspect ratio
- The gear icon in the top app bar opens Settings from any screen (except Settings itself and Scanner)

### Settings
The settings screen is the data management hub, split into two sections.

**Card Database**
- Sync status indicator (color-coded dot)
- Last synced version and card count
- "Sync Now" button that triggers the card data update

**Collection Backup**
- Export Collection — shares the owned collection as a `.json` file via the platform share sheet
- Import Collection — opens a file picker; after selection, a dialog asks how to handle conflicts:
  - **Overwrite** — replaces all current quantities with the file's values
  - **Merge** — keeps whichever quantity is higher for each card
- Validation: if any card ID in the file is not found in the local database, the import is cancelled and a message asks the user to sync first

### Card List
- Paged card browsing
- Search by name
- Scroll position restoration
- Opens card detail on tap

Current UI:
- 3-column card image grid
- Search input with search IME action
- Loading and empty states shown

### Collection
- Paged browsing of owned cards
- Owned quantity as an overlay badge
- Opens card detail on tap
- Scroll position restoration

### Card Detail
- Loads card information by ID
- Shows card image and full metadata
- Shows and updates owned quantity (increment / decrement)
- Ability text renders multi-bracket abilities on separate lines for readability

Scanner return flow:
- If opened from the scan flow and quantity was changed, back navigation skips `ScanResults` and returns directly to `Scanner`
- If opened from normal browsing, back behaves normally

### Scanner (Android)
- Camera permission request
- Live camera preview with centered crop guide
- OCR text detection from the preview stream
- Progress indicator while lookup runs

### Scan Results
Used when a scanned base ID matches multiple variants:
- Loads all matching variants
- Shows them in a 3-column grid
- Opens card detail on tap

## Navigation Flow

Current routes:

- `Home`
- `CardList`
- `Collection`
- `Settings`
- `CardDetail(id, fromScan)`
- `Scanner`
- `ScanResults(baseId)`

### Main flows

#### Browsing
- `Home → CardList → CardDetail`
- `Home → Collection → CardDetail`

#### Settings / Sync
- Settings is accessible from the top app bar gear icon on all screens except Settings itself and Scanner

#### Scanner
- `Home → Scanner`
- Single match: `Scanner → CardDetail`
- Multiple variants: `Scanner → ScanResults → CardDetail`

### Special back behavior in scan flow
When a user reaches `CardDetail` from the scanner flow:
- If quantity was **not** changed, back returns normally
- If quantity **was** changed, back skips `ScanResults` and returns directly to `Scanner`

This applies to both app bar back and system/gesture back navigation.

## Export / Import Details

### Export
- Queries all cards where `quantity > 0`
- Serializes to JSON: `[{"cardId":"OP01-001","quantity":3}, ...]`
- Shares the file via the platform share sheet as `deckamushi_backup.json`
- Android: uses `FileProvider` + `Intent.ACTION_SEND` with `application/json` MIME type
- iOS: writes `NSData` to a temp file and presents via `UIActivityViewController`

### Import
- User picks a `.json` file via the platform file picker
- File content is validated: any unknown card ID cancels the entire transaction
- User confirms the conflict strategy (Overwrite or Merge) via an in-app dialog before any data is written
- The entire import runs inside a single database transaction

## Known Limitations

- iOS scanner is a placeholder (no working camera / OCR)
- iOS `RemoteImage` is currently a placeholder (gray box)
- Collection search UI exists in the domain layer but is not active in the screen
- Sync is user-triggered from Settings rather than automatic on launch

## Scope of This Document

Update this file whenever:
- A new user-facing screen is added
- A major flow changes
- Platform support status changes
- Scanner behavior changes significantly
- Export / import format or flow changes