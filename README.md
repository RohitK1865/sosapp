# 🚨 SOS App – Voice-Activated Emergency Alert System

An Android application that listens for a customizable **SOS phrase** (e.g., “Help me”) using **voice recognition** and immediately places a call to a predefined emergency contact. Built to empower **rural girls and communities** with a quick and reliable safety tool.

---

## 🛡️ Features

- 🎤 **Voice Recognition:** Detects a custom SOS phrase (e.g., “Help me”).
- ⚙️ **Firebase Firestore Integration:** Store and manage:
  - SOS phrase (customizable)
  - Emergency contact number
- 📞 **Emergency Call Trigger:** Automatically places a call when SOS phrase is recognized.
- 🔐 **Firebase Authentication:** Secure login with persistent sessions.
- 📱 **Material Design UI:** Simple, intuitive interface.
- 🔘 **Voice Listener Toggle:** Enable or disable listening from the navigation menu.
- 🔄 **Auto Login:** Persistent login state using Firebase Auth.

---


## 🚀 Getting Started

### 📦 Prerequisites

- Android Studio (Electric Eel or later)
- Java 8+
- Firebase Project
  - Firebase Authentication
  - Firebase Firestore
- Internet Connection
- Android Device (API Level 23+)

---

### 🔧 Firebase Setup

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project.
3. Enable **Authentication > Email/Password**.
4. Create a Firestore database (in **test mode** for dev).
5. Add an **Android app** to the project:
   - Package name: `com.example.sosapp`
6. Download the `google-services.json` and place it in:

---
### 🔐 Security

-All user data is securely stored using Firebase Authentication & Firestore.

-App runs voice detection locally—no voice is sent to the cloud.

---

### 👩‍💻 Tech Stack

-Android (Java)

-Firebase Authentication

-Firebase Firestore

-Material Design

-SpeechRecognizer API

---

### 🙋‍♀️ Purpose

-Designed to empower rural girls and women, ensuring safety with just a voice command, even when they can't physically reach their phone.

---

### 🧑‍💻 Contributing

-Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

---

### ✨ Developed by

-Rohit Kumbhar
📧 rohitak1865@gmail.com
