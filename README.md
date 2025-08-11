# GISMemo Multiplatform

A Kotlin Multiplatform sample application that allows you to save memos with GIS (Geographic Information System) information, photos, videos, and weather data.

| | |
| --- | --- |
| **Platform** | Android, iOS |
| **UI** | Jetpack Compose (Android), SwiftUI (iOS) |
| **Architecture** | Kotlin Multiplatform Mobile (KMM) |
| **Database** | SQLDelight |
| **Networking** | Ktor |
| **Image Loading**| Coil 3 |


## Features

*   **Geo-tagged Memos**: Create memos and associate them with a specific geographic location.
*   **Interactive Map**: View all your memos as markers on a Google Map.
*   **Rich Media Attachments**: Enhance your memos by attaching photos and videos captured directly within the app.
*   **Speech-to-Text**: Conveniently dictate your memos using the integrated speech recognition feature.
*   **Automatic Weather Data**: The app automatically fetches and attaches the current weather information to your memo based on its location.
*   **Paginated Memo List**: Efficiently browse through your memos with a paginated list.
*   **Multi-language Support**: The UI is available in multiple languages.

## Technology Stack

This project leverages a modern technology stack for building multiplatform applications.

### Shared Module (`/shared`)

*   **[Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines)**: For managing asynchronous operations.
*   **[Ktor](https://ktor.io/)**: For networking, used here to fetch weather data.
*   **[SQLDelight](https://github.com/cashapp/sqldelight)**: For a type-safe, multiplatform local database.
*   **[Paging 3 (Multiplatform)](https://github.com/cashapp/multiplatform-paging)**: For efficiently loading large data sets.
*   **[Coil 3](https://coil-kt.github.io/coil/getting_started/)**: For multiplatform image loading.
*   **[Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)**: For JSON serialization/deserialization.

### Android (`/androidApp`)

*   **[Jetpack Compose](https://developer.android.com/jetpack/compose)**: For building the UI.
*   **[Google Maps Compose SDK](https://developers.google.com/maps/documentation/android-sdk/maps-compose)**: For displaying maps.
*   **[CameraX](https://developer.android.com/training/camerax)**: For camera functionalities.
*   **[Media3 ExoPlayer](https://developer.android.com/guide/topics/media3)**: For video playback.
*   **[Accompanist Permissions](https://google.github.io/accompanist/permissions/)**: To handle runtime permissions.
*   **[Jetpack Navigation](https://developer.android.com/guide/navigation)**: For navigating between screens.

### iOS (`/iosApp`)

*   **SwiftUI**: For the user interface.
*   Utilizes the shared module for business logic, data storage, and networking.

## How to Build

To build and run this project, you will need to provide your own API keys for Google Maps and a weather service (like OpenWeatherMap).

1.  Create a `local.properties` file in the root directory of the project.
2.  Add your API keys to this file, like so:

    ```properties
    # Google Maps API Key for Android
    MAPS_API_KEY="YOUR_GOOGLE_MAPS_API_KEY"

    # OpenWeatherMap API Key
    OPENWEATHER_API_KEY="YOUR_OPENWEATHER_API_KEY"
    ```

3.  Build the project using Android Studio or from the command line.
