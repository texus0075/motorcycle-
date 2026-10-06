# 🏍️ MotoScope - Global Motorcycle Catalog & AI Buying Advisor

MotoScope is a production-grade Android application built with **Jetpack Compose**, **Firebase Full-Stack Backend**, and **Google Gemini 2.5 Flash AI**. It serves as an encyclopedia and intelligent decision platform for motorcycle enthusiasts, featuring comprehensive engineering specifications, interactive comparisons, EMI estimation, cloud sync, and conversational motorcycle consulting.

---

## ✨ Features & Architecture

### 1. 🔥 Full-Stack Firebase Backend
- **Firebase Project:** `motohub-live-1a2b3c`
- **Authentication (`FirebaseAuthService.kt`):**
  - Email & Password sign-in / registration
  - Phone SMS OTP authentication
  - Google Sign-In with Credential Manager API
  - Anonymous Guest authentication
- **Cloud Firestore (`firestore.rules`):**
  - Public read access for global motorcycle catalogs and brand history
  - User-specific private subcollections for saved favorites, garage sync, and personal ratings
  - Production security rules preventing unauthorized write or schema tampering
- **Firebase Cloud Storage (`storage.rules`):**
  - Authenticated motorcycle image uploads and asset hosting
- **Firebase Crashlytics & FCM (`MotoFirebaseMessagingService.kt`):**
  - Real-time crash monitoring and push notifications for motorcycle launches

### 2. 🤖 Gemini 2.5 Flash AI Intelligence (`GeminiAiService.kt`)
- **Moto AI Advisor (`AiAdvisorDialog.kt`):**
  - Real-time natural language consultation powered by Google Gemini
  - Tailored recommendations based on rider height, budget, experience level, and daily commuting vs highway touring goals
- **AI Shootout Verdict (`ComparisonScreen.kt`):**
  - Multi-bike head-to-head dyno and ergonomic analysis comparing power, torque, weight, seat height, and running costs
- **AI Rider Insights (`MotorcycleDetailScreen.kt`):**
  - Ergonomics evaluation, pillion comfort analysis, and real-world maintenance cost forecasts

### 3. 🛠️ Core Application Features
- **Offline-First Room Database:** Complete catalog preloaded and instantly available offline with real-time cloud synchronization.
- **3-Way Comparison Matrix:** Compare up to 3 motorcycles side-by-side across 25+ certified engineering attributes with radar scoring.
- **EMI & On-Road Estimator (`EmiCalculatorCard.kt`):** Dynamic down payment, loan tenure, and interest calculation.
- **Admin Studio PIN Lock (`AdminScreen.kt`):** Secure catalog management secured by PIN authentication.
- **Community Rating & Reviews:** 5-star user reviews stored locally and backed up to Firebase Firestore.
- **Share Comparison:** Share technical comparisons via Android `Intent.ACTION_SEND`.

---

## 🚀 Getting Started

### Prerequisites
- [Android Studio Ladybug / Meerkat or newer](https://developer.android.com/studio)
- Android SDK 36 (minSdk 24)
- Java 11 or 17

### Setup Instructions
1. **Clone the repository:**
   ```bash
   git clone https://github.com/texus0075/motorcycle-.git
   cd motorcycle-
   ```
2. **Configure Environment Secrets:**
   Create a `.env` file in the root directory (based on `.env.example`):
   ```env
   GEMINI_API_KEY=YOUR_GEMINI_API_KEY_HERE
   ```
   *(The Secrets Gradle Plugin automatically reads `.env` and injects keys into `BuildConfig.GEMINI_API_KEY`)*
3. **Firebase Configuration:**
   - The app is registered with Firebase project `motohub-live-1a2b3c`.
   - `app/google-services.json` is pre-configured.
4. **Build and Run:**
   - Open the project in Android Studio.
   - Sync Gradle project files.
   - Run on an emulator or physical device.
