# XRayDB Pocket

An offline-first Kotlin + Jetpack Compose Android reference application designed for synchrotron researchers, beamline scientists, and X-ray spectroscopists.

## Features

- **Complete Periodic Table (118 Elements)**: Color-coded by state of matter at standard conditions (Gas in cyan, Liquid in green, Solid in neutral) following NIST conventions.
- **Authoritative Database (794+ Records)**:
  - **280** Solid-state core-level binding energies & XPS lines across 80 elements.
  - **366** Characteristic X-ray emission lines with Greek letter notation ($\alpha, \beta, \gamma, \ell$) and precise energies.
  - **26** Auger electron emission transitions (KLL, LMM, MNN).
  - **81** Gas-phase and vapor XPS binding energies for molecular gases and volatile solvents (Tartu / NIST).
  - **41** X-ray absorption edge threshold energies ($K, L_3$ edges) for XAS/EXAFS/XANES.
- **Reverse Energy Lookup**: Search transitions and edges by photon energy ($\text{eV}$ / $\text{keV}$) with tunable tolerance, displaying immediate wavelength ($\lambda$) and frequency ($\nu$) conversions.
- **XRF / EDX Peak Identifier**: Input experimental peak energies to instantly identify matching elemental emission lines with direct element navigation.
- **X-Ray Attenuation & Transmission Calculator**: Beer-Lambert calculator supporting 18 pre-set synchrotron filters/windows (Be, Diamond, Kapton, Mylar, Si, Ge, Al, Ti, Mo, Ag, Pd, Cu, Au, C, BN, Water, Air, PMMA) and custom chemical compounds ($\text{SiO}_2$, $\text{GaAs}$, $\text{Fe}_2\text{O}_3$, $\text{Al}_2\text{O}_3$, $\text{CeO}_2$, $\text{PbS}$, $\text{CdTe}$, $\text{InP}$, $\text{Si}_3\text{N}_4$, $\text{YBCO}$) with a compact 4-column preset grid.
- **Lab Note Markdown Export**: Copy formatted element summaries or individual spectral lines directly to the clipboard for electronic lab notebooks and publications.
- **Modern UI & UX**: Material 3 design, edge-to-edge support, system back-gesture integration (`BackHandler`), and custom Bohr/Moseley atomic transition vector adaptive app icon.
- **Monetization**: Top-most full-width Google AdMob banner integration.

## Tech Stack

- **Language**: Kotlin 2.0
- **UI**: Jetpack Compose & Material 3
- **Architecture**: MVVM with Kotlin Coroutines & Flows
- **Database**: Room Persistence Library with pre-packaged SQLite asset (`xray.db`)

## Build & Run

1. Open this repository in Android Studio (JBR 17/21).
2. Allow Gradle sync.
3. Run `assembleDebug` or `bundleRelease` to build the app.
