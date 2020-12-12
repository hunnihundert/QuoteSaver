package com.hooni.quotesaver.repository

import com.hooni.quotesaver.data.local.FavoriteQuotesDao
import com.hooni.quotesaver.data.model.ApiQuoteResult
import com.hooni.quotesaver.data.model.ApiTagResult
import com.hooni.quotesaver.data.model.Quote
import com.hooni.quotesaver.data.remote.QuotesApi
import com.hooni.quotesaver.util.NUMBER_OF_QUOTES_RETURNED_AT_ONCE
import kotlinx.coroutines.flow.Flow

class QuoteRepository(private val quotesApi: QuotesApi, private val favoriteQuotesDao: FavoriteQuotesDao) {

    suspend fun getTags(): ApiTagResult {
        return quotesApi.getTags()
    }

    suspend fun getQuotesByCategory(category: String, offsetParameter: Int = 0): ApiQuoteResult {
        return quotesApi.getQuotesByCategory(
            category,
            NUMBER_OF_QUOTES_RETURNED_AT_ONCE,
            offsetParameter
        )
    }

    fun getAllFavorites(): Flow<List<Quote>> {
        return favoriteQuotesDao.getAllFavoriteQuotes()
    }

    suspend fun addToFavorites(quote: Quote) {
        favoriteQuotesDao.addFavoriteQuote(quote)
    }

    suspend fun removeFromFavorites(quote: Quote) {
        favoriteQuotesDao.removeFavoriteQuote(quote)
    }

}