package com.hooni.quotesaver.repository

import com.hooni.quotesaver.data.local.FavoriteQuotesDao
import com.hooni.quotesaver.data.model.ApiQuoteResult
import com.hooni.quotesaver.data.model.ApiTagResult
import com.hooni.quotesaver.data.model.Quote
import com.hooni.quotesaver.data.remote.QuotesApi
import com.hooni.quotesaver.data.remote.Resource
import com.hooni.quotesaver.data.remote.ResponseHandler
import com.hooni.quotesaver.util.NUMBER_OF_QUOTES_RETURNED_AT_ONCE
import kotlinx.coroutines.flow.Flow

class QuoteRepository(
    private val quotesApi: QuotesApi,
    private val favoriteQuotesDao: FavoriteQuotesDao,
    private val responseHandler: ResponseHandler
) {

    suspend fun getTags(): Resource<ApiTagResult> {
        return try {
            responseHandler.handleSuccess(quotesApi.getTags())
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun getQuotesByCategory(category: String, nextUrl: String?): Resource<ApiQuoteResult> {
        val offset = getOffSet(nextUrl)
        return try {
            responseHandler.handleSuccess(
                quotesApi.getQuotesByCategory(
                    category,
                    NUMBER_OF_QUOTES_RETURNED_AT_ONCE,
                    offset
                )
            )
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    private fun getOffSet(url: String?): Int {
        return if (url.isNullOrBlank()) 0
        else url.substringAfter("offset=").substringBeforeLast("&tags").toInt()
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