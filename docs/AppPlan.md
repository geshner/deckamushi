# One Piece Card Collection App — Project Plan

## Stack

| Concern | Technology |
|---|---|
| Language | Kotlin Multiplatform |
| UI | Compose Multiplatform |
| Architecture | MVI |
| Database | SQLDelight (SQLite) |
| DI | Koin |
| Image loading | Coil 3 + Ktor client |
| Card scanning | CameraX (Android) / AVFoundation (iOS) |
| OCR | Tesseract 4 — `tesseract4android` (Android), `SwiftyTesseract` (iOS) |
| Fuzzy matching | Pure Kotlin Levenshtein distance |

---

## Feature Spec

### Screen 1 — Card Browser

| # | User story | Acceptance criteria |
|---|---|---|
| 1.1 | Browse all cards in a grid | Grid shows card image only |
| 1.2 | Search cards by name | User types then taps Search button to trigger the query |
| 1.3 | Filter by color | Multi-select color chips |
| 1.4 | Filter by rarity | Multi-select rarity chips |
| 1.5 | Filter by card type | Leader, Character, Event, Stage |
| 1.6 | Clear all filters | "Clear" button resets all active filters |
| 1.7 | Tap a card to see detail | Navigates to Card Detail screen |

### Screen 2 — Card Detail

| # | User story | Acceptance criteria |
|---|---|---|
| 2.1 | See full card info | Name, ID, color, rarity, type, cost, power, attribute (with icon), effect text |
| 2.2 | See card image | Loaded from remote URL, shows placeholder while loading |
| 2.3 | Add card to collection | "Add to collection" button increments owned quantity |
| 2.4 | See how many copies I own | Owned count displayed on the detail screen |
| 2.5 | See all variants of a card | Base ID variants (`_p1`, `_p2`, etc.) listed and selectable |

### Screen 3 — My Collection

| # | User story | Acceptance criteria |
|---|---|---|
| 3.1 | See all owned cards | List/grid of owned cards with quantity badges |
| 3.2 | Increase or decrease quantity | `+` / `−` controls per card |
| 3.3 | Remove a card | Quantity reaching 0 removes the entry |
| 3.4 | Search my collection | Same search/filter as Card Browser, scoped to owned cards |
| 3.5 | See a summary | Total unique cards owned and total card count |

### Screen 4 — Card Scanner

| # | User story | Acceptance criteria |
|---|---|---|
| 4.1 | Open camera to scan a card | Camera preview with ROI frame overlay on the bottom-right |
| 4.2 | App reads card ID automatically | OCR runs continuously; regex extracts the ID pattern |
| 4.3 | See all possible matches | OCR extracts base ID; all variants shown as horizontal list of card images |
| 4.4 | Select a variant and choose what to do | Tapping a variant image opens Card Detail screen; "Add to collection" from there |
| 4.5 | Feedback if no card found | "No match found — try again" message |
| 4.6 | Fall back to manual search | Link to Card Browser from the scanner screen |

---

## Data Model

### Table: `cards` (seeded once from JSON, read-only)

| Column | Type | Notes |
|---|---|---|
| `variant_id` | TEXT PK | e.g. `OP01-001_p1` |
| `base_id` | TEXT NOT NULL | e.g. `OP01-001` — shared across variants, used for scanner lookup |
| `variant` | TEXT NOT NULL | e.g. `p1`, or empty string for base printing |
| `name` | TEXT NOT NULL | |
| `color_flags` | INTEGER NOT NULL | Bitmask of `CardColor` enum |
| `rarity_id` | INTEGER NOT NULL | Maps to `Rarity` enum |
| `card_type` | TEXT NOT NULL | Leader / Character / Event / Stage |
| `cost` | INTEGER | Nullable |
| `power` | INTEGER | Nullable |
| `counter` | INTEGER | Nullable |
| `life` | INTEGER | Nullable (Leaders only) |
| `attribute` | TEXT | Nullable |
| `attribute_icon` | TEXT | Filename only, e.g. `slash.png` → bundled resource |
| `feature` | TEXT | Nullable |
| `effect` | TEXT | Nullable |
| `trigger` | TEXT | Nullable |
| `block_icon` | TEXT | Nullable |
| `card_set` | TEXT | Nullable |
| `get_info` | TEXT | Nullable |
| `image_url` | TEXT NOT NULL | Remote URL — loaded by Coil |

### Table: `collection` (user-owned cards)

| Column | Type | Notes |
|---|---|---|
| `variant_id` | TEXT PK | FK → `cards.variant_id` |
| `quantity` | INTEGER NOT NULL | Min 1; deleting row = removing from collection |

---

## Enums

### `CardColor` — bitmask

