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

The project is built as a Kotlin Multiplatform app with a shared UI and shared domain/data layers, but the scanner experience is currently **Android-first**.

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

### iOS
iOS project structure exists and the shared app can be hosted there, but feature parity is not complete.

Current status:
- Shared navigation and shared UI structure exist
- Scanner route exists
- Scanner preview currently shows a placeholder message instead of a working camera scanner

Current scanner placeholder text:
- `Scanner not available on this platform`

## Main Screens

### Home
The home screen is currently a simple hub with buttons to open:
- All Cards
- My Collection
- Sync Data
- Scanner

### Card List
The card list screen currently supports:
- paged card browsing
- search by name
- scroll position restoration through state
- opening a card detail screen by tapping a card image

Current UI notes:
- cards are shown in a 3-column grid
- search uses an input field with search IME action
- loading and error states are displayed

### Collection
The collection screen currently supports:
- paged browsing of owned cards
- displaying owned quantity as an overlay badge
- opening card detail by tapping a card image
- scroll position restoration through state

Current limitation:
- collection search UI is not currently active in the screen

### Sync
The sync screen currently supports:
- triggering remote sync manually
- showing sync status
- showing last seeded version
- showing number of rows written
- navigating to the list after sync flow

Current status values shown by the UI:
- Idle
- Working...
- Up to date
- Seeded
- Error

### Card Detail
The card detail screen currently supports:
- loading card information by id
- showing card image and metadata
- showing current owned quantity
- incrementing owned quantity
- decrementing owned quantity

It also participates in a special scanner return flow:
- if the user opened detail from scan flow
- and changed quantity
- then back navigation skips `ScanResults` and returns directly to `Scanner`

If the user opened detail from normal browsing flows, back behaves normally.

### Scanner
The scanner screen currently supports:
- camera permission request on Android
- live camera preview on Android
- OCR text detection from the preview stream
- centered crop guide overlay
- progress indicator while lookup is processing

The scanner is designed to read the printed card id from the physical card rather than barcode or QR code data.

### Scan Results
The scan results screen is used when a scanned base id matches multiple variants.

It currently supports:
- loading all matching variants for a base id
- showing them in a 3-column grid
- opening card detail when the user taps the correct variant

## Navigation Flow

The shared app currently includes these routes:

- `Home`
- `CardList`
- `Collection`
- `Sync`
- `CardDetail`
- `Scanner`
- `ScanResults`

### Main flows

#### Browsing flow
- `Home -> CardList -> CardDetail`
- `Home -> Collection -> CardDetail`

#### Sync flow
- `Home -> Sync`
- the sync screen can navigate to the list after sync

#### Scanner flow
- `Home -> Scanner`
- if one match is found: `Scanner -> CardDetail`
- if multiple variants are found: `Scanner -> ScanResults -> CardDetail`

### Special back behavior in scan flow
When a user reaches `CardDetail` from scanner flow:
- if quantity was **not** changed, back returns normally
- if quantity **was** changed, back skips `ScanResults` and returns directly to `Scanner`

This behavior applies to both:
- app bar back navigation
- system / gesture back navigation

## Current Scanner Behavior

The current Android scanner behavior includes:

- OCR-based card id detection
- support for these card id patterns:
  - `LLDD-DDD`
  - `L-DDD`
  - `LLLDD-DDD`
- normalization logic for common OCR mistakes like:
  - `O` vs `0`
  - `I` vs `1`
- 3 consecutive frame confirmation before accepting a match
- 1.5 second cooldown after a match
- direct navigation to detail for single-result matches
- navigation to variant selection for multi-result matches

## Known Limitations

These limitations are visible from the current implementation:

- iOS scanner is currently a placeholder, not a working camera/OCR implementation
- collection search UI is currently commented out / inactive
- the home screen is still a simple functional hub rather than a polished dashboard
- sync is user-triggered from the sync screen rather than fully automated
- current documentation is still being refreshed to match the implementation

## Scope of This Document

This file is meant to describe the **current app state** only.

It should be updated whenever:
- a new user-facing screen is added
- a major flow changes
- platform support changes
- scanner behavior changes significantly

