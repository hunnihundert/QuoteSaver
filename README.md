# QuoteSaver
[![Kotlin](https://badgen.net/badge/Kotlin/1.4.21/purple)](https://kotlinlang.org/)
[![Android Gradle Plugin](https://badgen.net/badge/AGP/4.1.1/yellow)](https://developer.android.com/studio/releases/gradle-plugin)
[![Gradle](https://badgen.net/badge/Gradle/6.5/blue)](https://gradle.org)
[![API](https://badgen.net/badge/API/21+/green)](https://android-arsenal.com/api?level=21#l21)

Searching|Adding Favorites|Fullscreen|Sharing
------------ | ------------- | -------------  | -------------  
![Search](Readme%20Images/search.gif)|![Favorites](Readme%20Images/favorites.gif)|![Fullscreen](Readme%20Images/fullscreen.gif)|![Fullscreen](Readme%20Images/share.gif)



An app that get quotes from an API and let's the user save quotes locally.
This app is meant for presentational purposes.

## Tech Stack
- [100% Kotlin](https://kotlinlang.org/)
- [Koin](https://github.com/InsertKoinIO/koin) (dependency injection)
- [Coroutines, Flows](https://developer.android.com/kotlin/coroutines) (asynchronous operations)
- [Retrofit](https://square.github.io/retrofit/), [https://square.github.io/okhttp/](OkHttp) (REST API)
- [Jetpack](https://developer.android.com/jetpack)
  - Lifecycle
  - LiveData
  - Navigation Component
  - Room
  - ViewModel
- [Picasso](https://square.github.io/picasso/) (image loading)

## MVVM Architecture
![MVVM](Readme%20Images/schema.png)
