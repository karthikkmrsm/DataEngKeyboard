# DataEng Keyboard 🔡
**Custom Android Keyboard with SQL, PySpark & Databricks keywords**

Works in ANY Android app — ChatGPT, Claude, WhatsApp, Gmail, Notebooks, etc.

---

## Features
- ⚡ **450+ keywords** across SQL, PySpark, and Databricks
- 🔍 **Live autocomplete** — type letters to filter keywords instantly
- ⌨️ Full **QWERTY + Numbers + Symbols** keyboard
- 🎨 Color-coded keys: Teal=SQL, Purple=PySpark, Orange=Databricks
- 📱 Works in **every Android app** (it's a real system keyboard)
- One-tap keyword insertion with smart spacing

---

## How to Build (Android Studio)

### Prerequisites
- Android Studio (latest stable) — download from https://developer.android.com/studio
- Android SDK 34
- Java 17

### Steps
1. **Open project**: File → Open → select the `DataEngKeyboard` folder
2. **Wait for Gradle sync** to complete
3. **Build APK**: Build → Build Bundle(s)/APK(s) → Build APK(s)
4. APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

### Install on your phone
```bash
# Via USB (enable USB debugging first)
adb install app/build/outputs/apk/debug/app-debug.apk

# Or copy the APK to your phone and open it (enable "Install from unknown sources")
```

---

## How to Activate After Install

1. **Open the DataEng Keyboard app** → tap "Open Input Settings"
2. **Enable** "DataEng Keyboard" in the list
3. **Set as default**: tap "Change Default Keyboard" in the app, or
   - tap the keyboard icon in any text field
   - choose "DataEng Keyboard"

---

## Project Structure
```
DataEngKeyboard/
├── app/src/main/
│   ├── AndroidManifest.xml          # App & IME registration
│   ├── java/com/dataeng/keyboard/
│   │   ├── DataEngIME.java          # ← Main keyboard service (IME)
│   │   ├── KeywordData.java         # ← All 450+ keywords
│   │   ├── KwAdapter.java           # ← RecyclerView adapter
│   │   └── SetupActivity.java       # ← Onboarding screen
│   └── res/
│       ├── layout/
│       │   ├── keyboard_view.xml    # ← Keyboard UI layout
│       │   ├── item_kw_chip.xml     # ← Keyword grid item
│       │   └── activity_setup.xml  # ← Setup screen UI
│       ├── values/
│       │   ├── strings.xml
│       │   ├── colors.xml
│       │   ├── themes.xml
│       │   └── dimens.xml
│       ├── xml/
│       │   └── method.xml           # IME subtype declaration
│       └── drawable/                # Key backgrounds
├── build.gradle
└── settings.gradle
```

---

## Keyword Coverage

| Category | Sub-categories | Count |
|----------|---------------|-------|
| SQL | Clauses, Functions, Window, DDL, Predicates, CTE/Adv | ~140 |
| PySpark | Session, DataFrame, Transform, Functions, I/O, Window, Imports | ~160 |
| Databricks | Delta, Unity Catalog, dbutils, Compute, MLflow | ~110 |
| **Total** | | **~450** |

---

## Customizing
- Add keywords: edit `KeywordData.java` — add entries to any map
- Add new category: add a new `put()` call to SQL/SPARK/DB maps
- Change colors: edit `res/values/colors.xml`

---

## License
MIT — free to use, modify, and distribute.
