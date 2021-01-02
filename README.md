# QuoteSaver
[![Kotlin](https://badgen.net/badge/Kotlin/1.4.21/blue)](http://https://kotlinlang.org/)
[![Android Gradle Plugin](https://badgen.net/badge/AGP/4.1.1/blue)](https://developer.android.com/studio/releases/gradle-plugin)
[![Gradle](https://badgen.net/badge/Gradle/6.5/blue)](https://gradle.org)


An app that get quotes from an API and let's the user save quotes locally.
This app is meant for presentational purposes.

## Architecture
MVVM with repository architecture

### View:
FeedFragment
FavoritesFragment
FullscreenFragment

### ViewModel:
FeedViewModel

### Repository:
QuoteRepository

### Model:
FavoriteQuotesDatabase (local)
QuotesApi (remote)
