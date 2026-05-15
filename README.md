# Grama Katha

Grama Katha is an Android app for village shopkeepers who still manage customer credit in physical notebooks. It replaces fragile paper-based records with a simple offline digital ledger for tracking dues, recording payments, and sending reminders through WhatsApp or SMS.

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

- Customer management with name and phone number
- Live due dashboard sorted by balance
- Search customers by name
- Quick add credit and payment entries
- Detailed customer transaction history
- Real-time net balance updates using ViewModel + Room flows
- WhatsApp/SMS reminder sharing with customizable message template
- Shop profile and settings management
- UPI payment mode selection and UPI QR support flow
- Daily and monthly business summaries in text format
- CSV ledger export for sharing records
- Kannada localization support
- Delete customer and delete transaction actions

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

## Success Criteria Alignment

This project was built to satisfy the given academic evaluation goals.

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
- `Android Intents`
- `FileProvider` for CSV sharing
- `ZXing` for QR code generation

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
  Customer list, search, summary card, quick actions, daily/monthly reports

- `Add/Edit Customer Screen`
  Add customer details with basic validation

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

## Build Readiness

The project is structured as a complete Android Studio app with Gradle configuration, Room database integration, Compose UI, resources, icons, and generated APK output during development.

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

## Evaluation-Focused Summary

Grama Katha is an Android-based offline digital ledger application for village shopkeepers who currently track customer credit using paper notebooks. The app enables customer management, real-time due calculation, credit/payment transaction logging, daily and monthly report generation, WhatsApp/SMS reminder sharing, and CSV ledger export. It is built using Kotlin, Jetpack Compose, Room Database, and MVVM architecture, with a strong focus on usability, reliability, and micro-enterprise financial digitization.
