package com.hooni.quotesaver.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.hooni.quotesaver.data.model.Quote
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteQuotesDao {

    @Query("SELECT * from quote")
    fun getAllFavoriteQuotes(): List<Quote>

    @Insert
    suspend fun addFavoriteQuote(quote: Quote)

    @Delete
    suspend fun removeFavoriteQuote(quote: Quote)

}