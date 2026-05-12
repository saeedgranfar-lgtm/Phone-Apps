# Positive News

Android news app, written in Kotlin + Jetpack Compose, that aggregates BBC and
CNN RSS feeds and filters them down to uplifting headlines using an on-device
lexicon scorer. Optimized for the Samsung Galaxy S25 Ultra (1–120 Hz LTPO panel,
edge-to-edge, Material You).

## Highlights

- **Sources**: BBC News + sub-feeds (Health, Science) and CNN + sub-feeds
  (Health, Entertainment). All over RSS — no API key.
- **Positivity filter**: on-device keyword scorer in
  `app/src/main/java/com/positivenews/app/data/PositivityFilter.kt`. Tunable
  thresholds, runs offline, leaks no reading data.
- **S25 Ultra tuning**:
  - `preferredRefreshRate = 120f` window attribute.
  - `REQUESTED_FRAME_RATE_CATEGORY_HIGH` on Android 14+.
  - Edge-to-edge layout with `windowLayoutInDisplayCutoutMode = shortEdges` for
    the punch-hole camera.
  - Material You dynamic color (Android 12+) with AMOLED-friendly dark theme.
- **Image loading**: Coil 3 with 64 MB disk cache + 25 % memory cache.
- **External links**: open in Chrome Custom Tabs.

## Build locally

Prereqs: JDK 17+, Android Studio Ladybug or newer, Android SDK Platform 35.

```
./gradlew assembleDebug      # debug APK
./gradlew assembleRelease    # release APK (unsigned)
```

The signed debug APK lands at `app/build/outputs/apk/debug/app-debug.apk`.

## CI build

`.github/workflows/build-apk.yml` builds the debug APK on every push to this
branch and uploads it as a workflow artifact named **`PositiveNews-debug-apk`**.
That's the easiest way to get an installable APK — open the latest run on the
Actions tab, scroll to *Artifacts*, download the zip, transfer to the device,
install.

## Tuning the positive filter

Edit the lexicon in `PositivityFilter.kt`. The score is
`positive_hits - 2 * negative_hits`; the inclusion threshold is `THRESHOLD = 1`.
Lower the threshold to see more borderline stories.
