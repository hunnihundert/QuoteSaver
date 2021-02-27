package com.hooni.quotesaver.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hooni.quotesaver.data.model.Quote

@Database(entities = [Quote::class], version = 1)
@TypeConverters(Converter::class)
abstract class FavoriteQuotesDatabase : RoomDatabase() {
    abstract fun getFavoriteQuotesDao(): FavoriteQuotesDao
}