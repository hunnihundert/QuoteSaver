package com.hooni.quotesaver.data

import com.hooni.quotesaver.data.model.ApiQuoteResult
import com.hooni.quotesaver.data.model.ApiTagResult
import com.hooni.quotesaver.data.remote.QuotesApi
import com.hooni.quotesaver.util.NUMBER_OF_QUOTES_RETURNED_AT_ONCE

class QuoteRepository(private val quotesApi: QuotesApi) {

    suspend fun getTags(): ApiTagResult {
        return quotesApi.getTags()
    }

    suspend fun getQuotesByCategory(category: String, offsetParameter: Int = NUMBER_OF_QUOTES_RETURNED_AT_ONCE): ApiQuoteResult {
        return quotesApi.getQuotesByCategory(
            category,
            NUMBER_OF_QUOTES_RETURNED_AT_ONCE,
            offsetParameter
        )
    }
}