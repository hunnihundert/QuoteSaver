package com.hooni.quotesaver.repository

import android.util.Log
import com.hooni.quotesaver.data.local.FavoriteQuotesDao
import com.hooni.quotesaver.data.model.ApiQuoteResult
import com.hooni.quotesaver.data.model.ApiTagResult
import com.hooni.quotesaver.data.model.Quote
import com.hooni.quotesaver.data.remote.QuotesApi
import kotlinx.coroutines.flow.Flow

class QuoteRepository(private val quotesApi: QuotesApi, private val favoriteQuotesDao: FavoriteQuotesDao) {

    companion object {
        private const val TAG = "QuoteRepository"
        const val NUMBER_OF_QUOTES_RETURNED_AT_ONCE = 20
        const val SEARCH_OFF_SET_PARAMETER = 20
    }

    suspend fun getTags(): ApiTagResult {
        return quotesApi.getTags()
    }

    suspend fun getQuotesByCategory(category: String): ApiQuoteResult {
        return quotesApi.getQuotesByCategory(
            category,
            NUMBER_OF_QUOTES_RETURNED_AT_ONCE,
            SEARCH_OFF_SET_PARAMETER
        )
    }

    fun getAllFavorites(): Flow<List<Quote>> {
        Log.d(TAG, "getAllFavorites: getting Favorites")
        return favoriteQuotesDao.getAllFavoriteQuotes()
    }

    suspend fun addToFavorites(quote: Quote) {
        Log.d(TAG, "addToFavorites: $quote")
        favoriteQuotesDao.addFavoriteQuote(quote)
    }

    suspend fun removeFromFavorites(quote: Quote) {
        Log.d(TAG, "removeFromFavorites: $quote")
        favoriteQuotesDao.removeFavoriteQuote(quote)
    }

}