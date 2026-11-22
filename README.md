# XRayDB Pocket

[![Version](https://img.shields.io/badge/version-1.4.0%20(5)-blue.svg)](app/build.gradle.kts)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg)](app/build.gradle.kts)
[![License](https://img.shields.io/badge/license-MIT-orange.svg)](LICENSE)

An offline-first Kotlin + Jetpack Compose Android reference application designed for synchrotron researchers, beamline scientists, X-ray spectroscopists, and materials characterization experts.

---

## 🌟 Key Features

- **Optimized Periodic Table (118 Elements)**:
  - Clean 5-column responsive grid layout optimized for phone and tablet screens.
  - Color-coded by state of matter at standard conditions (Gas, Liquid, Solid) with optimized contrast for both Light and Dark Hutch themes.
  - Dynamic font & padding auto-scaling on small-screen devices to prevent text clipping.

- **Authoritative X-Ray Database (794+ Records)**:
  - **280** Solid-state core-level binding energies & XPS lines across 80 elements.
  - **366** Characteristic X-ray emission lines with Greek letter notation ($K\alpha_1, K\alpha_2, L\alpha, L\beta, L\gamma, M\alpha$) and precise energies.
  - **26** Auger electron emission transitions ($KLL, LMM, MNN$).
  - **81** Gas-phase and vapor XPS binding energies for molecular gases and volatile solvents (Tartu / NIST).
  - **41** X-ray absorption edge threshold energies ($K, L_1, L_2, L_3$ edges) for XAS / EXAFS / XANES spectroscopy.

- **Reverse Energy Lookup**:
  - Search transitions and absorption edges by photon energy ($\text{eV}$ / $\text{keV}$) with tunable energy tolerance ($\pm \text{eV}$).
  - Live conversions to photon wavelength ($\lambda$ in $\text{Å}$ / $\text{nm}$) and frequency ($\nu$ in $\text{Hz}$).

- **XRF / EDX Peak Identifier**:
  - Input experimental peak energies to instantly identify matching elemental emission lines.
  - One-tap navigation directly to element detail cards.

- **X-Ray Attenuation & Transmission Calculator**:
  - Beer-Lambert attenuation calculator supporting 18 pre-set synchrotron filter/window materials ($\text{Be}$, Diamond, Kapton, Mylar, $\text{Si}$, $\text{Ge}$, $\text{Al}$, $\text{Ti}$, $\text{Mo}$, $\text{Ag}$, $\text{Pd}$, $\text{Cu}$, $\text{Au}$, $\text{C}$, $\text{BN}$, $\text{Water}$, $\text{Air}$, $\text{PMMA}$).
  - Custom compound calculator ($\text{SiO}_2$, $\text{GaAs}$, $\text{Fe}_2\text{O}_3$, $\text{Al}_2\text{O}_3$, $\text{CeO}_2$, $\text{PbS}$, $\text{CdTe}$, $\text{InP}$, $\text{Si}_3\text{N}_4$, $\text{YBCO}$) with accurate molar mass resolution and safety against NaN errors.

- **Lab Note Markdown Export**:
  - Copy formatted element summaries or individual spectral lines to clipboard for Electronic Lab Notebooks (ELN) and publication drafts.

- **Modern Android Architecture & UX**:
  - Built with Jetpack Compose and Material 3 design guidelines.
  - Full edge-to-edge display support and system back-gesture integration (`BackHandler`) compatible with Samsung One UI & Android 14+.
  - Custom Bohr/Moseley atomic transition vector adaptive app icon.

- **Privacy & Policy Compliant**:
  - In-app links and included documentation for [Privacy Policy](PRIVACY_POLICY.md) (`privacy.html`) and [Terms of Service](TERMS_OF_SERVICE.md) (`terms.html`).
  - Google AdMob integration with tuned R8 / ProGuard keep rules for release builds.

---

## 🛠 Tech Stack

- **Language**: Kotlin 2.0.21
- **UI Toolkit**: Jetpack Compose (Material 3)
- **Architecture**: MVVM with Kotlin Coroutines & StateFlow
- **Database**: Room Persistence Library 2.6.1 with pre-packaged SQLite asset (`xray.db`)
- **Build System**: AGP (Android Gradle Plugin) targeting Android 16 (API Level 36, minSdk 26)
- **Code Shrinking**: ProGuard / R8 optimized release build configuration

---

## 🚀 Build & Release

### Prerequisites
- Android Studio Ladybug | 2024.2.1 or newer (JDK 17/21).
- Android SDK 36 installed.

### Build Commands

```bash
# Debug Build
./gradlew assembleDebug

# Release Build (AAB / Signed APK)
./gradlew bundleRelease
./gradlew assembleRelease
```

---

## 📄 License & Legal

Distributed under the MIT License. See [PRIVACY_POLICY.md](PRIVACY_POLICY.md) and [TERMS_OF_SERVICE.md](TERMS_OF_SERVICE.md) for privacy and terms of service details.
