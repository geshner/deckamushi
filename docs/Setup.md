# Setup

This document explains how to set up and run Deckamushi from the current codebase.

It focuses on the implementation as it exists today.

## Prerequisites

### Required tools

- Android Studio (current stable is recommended)
- JDK 11 available to Gradle (project uses Java 11 compatibility in `androidApp`)
- Git

### Platform-specific tools

### Android
- Android SDK installed through Android Studio
- SDK for:
  - compile SDK 37
  - min SDK 24 (runtime target for app users)

### iOS
- macOS with Xcode
- Cocoa runtime/simulator tooling available in Xcode

## Gradle and Modules

Project uses Gradle wrapper:

- Gradle distribution: `9.3.1`

Included Gradle modules:

- `:shared`
- `:androidApp`

iOS host app exists in `iosApp/` and is built/run from Xcode.

## Local Configuration

Deckamushi reads required sync values from `local.properties` (root project).

### Required keys

```properties
# Android SDK path (set by Android Studio)
sdk.dir=YOUR_ANDROID_SDK_PATH

# Remote card data source config used by BuildKonfig
GITHUB_PAT=your_github_token
GITHUB_DATA_OWNER=your_github_username_or_org
GITHUB_DATA_REPO=your_repo_name
```

These keys are used by `shared/build.gradle.kts` to generate:

- `BuildKonfig.API_KEY`
- `BuildKonfig.BASE_URL`

If these values are missing, sync-related runtime behavior will not work correctly.

## Android Setup and Run

### Build debug APK

```powershell
.\gradlew.bat :androidApp:assembleDebug
```

### Compile shared Android source set only

```powershell
.\gradlew.bat :shared:compileAndroidMain
```

### Optional: compile all shared metadata (includes iOS metadata)

```powershell
.\gradlew.bat :shared:compileIosMainKotlinMetadata
```

### Run from Android Studio

1. Open project root `Deckamushi`
2. Select `androidApp` run configuration
3. Run on emulator/device

## iOS Setup and Run

iOS host app uses SwiftUI wrapper over shared `MainViewController()`.

### Open in Xcode

- Open `iosApp/iosApp.xcodeproj`
- Select simulator/device
- Run

Notes:

- The app structure is shared-first, but iOS feature parity is not complete yet (for example, scanner implementation is currently placeholder-only)
- iOS setup is expected to be performed on macOS

## Quick Verification Checklist

After setup, verify in this order:

1. Android build compiles
2. App launches on Android
3. Sync screen opens and can trigger sync flow
4. Card list renders
5. Scanner route opens on Android

## Useful Commands

```powershell
# Build Android debug app
.\gradlew.bat :androidApp:assembleDebug

# Compile shared Android source set
.\gradlew.bat :shared:compileAndroidMain

# Compile iOS Kotlin metadata from shared module
.\gradlew.bat :shared:compileIosMainKotlinMetadata
```

## Troubleshooting

### Gradle/JDK issues

- Ensure Gradle is using JDK 11-compatible runtime
- If needed, set `JAVA_HOME` before running Gradle

Example PowerShell session:

```powershell
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
.\gradlew.bat :androidApp:assembleDebug
```

### Sync not working

Check:

- `local.properties` contains all required GitHub keys
- token permissions and repository visibility are correct
- network access is available

### Camera scanner not working on Android

Check:

- camera permission granted in app settings
- device/emulator has camera support
- manifest includes camera permission and feature declarations

## Scope of This Document

Update this file when any of these change:

- required tools or versions
- module layout
- required `local.properties` keys
- Gradle tasks used for build/verification
- platform run flow

