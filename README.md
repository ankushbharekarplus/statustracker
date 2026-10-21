# Usage Tracker (Android)

A simple Android app that shows how much time you've spent in each app today,
plus a total screen-time figure at the top.

## How it works
- Uses Android's built-in `UsageStatsManager` API to read per-app foreground time.
- Requires the special **"Usage Access"** permission (not a normal runtime permission —
  granted from a system Settings screen, since it exposes sensitive data).
- On first launch, if the permission isn't granted yet, the app shows a button that
  opens the correct Settings screen for you.
- Once granted, it lists every app used today, sorted by time spent, with icon and name.

## Project structure
```
UsageTracker/
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/usagetracker/
│       │   ├── MainActivity.kt       # permission check + loads/display usage stats
│       │   ├── UsageAdapter.kt       # RecyclerView adapter for the app list
│       │   └── AppUsageInfo.kt       # simple data model
│       └── res/
│           ├── layout/               # activity_main.xml, item_app_usage.xml
│           ├── values/               # strings, colors, theme
│           └── drawable, mipmap/     # app icon
├── build.gradle
└── settings.gradle
```

## How to build & run
### Option A: Android Studio
1. Open the `UsageTracker` folder in **Android Studio** (Hedgehog or newer recommended).
2. Let Gradle sync — it will pull in AndroidX/Material dependencies automatically.
3. Run the app on a device or emulator (minSdk 26 / Android 8.0+).

### Option B: No installs at all — build via GitHub Actions
This repo includes `.github/workflows/build.yml`, which builds an installable
debug APK in the cloud automatically:
1. Create a new GitHub repo and push this `UsageTracker` folder to it.
2. Go to the repo's **Actions** tab — the "Build Debug APK" workflow runs
   automatically on push (or trigger it manually via "Run workflow").
3. When it finishes, open the workflow run and download the **UsageTracker-debug-apk**
   artifact (a zip containing `app-debug.apk`).
4. Transfer `app-debug.apk` to your Android phone (email, Drive, USB, etc.),
   tap it to install, and allow "install from unknown sources" if prompted.

### Option C: Command-line only, on your own machine
If you don't want the full Android Studio IDE but do want a local build:
1. Install just the **Android SDK Command-line Tools** (much lighter than
   Studio) and a JDK 17.
2. From the project root, run `gradle assembleDebug` (or generate a wrapper
   first with `gradle wrapper` if you'd rather use `./gradlew`).
3. The APK lands in `app/build/outputs/apk/debug/app-debug.apk` — copy it to
   your phone and install it the same way as in Option B.

## Granting permission (any install method)
Once installed on your phone, tap **"Grant Usage Access"** — this opens Settings → Usage Access.
Find "Usage Tracker" in the list and toggle it on, then go back to the app. The home
screen will then populate with today's per-app usage, sorted highest first.

## Notes & easy extensions
- Currently shows **today's** usage only (resets at midnight). You could add a
  date picker or week/month view using `UsageStatsManager.INTERVAL_WEEKLY` / `INTERVAL_MONTHLY`.
- You could add a pull-to-refresh, or auto-refresh on a timer.
- You could set daily limits per app and show a warning when exceeded — a common
  "digital wellbeing" style feature.
- For more precise foreground-time tracking (e.g. multiple short sessions per app),
  swap the daily aggregate query for `UsageEvents` and sum `MOVE_TO_FOREGROUND`/
  `MOVE_TO_BACKGROUND` event pairs yourself.