```kotlin
enum class CardColor(val bit: Int) {
    RED(1), GREEN(2), BLUE(4), PURPLE(8),
    BLACK(16), YELLOW(32), MULTICOLOR(64)
}

// Encode a set of colors to an integer
fun Set<CardColor>.toFlags() = fold(0) { acc, c -> acc or c.bit }

// Decode an integer back to a set of colors
fun Int.toColors() = CardColor.entries.filter { this and it.bit != 0 }.toSet()
```

Dual-color cards store both bits set, e.g. Red/Green = `1 or 2 = 3`.  
Filter query: `WHERE color_flags & :bit != 0`

### `Rarity`

```kotlin
enum class Rarity(val id: Int) {
    COMMON(0), UNCOMMON(1), RARE(2),
    SUPER_RARE(3), SECRET_RARE(4),
    LEADER(5), PROMO(6), SP_CARD(7)
}
```

### Rarity seeder mapping (raw JSON → enum)

| Raw value | Enum |
|---|---|
| `C` | `COMMON` |
| `UC` | `UNCOMMON` |
| `R` | `RARE` |
| `SR` | `SUPER_RARE` |
| `SEC` | `SECRET_RARE` |
| `L` | `LEADER` |
| `P` | `PROMO` |
| `SP P` | `PROMO` |
| `SPカード` | `SP_CARD` |

`SP_CARD` is the alternate-illustration / special-art variant of an existing card (same stats, premium artwork). Confirmed real — not a scraper artifact.

### Color seeder mapping (raw JSON → bitmask)

| Raw value | Flags |
|---|---|
| `色赤` | `RED` |
| `色緑` | `GREEN` |
| `色青` | `BLUE` |
| `色紫` | `PURPLE` |
| `色黒` | `BLACK` |
| `色黄` | `YELLOW` |
| `色赤/緑` | `RED or GREEN` |
| `色赤/青` | `RED or BLUE` |
| `色赤/紫` | `RED or PURPLE` |
| `色赤/黄` | `RED or YELLOW` |
| `色赤/黒` | `RED or BLACK` |
| `色緑/青` | `GREEN or BLUE` |
| `色緑/紫` | `GREEN or PURPLE` |
| `色緑/黄` | `GREEN or YELLOW` |
| `色緑/黒` | `GREEN or BLACK` |
| `色青/紫` | `BLUE or PURPLE` |
| `色青/黄` | `BLUE or YELLOW` |
| `色青/黒` | `BLUE or BLACK` |
| `色紫/黄` | `PURPLE or YELLOW` |
| `色紫/黒` | `PURPLE or BLACK` |
| `色黒/黄` | `BLACK or YELLOW` |

---

## Images

| Type | Source | Strategy |
|---|---|---|
| Card art | `image_url` in DB (official site) | Loaded on demand by Coil, cached to disk |
| Attribute icons | `attribute_images/` (~15 files, <100KB total) | Bundled in `composeResources/drawable/`, loaded via `painterResource()` |

If the official site changes image URLs, re-run the scraper and re-seed the DB — no app code changes needed.

---

## Scanner Pipeline

1. Camera frame captured (CameraX / AVFoundation via `expect/actual`)
2. Crop bottom-right ~20% of frame (ROI)
3. Tesseract OCR with:
    - `tessedit_char_whitelist = ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-`
    - `--psm 7` (single text line)
4. Regex extract: `[A-Z][A-Z0-9]*-\d{3}`
5. Levenshtein fuzzy match against all `base_id` values in DB
6. Return all variants for the matched `base_id`
7. Show variant image list → user taps one → Card Detail → "Add to collection"

---

## Derived Queries (no extra tables needed)

| Concept | SQL |
|---|---|
| All variants of a card | `SELECT * FROM cards WHERE base_id = ?` |
| Owned status on Card Detail | `SELECT quantity FROM collection WHERE variant_id = ?` |
| Collection total card count | `SELECT SUM(quantity) FROM collection` |
| Collection unique cards owned | `SELECT COUNT(*) FROM collection` |
| Cards by color (including dual) | `WHERE color_flags & :bit != 0` |

---

## Build Phases

1. **Phase 1** — Gradle scaffold (KMP + Compose Multiplatform + SQLDelight + Koin + Coil + Tesseract)
2. **Phase 2** — SQLDelight schema (`cards`, `collection`)
3. **Phase 3** — Data layer (models, DB driver factory, repositories, JSON seeder)
4. **Phase 4** — Scanner pipeline (`expect/actual` camera, OCR, fuzzy match)
5. **Phase 5** — DI (Koin modules — shared + platform)
6. **Phase 6** — ViewModels (MVI: `CardListViewModel`, `CollectionViewModel`, `ScannerViewModel`)
7. **Phase 7** — UI screens + bottom nav
8. **Phase 8** — Platform glue (Android `MainActivity` + manifest, iOS `MainViewController` + `Info.plist`)
