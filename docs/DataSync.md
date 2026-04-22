# Data Sync

This document describes the **current** card-data sync implementation in Deckamushi.

It focuses on what the code does today, not on older planning notes or possible future improvements.

## Summary

The current sync flow does this:

1. Requests `version.json` from the GitHub-backed data source
2. Uses the cached `ETag` for `version.json` to avoid unnecessary downloads
3. Compares the remote `cards_version` with the locally cached one as an extra safety check
4. If an update is needed, downloads `cards.json`
5. Maps the remote DTOs into database rows
6. Writes the rows into the local SQLDelight `cards` table using `INSERT OR REPLACE`
7. Stores the new `ETag` and `cards_version` locally

At the UI level, the user triggers this flow manually from the `Sync` screen.

## Remote Source

The app builds its remote base URL from values in `local.properties` via `BuildKonfig`.

Required configuration:

```properties
GITHUB_PAT=your_github_token
GITHUB_DATA_OWNER=your_github_username_or_org
GITHUB_DATA_REPO=your_repo_name
```

This becomes a GitHub Contents API base URL of the form:

```text
https://api.github.com/repos/<owner>/<repo>/contents
```

The current sync flow reads two files from that source:

- `version.json`
- `cards.json`

## Network Requests

The current remote client is `DeckamushiDataApi`.

### `fetchVersion()`
The version request:
- calls `version.json`
- sends GitHub auth using `Bearer <API_KEY>`
- sends `Accept: application/vnd.github.raw+json`
- sends `If-None-Match` when a cached `ETag` exists

Possible outcomes:
- `RemoteResult.Success<VersionDto>`
- `RemoteResult.NotModified`
- `RemoteResult.HttpError`
- `RemoteResult.NetworkError`

### `fetchCards()`
The cards request:
- calls `cards.json`
- uses the same GitHub auth and raw JSON headers
- does **not** currently use a separate cached `ETag` for `cards.json`

Possible outcomes:
- `RemoteResult.Success<List<CardDto>>`
- `RemoteResult.HttpError`
- `RemoteResult.NetworkError`

## Local Sync Cache

The sync flow uses the `VersionCache` abstraction to store two values:

- `versionETag`
- `cardsVersion`

### What each cached value means

#### `versionETag`
This is the cached `ETag` from `version.json`.

It is used for conditional requests so the app can ask GitHub whether `version.json` changed without always re-downloading it.

#### `cardsVersion`
This is the `cards_version` field from `version.json`.

It acts as an extra safety check in case the server returns a successful `version.json` response but the logical card-data version is still the same.

### Current storage implementations

Available implementations in the project:
- Android: `AndroidVersionCache` backed by DataStore Preferences
- iOS: `IosVersionCache` backed by `NSUserDefaults`

The project is currently Android-first, but the cache abstraction already exists for both platforms.

## Version Metadata Currently Used

The remote `VersionDto` contains these fields:

- `schemaVersion`
- `cardsVersion`
- `generatedAtUtc`
- `cardCount`
- `cardsSha256`

In the current sync logic, only this field is actively used for update decisions:

- `cardsVersion`

The other fields are currently present in the payload but are **not** used by `UpdateCardDataUseCase`.

## Sync Decision Flow

The current sync logic lives in `UpdateCardDataUseCase.run()`.

### Step 1: Load cached values
Before making requests, the use case reads:
- cached `versionETag`
- cached `cardsVersion`

### Step 2: Request `version.json`
The app calls `fetchVersion(cachedETag)`.

#### If GitHub returns `NotModified`
The sync ends immediately as:
- `Result.UpToDate`

This means the cached `ETag` is still valid and the app does not fetch `cards.json`.

#### If GitHub returns an HTTP or network error
The sync ends as:
- `Result.Error(...)`

#### If GitHub returns a new `version.json`
The app:
1. reads `cardsVersion` from the response
2. stores the returned `ETag`
3. compares remote `cardsVersion` with the locally cached `cardsVersion`

### Step 3: Extra safety check using `cardsVersion`
If the new `cardsVersion` is not blank and is equal to the cached `cardsVersion`, the sync exits as:
- `Result.UpToDate`

This is an extra guard on top of the `ETag` check.

### Step 4: Fetch `cards.json`
If the version check indicates new data, the app downloads `cards.json`.

If this request fails, sync returns:
- `Result.Error(...)`

If it succeeds, the app maps all remote card DTOs into database models and writes them into the database.

## Database Write Behavior

The local database uses SQLDelight.

The current sync writes into the `cards` table inside a database transaction.

For each mapped row, the app calls:
- `cardQueries.insertCard(...)`

The SQL for that query is:

```sql
INSERT OR REPLACE INTO cards (...)
```

### What this means in practice

For each incoming card row:
- if the card id does not exist yet, the row is inserted
- if the card id already exists, the row is replaced with the new values

### Important consequence
The current sync logic does **not** run a delete step before writing.

That means:
- new cards are added
- existing cards are updated/replaced
- cards missing from the new `cards.json` are **not automatically removed**

## What Sync Does Not Do

The current implementation does **not** do these things:

- it does **not** wipe the full `cards` table before inserting
- it does **not** delete cards that are absent from the new `cards.json`
- it does **not** use a dedicated `ETag` cache for `cards.json`
- it does **not** validate `cardsSha256`
- it does **not** use `schemaVersion` for migration logic
- it does **not** use `cardCount` for integrity checks
- it does **not** use `generatedAtUtc` for decision making
- it does **not** auto-run silently in the background from app startup

## Collection Safety Implications

Because the current sync flow does not explicitly delete rows from `cards`, it does **not** trigger collection cleanup through card deletion during normal sync.

With the current implementation:
- updating an existing card keeps the same primary key path
- inserting new cards is safe
- missing cards are left untouched instead of being removed

This means the current sync strategy does **not** behave like a destructive wipe-and-reseed process.

## User-Visible Sync Behavior

From the current `Sync` screen, the user can:
- tap `Sync / Seed cards`
- see the current status
- see the last seeded version
- see how many rows were written in the latest successful seed

### Current statuses shown in the UI
- `Idle`
- `Working...`
- `Up to date`
- `Seeded`
- `Error`

### Current result mapping
- `UpdateCardDataUseCase.Result.UpToDate` -> UI shows `Up to date`
- `UpdateCardDataUseCase.Result.Seeded` -> UI shows `Seeded` and updates version/count
- `UpdateCardDataUseCase.Result.Error` -> UI shows `Error`

## Scope of This Document

This file should be updated when any of these change:
- the remote source format
- cache keys or cache strategy
- sync decision rules
- SQL write behavior
- destructive sync behavior
- user-visible sync states

