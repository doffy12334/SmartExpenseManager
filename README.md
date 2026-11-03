# Smart Expense Manager

An offline-first Android application for personal finance management, built with Kotlin and XML.

Smart Expense Manager is designed to make everyday financial tracking easier through organized transaction records, monthly spending insights, category-based budgets, and receipt-assisted data entry.

This project brings together Android Jetpack libraries, reactive state management, on-device text recognition, and background processing in a single-module application.

> This application is under development. The capabilities below describe the project’s intended scope.

## Features

- **Income and expense tracking** — Record transactions with an amount, category, date, and note.
- **Transaction history** — Search and filter records by month, category, and transaction type.
- **Monthly financial overview** — Review income, expenses, and balance.
- **Budget management** — Set monthly category limits and monitor spending against them.
- **Spending visualization** — Explore financial trends through custom bar and donut charts.
- **Receipt scanning** — Extract transaction details from receipt images and review the results before saving.
- **Offline access** — Keep financial records available locally without an internet connection.
- **Background synchronization** — Synchronize local changes with a REST backend when connectivity is available.
- **Personalization** — Persist theme and application preferences.
- **Optional extensions** — Biometric app locking and CSV export.

## Tech Stack

| Category | Technologies |
| --- | --- |
| Language | Kotlin |
| UI | XML, Fragments, Material Components, ConstraintLayout |
| View access | ViewBinding |
| Lists | RecyclerView, ListAdapter, DiffUtil |
| Architecture | MVVM + Repository |
| State management | ViewModel, SavedStateHandle, StateFlow |
| Asynchronous operations | Kotlin Coroutines, Flow |
| Lifecycle | AndroidX Lifecycle |
| Navigation | Jetpack Navigation Component |
| Dependency injection | Hilt |
| Annotation processing | Kotlin Symbol Processing (KSP) |
| Local persistence | Room |
| Preferences | Preferences DataStore |
| Camera | CameraX |
| Text recognition | ML Kit Text Recognition |
| Networking | Retrofit, OkHttp, Gson |
| Background work | WorkManager |
| Biometric authentication | AndroidX Biometric |
| Charts | Custom Views, Canvas, Paint |
| Icons | Android VectorDrawable |
| Build configuration | Gradle Kotlin DSL, Version Catalog |

### Testing Tools

- **JUnit** for local unit tests.
- **Coroutines Test** for coroutine scheduling and asynchronous behavior.
- **MockK** for test doubles where mocking is appropriate.
- **Turbine** for testing Flow emissions.
- **MockWebServer** for HTTP response simulation.
- **Espresso** for UI interactions.
- **Room Testing** for database migration tests.
- **WorkManager Testing** for background task tests.

## Architecture

The project uses a single Gradle module, `:app`, organized into UI and data layers.

```text
Activity / Fragment
        │
        ▼
    ViewModel
        │
        ▼
    Repository
        │
        ├── Local data sources
        │   ├── Room
        │   └── DataStore
        │
        └── Remote data source
            └── Retrofit / OkHttp
```

### UI Layer

Activities and Fragments render screen state and forward user actions to ViewModels. ViewModels manage state and coordinate operations through repositories.

Coroutines and Flow support asynchronous operations and reactive updates, with lifecycle-aware collection in the UI.

### Data Layer

Repositories provide access to application data, coordinate local and remote sources, and map database entities and network DTOs into application models.

Room is the intended source of truth for financial records. Synchronization updates local storage, allowing the UI to observe data through the same repository APIs regardless of connectivity.

### Business Logic

The initial architecture keeps simple operations in ViewModels and repositories. A Domain layer and UseCases are introduced when business rules require independent testing or reuse, such as budget calculations or receipt interpretation.

### Background Work

WorkManager provides the foundation for persistent synchronization with connectivity constraints and retry policies. The synchronization design accounts for stable record identifiers and deletion markers.

## Project Structure

```text
app/src/main/
├── java/com/example/smartexpensemanager/
│   ├── MainActivity.kt
│   ├── ui/
│   │   ├── onboarding/
│   │   ├── dashboard/
│   │   ├── transactions/
│   │   ├── editor/
│   │   ├── budget/
│   │   ├── scan/
│   │   ├── settings/
│   │   └── common/
│   ├── data/
│   │   ├── model/
│   │   ├── repository/
│   │   ├── local/
│   │   │   ├── dao/
│   │   │   ├── entity/
│   │   │   ├── converter/
│   │   │   └── preferences/
│   │   ├── remote/
│   │   │   ├── api/
│   │   │   └── dto/
│   │   ├── mapper/
│   │   └── sync/
│   ├── di/
│   ├── worker/
│   └── util/
└── res/
    ├── drawable/
    ├── layout/
    └── values/
```

Dependency and plugin versions are managed centrally in `gradle/libs.versions.toml`.

## Design Principles

- **Offline-first data access:** Local persistence forms the foundation of the financial tracking experience.
- **Accurate monetary representation:** Monetary values use `Long` in the smallest currency unit instead of floating-point arithmetic.
- **User-reviewed OCR:** Recognized receipt data requires confirmation before becoming a transaction.
- **Separation of concerns:** UI rendering, screen state, data access, and storage responsibilities remain distinct.
- **Testable business rules:** Financial calculations and parsing logic are kept suitable for independent testing.

## Getting Started

### Requirements

- Android Studio compatible with the project’s Android Gradle Plugin.
- Android SDK API 37.
- A compatible Gradle JDK.
- An emulator or device running Android 7.0 (API 24) or later.

Java source and target compatibility are configured to Java 17. The JDK used to run Gradle must also satisfy the project’s Gradle and Android Gradle Plugin requirements.

### Run the Application

1. Clone this repository.
2. Open the root project directory in Android Studio.
3. Install the required Android SDK components.
4. Sync the project with Gradle.
5. Select the `app` run configuration.
6. Run on an emulator or connected device.

No backend credentials are required to run the current application.

### Build

```bash
# macOS / Linux
./gradlew :app:assembleDebug

# Windows
.\gradlew.bat :app:assembleDebug
```

The debug APK is generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Testing

Run local unit tests:

```bash
# macOS / Linux
./gradlew :app:testDebugUnitTest

# Windows
.\gradlew.bat :app:testDebugUnitTest
```

Run instrumented tests with a connected emulator or device:

```bash
# macOS / Linux
./gradlew :app:connectedDebugAndroidTest

# Windows
.\gradlew.bat :app:connectedDebugAndroidTest
```

The testing approach focuses on monetary calculations, budget rules, repository mapping, database behavior, receipt parsing, and critical user flows.

## Development Notes

The repository currently provides the application foundation and initial onboarding interface. The technology stack is configured for the broader financial tracking scope; dependency availability does not imply completed feature integration.

The current x86_64 emulator reports 16 KB page-size compatibility warnings for some native dependencies. Compatibility must be addressed and verified before release.

## Design

The interface is developed from Figma designs and implemented using native Android XML layouts and vector resources.

[View the Figma design](https://www.figma.com/design/YydkfW96kGL24Tbr9Rt7Xl/Untitled?node-id=1-240)
