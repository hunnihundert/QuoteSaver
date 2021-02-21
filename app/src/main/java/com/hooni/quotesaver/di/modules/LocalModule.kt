package com.hooni.quotesaver.di.modules

import android.content.Context
import androidx.room.Room
import com.hooni.quotesaver.data.local.FavoriteQuotesDao
import com.hooni.quotesaver.data.local.FavoriteQuotesDatabase
import org.koin.dsl.module

val localModule = module {
    single { provideFavoriteQuotesDatabase(get()) }
    single { provideFavoritesQuotesDao(get()) }
}

private fun provideFavoriteQuotesDatabase(applicationContext: Context): FavoriteQuotesDatabase {
    return Room
        .databaseBuilder(
            applicationContext,
            FavoriteQuotesDatabase::class.java,
            "favorite-quotes"
        ).build()
}

private fun provideFavoritesQuotesDao(favoriteQuotesDatabase: FavoriteQuotesDatabase): FavoriteQuotesDao {
    return favoriteQuotesDatabase.getFavoriteQuotesDao()
}
