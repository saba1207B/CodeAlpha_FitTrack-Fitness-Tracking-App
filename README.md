# FitTrack - Native Android Fitness Tracking App

FitTrack is a modern, fully-offline native Android fitness tracker application built using Kotlin, Jetpack Compose, Material 3, and Room Database.

## Features

- **Fitness Dashboard**:
  - Live summary of daily steps, burned calories, workout duration, and workout counts.
  - Interactive Daily Step Tracker with animated circular progress ring against customizable step goals.
  - Quick-log and view today's workout activities.

- **Workout Logging**:
  - Choose exercise types: Walking, Running, Cycling, Swimming, Gym, Strength Training, Yoga, Cardio, and more.
  - Input workout duration in minutes with smart automated calorie burn estimation.
  - Custom date selector and optional workout notes.
  - Real-time input validation.

- **Workout History**:
  - Chronological list of all logged workouts with timestamps, durations, and calorie values.
  - Filter workouts by exercise type (e.g., Running, Cycling, Strength Training).
  - Edit and delete functionality with confirmation dialogs.

- **Weekly Statistics & Analytics**:
  - 7-day fitness trends and totals (Weekly Steps, Total Calories, Total Duration, Workouts).
  - Custom Canvas-rendered animated weekly bar charts:
    - Weekly Calories Burned
    - Weekly Workout Duration
    - Weekly Steps Progress
  - Day-by-day table breakdown with inspector.

- **Settings & Privacy**:
  - Adjustable daily step goal with quick presets (e.g. 6,000, 8,000, 10,000, 12,000).
  - 100% offline local Room persistence — no cloud accounts, third-party backend, or internet permissions needed.
  - Data management controls: Reset data and optional demo data generator.

## Technology Stack

- **UI**: Jetpack Compose, Material Design 3, Dynamic Theming
- **Language**: Kotlin & Coroutines / StateFlow
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Persistence**: Room Database (SQLite) + Jetpack DataStore Preferences
- **Navigation**: Jetpack Navigation Compose with bottom navigation bar

## Building and Running

```bash
# Build Debug APK
./gradlew assembleDebug

# Run Unit & Robolectric Tests
./gradlew testDebugUnitTest
```
