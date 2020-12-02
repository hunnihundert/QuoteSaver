package com.hooni.quotesaver.data

import com.hooni.quotesaver.data.model.ApiQuoteResult
import com.hooni.quotesaver.data.model.ApiTagResult
import com.hooni.quotesaver.data.model.Quote
import com.hooni.quotesaver.data.remote.QuotesApi

class QuoteRepository(private val quotesApi: QuotesApi) {

    companion object {
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
}