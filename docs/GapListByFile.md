# Deckamushi — Gap List (by file, codebase-truth)

This document lists what each relevant file/module currently contains and what gaps remain to reach a working app using **UseCases + MVI**.

> Rule for this gap list: **the codebase is the source of truth**. Anything in docs that differs is treated as out-of-date.

---

## Top-level / Gradle

### `shared/build.gradle.kts`
**Has:**
- KMP + Compose Multiplatform + SQLDelight + Koin + Ktor + Serialization plugins.
- SQLDelight database configured: `AppDatabase` with package `io.capistudio.deckamushi.db`.
- BuildKonfig configured:
  - `BuildKonfig.API_KEY` from `local.properties` `GITHUB_PAT`
  - `BuildKonfig.BASE_URL` built from `GITHUB_DATA_OWNER` + `GITHUB_DATA_REPO`

**Gaps:**
- No explicit DataStore dependency in `:shared` visible here (Android DataStore may be in version catalog / other module). If you implement DataStore inside `:shared`, ensure dependency exists.
- Consider removing/choosing between `ApiResponse` vs `RemoteResult` (you currently have both types in code).

---

## SQLDelight (DB schema + queries)

### `shared/src/commonMain/sqldelight/io/capistudio/deckamushi/db/Card.sq`
**Has:**
- `cards` table with PK `id`.
- Columns include `base_id`, `variant`, `name`, `color_flags`, `rarity_id`, `card_category`, power/counter/life, etc.
- Insert statement: `insertCard` (INSERT OR REPLACE).
- Query: `getCardById`.
- Query: `getAllCardsWithOwnership` (LEFT JOIN `collection`, includes computed `owned_quantity` + `is_owned`).

**Gaps (likely needed soon):**
- Search query by `name`.
- Filter queries by `color_flags`, `rarity_id`, and `card_category`.
- Query for variants by `base_id` (for detail + scanner flows).

### `shared/src/commonMain/sqldelight/io/capistudio/deckamushi/db/Collection.sq`
**Has:**
- `collection(card_id PK, quantity, FK→cards(id))`.
- `incrementQuantity` implemented via INSERT OR REPLACE using a subquery.
- `decrementQuantity` only decrements if `quantity > 1`.
- `deleteCollectionEntry`, `getQuantityByCardId`, `getAllOwned`.

**Gaps:**
- A join query to fetch owned cards with card details (for Collection UI grid/list).
- A safe “decrement to zero removes entry” usecase/transaction (currently decrement stops at 1; deleting is separate).

---

## Remote API + DTOs

### `shared/src/commonMain/kotlin/io/capistudio/deckamushi/data/remote/DeckamushiDataApi.kt`
**Has:**
- `fetchVersion(etag)` calling GitHub contents URL derived from `BuildKonfig.BASE_URL`.
- Sends headers:
  - `Authorization: Bearer ${BuildKonfig.API_KEY}`
  - `Accept: application/vnd.github.raw+json`
  - `User-Agent: Deckamushi`
  - `If-None-Match` when ETag provided
- Returns `RemoteResult<VersionDto>`.

**Gaps:**
- Missing `fetchCards()` for `cards.json` (and/or other payload files).
- `errorBody` is read but not used (minor cleanup).
- Decide whether to standardize on `RemoteResult` OR `ApiResponse` (both exist).

### `shared/src/commonMain/kotlin/io/capistudio/deckamushi/data/remote/RemoteResult.kt`
**Has:**
- Result sealed interface with `Success`, `NotModified`, `HttpError`, `NetworkError`.

**Gaps:**
- Consider adding a structured error type (domain-friendly) later, but not required immediately.

### `shared/src/commonMain/kotlin/io/capistudio/deckamushi/data/remote/ApiResponse.kt`
**Has:**
- Another result wrapper type (`ApiResponse`).

**Gaps:**
- This duplicates `RemoteResult` and is currently unused by `DeckamushiDataApi`. Pick one and delete/stop using the other to reduce confusion.

### `shared/src/commonMain/kotlin/io/capistudio/deckamushi/data/remote/dto/VersionDto.kt`
**Has:**
- `VersionDto(schema_version, cards_version, generated_at_utc, card_count, cards_sha256)`.

**Gaps:**
- Ensure this exactly matches the JSON returned by your private `version.json`.

