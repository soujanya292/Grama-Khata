# Grama Katha

Grama Katha is an Android app for village shopkeepers who still manage customer credit in physical notebooks. It replaces fragile paper-based records with a simple offline digital ledger for tracking dues, recording payments, and sending reminders through WhatsApp or SMS.

## Project Links

- Public GitHub repository: `soujanya292/Grama-Khata`
- Live demo: [Appetize Preview](https://appetize.io/app/b_z2srbbnax75tz77kd3suxc7skm)
- Source code included: Yes
- README included: Yes
- Gradle configuration included: Yes
- Android resources included: Yes
- Buildable Android Studio project: Yes

## Problem Statement

Small grocery stores in villages often maintain informal credit using a `Vahi` or handwritten ledger. This creates several issues:

- physical books can be lost or damaged
- customers and shopkeepers may disagree on the current due amount
- reminding customers is manual and inconsistent
- balances are difficult to track during busy shop hours

Grama Katha solves this by turning the trust-based `Give / Take` workflow into a simple mobile experience designed for small merchants.

## Vision

The app is built as a simplified digital ledger for rural micro-finance use cases. Instead of complex accounting screens, it focuses on:

- fast customer lookup
- one-hand transaction entry
- instant due calculation
- reminder generation with minimal effort
- offline-first reliability

## Core Features

- Customer management with name, phone number, and lender photo
- Live due dashboard sorted by balance
- Search customers by name
- Quick add credit and payment entries
- Detailed customer transaction history
- Camera-based lender photo capture for easier customer identification
- Real-time net balance updates using ViewModel + Room flows
- WhatsApp/SMS reminder sharing with customizable message template
- Shop profile and settings management
- UPI payment mode selection and UPI QR support flow
- Daily and monthly business summaries in text format
- CSV ledger export for sharing records
- Kannada localization support
- Delete customer and delete transaction actions

## Source Code Overview

The repository contains full Android source code, configuration, assets, and resources required to run and review the project.

### Main source folders

- `app/src/main/java/com/gramakhata/data`
  Database, repository, entities, and DAO files

- `app/src/main/java/com/gramakhata/ui/screens`
  Compose UI screens for dashboard, customer form, settings, and transactions

- `app/src/main/java/com/gramakhata/ui/viewmodel`
  ViewModels for business logic and live state updates

- `app/src/main/java/com/gramakhata/util`
  Utility classes for reminders, CSV export, and preferences

- `app/src/main/res`
  Strings, themes, icons, localization resources, and XML configuration

### Important code files

- `app/src/main/java/com/gramakhata/MainActivity.kt`
- `app/src/main/java/com/gramakhata/data/GramaKhataDatabase.kt`
- `app/src/main/java/com/gramakhata/data/GramaKhataRepository.kt`
- `app/src/main/java/com/gramakhata/ui/screens/DashboardScreen.kt`
- `app/src/main/java/com/gramakhata/ui/screens/TransactionScreen.kt`
- `app/src/main/java/com/gramakhata/ui/viewmodel/DashboardViewModel.kt`
- `app/src/main/java/com/gramakhata/ui/viewmodel/TransactionViewModel.kt`

## User Flow

1. The shopkeeper opens the dashboard and sees all customers with their current balances.
2. A new customer can be added with a name and optional phone number.
3. Tapping a customer opens the ledger screen for that person.
4. The shopkeeper records either:
   - `Udari (+)` when goods are given on credit
   - `Payment (-)` when money is collected
5. The customer balance updates instantly after each entry.
6. The shopkeeper can send a due reminder through WhatsApp or SMS.
7. Reports can be viewed as text summaries and ledgers can be exported as CSV.

## Project Goals Alignment

This project is designed to meet the core goals of the Grama-Khata problem statement.

- `Net Due updates instantly`
  The balance is derived from Room transaction data and exposed through `StateFlow` in the ViewModel.

- `Daily Collection Report in text format`
  The dashboard provides daily and monthly text summaries for the business.

- `Usable with one hand`
  The app uses a simple dashboard, large action buttons, quick-add flows, and direct customer access from the home screen.

## Tech Stack

- `Kotlin`
- `Jetpack Compose`
- `Room Database`
- `MVVM Architecture`
- `StateFlow / Flow`
- `Navigation Compose`
- `Activity Result API`
- `Android Camera Integration`
- `Android Intents`
- `FileProvider` for CSV sharing
- `FileProvider` for captured customer photos
- `Coil` for image loading in Compose
- `ZXing` for QR code generation

## Configuration Files Present

- `settings.gradle.kts`
- `build.gradle.kts`
- `app/build.gradle.kts`
- `gradle/libs.versions.toml`
- `gradle.properties`
- `gradlew`
- `gradlew.bat`
- `AndroidManifest.xml`

## Architecture Overview

The app follows a clean Android structure:

- `UI Layer`
  Compose screens render state and collect user actions.

- `ViewModel Layer`
  Handles business logic such as balance calculation, report generation, and transaction operations.

- `Repository Layer`
  Provides a clean interface between the UI logic and Room database.

- `Persistence Layer`
  Room entities, DAOs, and the database class manage offline storage.

## Data Model

### Customer

- `id`
- `name`
- `phone`
- `photoUri`
- `createdAt`

### Transaction

- `id`
- `customerId`
- `amount`
- `type`
- `paymentMethod`
- `upiApp`
- `note`
- `timestamp`

### Transaction Logic

- `CREDIT` increases the customer due amount
- `PAYMENT` decreases the customer due amount

The current balance is not stored as a fixed field. It is calculated from transaction history in real time.

## Project Structure

```text
Grama-Khata/
├── app/
│   ├── src/main/java/com/gramakhata/
│   │   ├── data/
│   │   │   ├── dao/
│   │   │   ├── entity/
│   │   │   ├── GramaKhataDatabase.kt
│   │   │   └── GramaKhataRepository.kt
│   │   ├── ui/
│   │   │   ├── navigation/
│   │   │   ├── screens/
│   │   │   └── viewmodel/
│   │   ├── util/
│   │   └── MainActivity.kt
│   └── src/main/res/
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## Important Screens

- `Dashboard Screen`
  Customer list with profile photos, search, summary card, quick actions, daily/monthly reports

- `Add/Edit Customer Screen`
  Add customer details, basic validation, and capture a lender photo using the device camera

- `Transaction Screen`
  Record udari/payment, view ledger, delete transactions, send reminders, export CSV

- `Settings Screen`
  Configure shop name, UPI ID, reminder template, and settlement reminder buffer

## Build And Run

### Requirements

- Android Studio Hedgehog or newer
- Android SDK 34
- JDK 11+

### Steps

1. Clone the repository:

```bash
git clone https://github.com/soujanya292/Grama-Khata.git
cd Grama-Khata
```

2. Open the project in Android Studio.

3. Let Gradle sync completely.

4. Run the `app` configuration on:
   - an Android emulator
   - or a physical Android device

### Command line build

```bash
./gradlew assembleDebug
```

This produces a debug APK under:

```text
app/build/outputs/apk/debug/
```

## Build Readiness

The project is structured as a complete Android Studio app with Gradle configuration, Room database integration, Compose UI, resources, icons, and generated APK output during development.

## Repository Checklist

- Repository is public
- Source code is present
- README explains the project
- Setup and run steps are documented
- Gradle dependency files are included
- Android project structure is organized
- Custom project naming is used throughout the repo
- Core implementation is domain-specific, not a starter template

## Screenshots / Demo

- Interactive demo: [https://appetize.io/app/b_z2srbbnax75tz77kd3suxc7skm](https://appetize.io/app/b_z2srbbnax75tz77kd3suxc7skm)
- Additional screenshots or a short demo video can be added later.

## Why This Project Is Meaningful

This is not a generic demo app. It addresses a real rural commerce workflow:

- informal credit management
- trust-based customer relationships
- low-tech merchant usability
- offline-first record safety
- simple payment reminder support

The app contributes to financial digitization for micro-enterprises and helps reduce disputes and record loss in village retail environments.

## Future Enhancements

- customer photo capture from camera
- PDF export for customer ledgers and daily reports
- backup and restore support
- multilingual reminder templates
- voice input for faster transaction entry
- cloud sync across multiple devices
- due date reminders and notification scheduling

## Project Summary

Grama Katha is an Android-based offline digital ledger application for village shopkeepers who currently track customer credit using paper notebooks. The app enables customer management, real-time due calculation, credit/payment transaction logging, daily and monthly report generation, WhatsApp/SMS reminder sharing, and CSV ledger export. It is built using Kotlin, Jetpack Compose, Room Database, and MVVM architecture, with a strong focus on usability, reliability, and micro-enterprise financial digitization.
