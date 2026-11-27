# XRayDB Pocket

[![Version](https://img.shields.io/badge/version-1.5.0%20(6)-blue.svg)](app/build.gradle.kts)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg)](app/build.gradle.kts)
[![License](https://img.shields.io/badge/license-MIT-orange.svg)](LICENSE)

An offline-first Kotlin + Jetpack Compose Android reference application designed for synchrotron researchers, beamline scientists, X-ray spectroscopists, and materials characterization experts.

---

## 🌟 Key Features

- **Optimized Periodic Table & Beamline Favorites**:
  - Clean 5-column responsive grid layout optimized for phone and tablet screens.
  - Color-coded by state of matter at standard conditions (Gas, Liquid, Solid) with tuned contrast for Light and Dark Hutch themes.
  - **Beamline Favorites ⭐**: One-tap star marking for frequently analyzed beamline elements with persistent storage and dedicated favorites filter mode.
  - Dynamic font & padding auto-scaling on small-screen devices to prevent text clipping.

- **Global App Bar Search**:
  - Live top bar search supporting element symbols ($\text{Fe}$), element names (*Iron*), atomic numbers ($Z=26$), spectral line notation ($K\alpha_1$), or photon energies ($6403.8 \text{ eV}$) with instant navigation.

- **Fundamental X-Ray Physics Parameters & Yields**:
  - **Krause / Bambynek Fluorescence Yields**: Calculated $K$-shell ($\omega_K$) and $L_3$-subshell ($\omega_{L3}$) radiative decay probabilities for all elements.
  - **Natural Core-Hole Lifetime Widths**: $K$ and $L_3$ level Lorentzian FWHM broadenings ($\Gamma_K, \Gamma_{L3}$ in $\text{eV}$) for EXAFS/XANES fitting and XPS curve resolution.
  - **Siegbahn Relative Intensity Ratios**: Relativistic spin-orbit couplings ($K\alpha_2 / K\alpha_1 \approx 0.50$) and element-dependent $K\beta / K\alpha$ branching ratios.

- **Authoritative X-Ray Database (794+ Records)**:
  - **280** Solid-state core-level binding energies & XPS lines across 80 elements.
  - **366** Characteristic X-ray emission lines with Greek letter notation ($K\alpha_1, K\alpha_2, L\alpha, L\beta, L\gamma, M\alpha$) and precise energies.
  - **26** Auger electron emission transitions ($KLL, LMM, MNN$).
  - **81** Gas-phase and vapor XPS binding energies for molecular gases and volatile solvents (Tartu / NIST).
  - **41** X-ray absorption edge threshold energies ($K, L_1, L_2, L_3$ edges) for XAS / EXAFS / XANES spectroscopy.

- **Monochromator Crystal Bragg Angle Calculator**:
  - Double-crystal monochromator (DCM) interplanar $d$-spacing reference: $\text{Si}(111)$, $\text{Si}(311)$, $\text{Si}(220)$, $\text{Si}(400)$, $\text{Ge}(111)$, $\text{Ge}(220)$, $\text{Diamond}(111)$, $\text{LiF}(200)$.
  - Live Bragg angle solver: $\theta = \arcsin\left(\frac{\lambda}{2d}\right)$ with limits checking and $2\theta$ readout.

- **Standard Lab X-Ray Target Anodes & Reverse Energy Lookup**:
  - One-tap quick selector for common laboratory X-ray tube anode emission lines ($\text{Cu } K\alpha_1$, $\text{Cu } K\beta_1$, $\text{Mo } K\alpha_1$, $\text{Cr } K\alpha_1$, $\text{Fe } K\alpha_1$, $\text{Co } K\alpha_1$, $\text{Ag } K\alpha_1$, $\text{W } L\alpha_1$, $\text{Ga } K\alpha_1$).
  - Search transitions and edges by photon energy ($\text{eV}$ / $\text{keV}$) with tunable tolerance ($\pm \text{eV}$), live wavelength ($\lambda$) and frequency ($\nu$) conversions.

- **XRF / EDX Peak Identifier**:
  - Input experimental peak energies to instantly identify matching elemental emission lines with direct element navigation.

- **Expanded X-Ray Attenuation & Transmission Calculator**:
  - Beer-Lambert attenuation calculator supporting 26 pre-set filter/window/coating materials ($\text{Be}$, Diamond, Kapton, Mylar, $\text{Si}$, $\text{Ge}$, $\text{Al}$, $\text{Ti}$, $\text{Mo}$, $\text{Ag}$, $\text{Pd}$, $\text{Cu}$, $\text{Au}$, $\text{C}$, $\text{BN}$, Water, Air, PMMA, Teflon, Fused Silica, Pyrex, $\text{Pt}$, $\text{Rh}$, $\text{Ru}$, $\text{TiN}$, $\text{SiC}$).
  - Custom compound calculator ($\text{SiO}_2$, $\text{GaAs}$, $\text{Fe}_2\text{O}_3$, $\text{Al}_2\text{O}_3$, $\text{CeO}_2$, $\text{PbS}$, $\text{CdTe}$, $\text{InP}$, $\text{Si}_3\text{N}_4$, YBCO, Teflon, Polypropylene, GaN, ZnO, SiC, Perovskite, PZT) with accurate molar mass resolution and safety against NaN errors.

- **Lab Note Markdown Export**:
  - Copy formatted element summaries including fundamental physics parameters and spectral lines directly to clipboard for Electronic Lab Notebooks (ELN) and publication drafts.

- **Modern Android Architecture & UX**:
  - Material 3 design, full edge-to-edge layout, system back-gesture integration (`BackHandler`) compatible with Samsung One UI & Android 14+.
  - Custom Bohr/Moseley atomic transition vector adaptive app icon.

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
