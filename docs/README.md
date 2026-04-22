# Documentation Index

This index helps separate **current implementation docs** from **historical/planning docs**.

When documents conflict, treat the current implementation docs (and code) as source of truth.

## Current Source of Truth

These documents describe the app as it exists today.

- `README.md`
  - High-level project overview and quick orientation
- `docs/CurrentState.md`
  - What is currently implemented (features, flows, platform status)
- `docs/Architecture.md`
  - Current architecture boundaries, layering, DI, platform seams
- `docs/DataSync.md`
  - Current sync pipeline from GitHub data to SQLDelight
- `docs/ScannerFlow.md`
  - Current Android scanner behavior and scan navigation rules
- `docs/Setup.md`
  - Practical setup/build/run instructions

## Recommended Reading Order (New Contributors)

1. `README.md`
2. `docs/CurrentState.md`
3. `docs/Architecture.md`
4. `docs/DataSync.md`
5. `docs/ScannerFlow.md`
6. `docs/Setup.md`

Then optionally review historical docs for context.

## Maintenance Rule

When you change behavior in code, update the corresponding current doc in this order:

1. `docs/CurrentState.md` (if user-facing behavior changed)
2. specific deep-dive doc (`DataSync`, `ScannerFlow`, `Architecture`, `Setup`)
3. `README.md` (if high-level positioning changed)

