# XRayDB Pocket

Offline-first Kotlin + Jetpack Compose Android reference app.

## Open
1. Open this folder in Android Studio.
2. Allow Gradle sync.
3. Run on an Android 8.0+ device/emulator.

## Included
- 118-element selector (symbol, name and atomic number)
- Room/SQLite database
- Element detail grouped by X-ray/XPS category
- Reverse energy lookup with tolerance
- eV/keV, wavelength and frequency conversion
- Provenance/environment fields

## Data warning
The bundled spectral rows are a deliberately small demonstration dataset, not a complete scientific database. They are marked as demonstration values. Do not use them for publication without checking the cited primary database/literature. Import only datasets whose redistribution terms permit bundling in an app.

## Database schema
`elements` and `spectral_entries`. Replace `app/src/main/assets/xray.db` with a validated database using the same schema; increment the Room database version when changing schema.
