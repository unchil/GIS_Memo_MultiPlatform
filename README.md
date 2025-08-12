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


###  Video
|Write|Detail|Map|Setting|
|:-:|:-:|:-:|:-:|
|[![Alt text](https://github.com/unchil/GISMemo/blob/main/app/src/main/assets/write_portrait.png)](https://youtube.com/shorts/uM2O647Z7TE?feature=share)|[![Alt text](https://github.com/unchil/GISMemo/blob/main/app/src/main/assets/detail_portrait.png)](https://youtube.com/shorts/5z5QSPmNzvQ?si=7DvLyimANKWn2uQ4)|[![Alt text](https://github.com/unchil/GISMemo/blob/main/app/src/main/assets/map_portrait.png)](https://youtube.com/shorts/-yUi1thvrrw?feature=share)|[![Alt text](https://github.com/unchil/GISMemo/blob/main/app/src/main/assets/setting_portrait.png)](https://youtube.com/shorts/SAq5fDb9DWo?feature=share)|


###  Screen Shot
|     |                                          Portrait                                          |                                                                                                                                                        Landscape                                                                                                                                                        |
|:---:|:------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
|**List**|  ![Alt text](https://github.com/unchil/GISMemo/blob/main/app/src/main/assets/list_portrait.png)  |                                                  ```It is a list screen of created memos, and features such as search, share, delete, and view details are provided.```![Alt text](https://github.com/unchil/GISMemo/blob/main/app/src/main/assets/list_landscape.png)</img>                                                  |
|**Write**| ![Alt text](https://github.com/unchil/GISMemo/blob/main/app/src/main/assets/write_portrait.png)  |  ```This screen is for writing notes. It provides functions such as screenshots of pictures drawn on maps, texts using voice recognition, photos, and videos, and it is possible to set security, markers, and hashtags.```![Alt text](https://github.com/unchil/GISMemo/blob/main/app/src/main/assets/write_landscape.png)   |                                                             
|**Map**|  ![Alt text](https://github.com/unchil/GISMemo/blob/main/app/src/main/assets/map_portrait.png)   | ```The location of the memo with the marker set is displayed.If you touch the information window of the marker, a brief screen of the memo is displayed, and if you touch the screen, you move to the detailed view screen.``` ![Alt text](https://github.com/unchil/GISMemo/blob/main/app/src/main/assets/map_landscape.png) | 
|**Search**| ![Alt text](https://github.com/unchil/GISMemo/blob/main/app/src/main/assets/search_portrait.png) |                                                                ```Memos can be searched by title, creation date, hashtag, security, and marker conditions.```![Alt text](https://github.com/unchil/GISMemo/blob/main/app/src/main/assets/search_landscape.png)                                                                |   
|**Detail**| ![Alt text](https://github.com/unchil/GISMemo/blob/main/app/src/main/assets/detail_portrait.png) |                               ```This is the screen that appears when you tap an item in the list or tap an item in the entire map. Can check the detailed information of the written memo.```![Alt text](https://github.com/unchil/GISMemo/blob/main/app/src/main/assets/detail_landscape.png)                               |   
|**Setting**| ![Alt text](https://github.com/unchil/GISMemo/blob/main/app/src/main/assets/setting_portrait.png) |                               ```This GISMEMO can set the following functions.  Haptic, night mode, dynamic color, delete all notes, language selection (Korean, English, French, Portuguese, Spanish, Chinese)```![Alt text](https://github.com/unchil/GISMemo/blob/main/app/src/main/assets/setting_landscape.png)        




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