### `shared/src/commonMain/kotlin/io/capistudio/deckamushi/data/remote/dto/CardDto.kt`
**Has:**
- DTO with a mix of fields:
  - `@SerialName("variant_id") val id: String`
  - `@SerialName("id") val baseId: String`
  - other fields like `card_type`, `power`, etc.

**Gaps / risks:**
- SerialName mapping looks suspicious: `baseId` uses `@SerialName("id")` while DB schema uses `base_id`. This may still be correct if your JSON uses `id` for base id and `variant_id` for unique id — verify against your actual `cards.json`.
- No mapper exists: `CardDto` → SQLDelight `insertCard` arguments.

---

## Local persistence (version/etag)

### `shared/src/commonMain/kotlin/io/capistudio/deckamushi/data/local/VersionCache.kt`
**Has:**
- Interface with:
  - `getVersionETag` / `setVersionETag`
  - `getCardsVersion` / `setCardsVersion`

**Gaps:**
- No actual implementations.
- No factory/DI binding.

### `shared/src/androidMain/kotlin/io/capistudio/deckamushi/data/local/VersionCacheFactory.android.kt`
**Has:**
- Placeholder file; Android DataStore implementation is commented out.

**Gaps:**
- Implement DataStore-based `VersionCache` actual.
- Provide a way to construct it (needs Android `Context`).

### iOS `VersionCache`
**Has:**
- No file present.

**Gaps:**
- Add `shared/src/iosMain/.../VersionCache.ios.kt` using `NSUserDefaults`.

---

## Networking platform engines

### `shared/src/commonMain/kotlin/io/capistudio/deckamushi/core/network/KtorClientFactory.kt`
**Has:**
- `expect fun createHttpClient()` plus shared `commonConfig()` installing timeouts, JSON, logging.

**Gaps:**
- No DI wiring currently provides `HttpClient` to `DeckamushiDataApi`.

### `shared/src/androidMain/kotlin/.../KtorClientFactory.android.kt`
**Has:**
- OkHttp engine actual.

### `shared/src/iosMain/kotlin/.../KtorClientFactory.ios.kt`
**Has:**
- Darwin engine actual.

---

## UI / App entry

### `shared/src/commonMain/kotlin/io/capistudio/deckamushi/App.kt`
**Has:**
- Compose template (“Click me!”) — no app screens.

**Gaps:**
- Replace with navigation shell and real screens.
- Add ViewModel hookup (Koin / manual).

### `shared/src/androidMain/kotlin/io/capistudio/deckamushi/MainActivity.kt`
**Has:**
- Calls `App()`.

**Gaps:**
- Start DI.
- Provide Android `Context` bindings needed by `VersionCache` and SQLDelight driver.
- Trigger initial data sync (or expose a UI button).

### `shared/src/iosMain/kotlin/io/capistudio/deckamushi/MainViewController.kt`
**Has:**
- Calls `App()`.

**Gaps:**
- Initialize DI on iOS (if using Koin).
- Provide iOS bindings (VersionCache + SQLDelight driver).

---

## Entire layers missing (not present yet)

### Domain layer (UseCase pattern)
**Missing packages/files:**
- `domain/model/*` (Card, enums, etc.)
- `domain/repository/*` (CardRepository, CollectionRepository, SyncRepository)
- `domain/usecase/*`

### Data layer repository implementations
**Missing packages/files:**
- `data/repository/*` implementing the domain repositories using SQLDelight + API.

### Presentation layer (MVI)
**Missing packages/files:**
- `presentation/mvi/*` base store/viewmodel
- feature viewmodels:
  - Card list
  - Card detail
  - Collection
  - Scanner

### DI (Koin modules)
**Missing packages/files:**
- `di/*` modules that provide HttpClient, API, DB, repositories, usecases, viewmodels.

---

## Suggested “first vertical slice” to implement (to validate the architecture)

1. Implement VersionCache (Android + iOS)
2. Add `fetchCards()` to `DeckamushiDataApi`
3. Add DB provider + seed/update service writing via `insertCard`
4. Create `CardRepository` + `GetCardsWithOwnershipUseCase`
5. Create MVI base + `CardListViewModel`
6. Replace `App.kt` template with CardList screen rendering DB-backed cards

