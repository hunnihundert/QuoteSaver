package com.hooni.quotesaver.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.hooni.quotesaver.data.local.FavoriteQuotesDao
import com.hooni.quotesaver.data.model.ApiTagResult
import com.hooni.quotesaver.data.model.Quote
import com.hooni.quotesaver.data.remote.QuotesApi
import com.hooni.quotesaver.util.NUMBER_OF_QUOTES_RETURNED_AT_ONCE
import kotlinx.coroutines.flow.Flow

class QuoteRepository(
    private val quotesApi: QuotesApi,
    private val favoriteQuotesDao: FavoriteQuotesDao,
) {

//    suspend fun getTags(): Resource<ApiTagResult> {
//        return try {
//            responseHandler.handleSuccess(quotesApi.getTags())
//        } catch (e: Exception) {
//            responseHandler.handleException(e)
//        }
//    }

    suspend fun getTags(): ApiTagResult {
        return quotesApi.getTags()
    }

    fun getQuotesByCategory(category: String): Flow<PagingData<Quote>> {
        return Pager(
            PagingConfig(
                pageSize = NUMBER_OF_QUOTES_RETURNED_AT_ONCE,
            enablePlaceholders = false),
            pagingSourceFactory = {QuoteDataSource(quotesApi,category)}
        ).flow
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