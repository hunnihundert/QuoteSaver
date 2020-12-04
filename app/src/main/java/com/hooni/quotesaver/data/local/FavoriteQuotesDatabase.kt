package com.hooni.quotesaver.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hooni.quotesaver.data.model.Quote

@Database(entities = [Quote::class], version = 1)
abstract class FavoriteQuotesDatabase: RoomDatabase() {
    abstract fun getFavoriteQuotesDao(): FavoriteQuotesDao
}