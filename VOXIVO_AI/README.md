# VOXIVO AI — YOUR VOICE. YOUR INTELLIGENCE.

VOXIVO AI is an advanced, production-grade Android Voice Assistant and Reminder application built with Kotlin and Jetpack Compose.

## 🚀 Key Architecture & Technology Stack

- **Platform:** Android (Min SDK: 26, Target SDK: 35)
- **Primary Language:** Kotlin (Clean Architecture + MVVM)
- **UI Framework:** Jetpack Compose + Material3 + Cyber-Glassmorphism Design System
- **Database (Offline-First):** Android Room Database with 7 Entities:
  1. `ReminderEntity`
  2. `TaskEntity`
  3. `UserProfileEntity`
  4. `RecurrenceEntity`
  5. `ReminderHistoryEntity`
  6. `VoicePreferencesEntity`
  7. `AppSettingsEntity`
- **Cloud Synchronization:** Firebase Firestore + Firebase Authentication (offline-tolerant)
- **AI Engine:** Google Gemini AI API with natural language understanding & fallback NLP
- **Voice Recognition:** Android `SpeechRecognizer` (multilingual: English, Telugu, Hindi, Auto)
- **Speech Synthesis:** Android `TextToSpeech` with 6 distinct voice personalities:
  - **Friendly** (warm & approachable)
  - **Professional** (crisp & concise)
  - **Calm** (soothing & mindful)
  - **Motivate** (energetic & action-oriented)
  - **Polite** (courteous & considerate)
  - **Firm** (authoritative & insistent)
- **Alarms & Scheduling:**
  - Android 12+ / 14+ `SCHEDULE_EXACT_ALARM` & `USE_EXACT_ALARM` support
  - Auto-rescheduling on reboot with `RECEIVE_BOOT_COMPLETED`
  - High-priority notification channel with custom vibration and alarm sound
  - Full-screen `LiveAlarmActivity` with digital clock, animated AI Orb, soundwaves, snooze (+5m, +10m, +15m) and dismiss controls.

## 📁 Project Structure

```
VOXIVO_AI/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/voxivo/ai/
│       │   │   ├── VoxivoApplication.kt
│       │   │   ├── MainActivity.kt
│       │   │   ├── data/
│       │   │   │   ├── local/ (VoxivoDatabase, DAOs, Entities)
│       │   │   │   ├── remote/ (FirebaseSyncManager, GeminiAiService)
│       │   │   │   └── repository/ (Repositories implementation)
│       │   │   ├── domain/
│       │   │   │   ├── model/ (Reminder, Task, VoicePersonality, etc.)
│       │   │   │   └── repository/ (Domain contracts)
│       │   │   ├── service/
│       │   │   │   ├── alarm/ (AlarmScheduler, AlarmReceiver, BootCompletedReceiver, NotificationHelper)
│       │   │   │   └── voice/ (SpeechRecognizerManager, TextToSpeechManager)
│       │   │   └── ui/
│       │   │       ├── MainAppScaffold.kt
│       │   │       ├── theme/ (Color, Theme, Type, Shape)
│       │   │       ├── components/ (AiOrb, CyberGlassCard, SoundWaveVisualizer, SquircleAvatar)
│       │   │       ├── navigation/ (Screen)
│       │   │       └── screens/ (Home, Calendar, Tasks, Reports, Profile, Voice, LiveAlarm)
│       │   └── res/ (values, drawables, mipmaps)
│       └── test/ (Unit Tests)
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/gradle-wrapper.properties
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md
```
