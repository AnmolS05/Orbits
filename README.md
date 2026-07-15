<div align="center">
  <img src="https://img.icons8.com/color/96/000000/orbit.png" alt="Orbits Logo" width="80" />
  <h1>Orbits</h1>
  <p><b>A lightning-fast Android app to seamlessly save and organize LinkedIn profiles without interrupting your workflow.</b></p>

  <p>
    <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white" alt="Platform" />
    <img src="https://img.shields.io/badge/Built_with-Jetpack_Compose-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose" />
    <img src="https://img.shields.io/badge/Architecture-Clean_Architecture-FF6C37?style=flat-square" alt="Clean Architecture" />
    <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white" alt="Kotlin" />
  </p>
</div>

---

## 🚀 Overview

**Orbits** is designed for recruiters, sales professionals, and networkers who frequently browse LinkedIn and need a frictionless way to save profiles. 

Instead of jumping between apps or taking screenshots, Orbits integrates directly into Android's native Share Sheet. You can save a profile invisibly in the background with a single tap, or bring up a quick overlay to add context tags—all without leaving LinkedIn!

## ✨ Key Features

### 1. Seamless LinkedIn Integration (Share Targets)
- ⚡ **Quick Orbit (Invisible Save):** Tap "Quick Orbit" from the share sheet to instantly save the profile in the background. Orbits smartly updates timestamps and prevents duplicates.
- 🏷️ **Tag & Save (Translucent Overlay):** Tap "Tag & Save" to pull up a lightweight bottom sheet *over* LinkedIn. Quickly select context tags (e.g., `Met at Event`, `Referred`, `Hire`, `Follow up`) and add personal notes before saving.

### 2. Instagram-Inspired Dark Aesthetic
- 🖤 **Pure Black Theme:** An elegant, high-contrast UI designed to look professional and save battery.
- 📱 **Compact & Dense UI:** View 5-6 profiles on a single screen with highly optimized row layouts.
- ✨ **Rich Visuals:** Avatar initials with dynamic gradient rings for active/pinned contacts, and interactive micro-animations.

### 3. Intuitive Feed & Management
- 🗂️ **Main Feed:** A chronological list of your saved contacts with quick deep links back to their native LinkedIn profiles.
- 👆 **Swipe Gestures:** Swipe left to instantly archive, or swipe right to delete (with an undo confirmation).
- 🔍 **Search & Filter:** A dedicated search tab with horizontal story-like "tag pills" to rapidly filter your database.
- 📋 **Clipboard Sniffing:** Automatically detects LinkedIn URLs copied to your clipboard and prompts a quick-save when you open the app.

---

## 🛠 Tech Stack

Built entirely with modern Android development standards:
- **UI Toolkit:** Jetpack Compose (Material 3)
- **Architecture:** Unidirectional Data Flow (UDF) + MVVM + Clean Architecture
- **Local Storage:** Room Database (SQLite)
- **Asynchrony:** Kotlin Coroutines & StateFlow
- **Navigation:** Jetpack Navigation Compose

---

## 🚀 Getting Started

To build and run Orbits locally:

1. **Clone the repository:**
   ```bash
   git clone https://github.com/AnmolS05/Orbits.git
   ```
2. Open the project in **Android Studio (Giraffe or newer)**.
3. Allow Gradle to sync and download all dependencies.
4. Hit **Run** (`Shift + F10`) to deploy to your emulator or physical Android device.

---

<div align="center">
  <p>Built with ❤️ to make networking frictionless.</p>
</div>
