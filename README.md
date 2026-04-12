# Deckamushi 🃏

A **Kotlin Multiplatform** card collection tracker for the One Piece Card Game.  
Scan physical cards with your camera, browse the full card database, and manage your collection — on Android and iOS from a single shared codebase.

---

## Tech Stack

| Concern | Technology |
|---|---|
| Language | Kotlin Multiplatform (KMP) |
| UI | Compose Multiplatform |
| Architecture | MVI |
| Database | SQLDelight (SQLite) |
| DI | Koin |
| Image loading | Coil 3 + Ktor |
| Card scanning | CameraX (Android) / AVFoundation (iOS) |
| OCR | Tesseract 4 — `tesseract4android` / `SwiftyTesseract` |
| Fuzzy matching | Pure Kotlin Levenshtein distance |

---

## Features

- 📖 **Card Browser** — search and filter by name, color, rarity, and type
- 🔍 **Card Detail** — full card info, owned count, variant list
- 🗂️ **My Collection** — track owned cards with quantity controls
- 📷 **Card Scanner** — point camera at a card; OCR reads the ID and shows all variants instantly

---

## Project Structure

```
composeApp/
  src/
    commonMain/     ← shared business logic, UI, ViewModels, repositories
    androidMain/    ← Android-specific implementations (CameraX, Tesseract)
    iosMain/        ← iOS-specific implementations (AVFoundation, SwiftyTesseract)
iosApp/             ← SwiftUI entry point for iOS
docs/
  AppPlan.md        ← full product & technical plan
  AppSteps.md       ← phase-by-phase build checklist
```

---

## Build & Run

### Android

```shell
# Windows
.\gradlew.bat :composeApp:assembleDebug

# macOS / Linux
./gradlew :composeApp:assembleDebug
```

### iOS

Open `iosApp/iosApp.xcodeproj` in Xcode (requires macOS + Xcode) and run on simulator or device.

---

## Status

> 🚧 In active development — see [`docs/AppSteps.md`](docs/AppSteps.md) for the current build checklist.
