package com.hooni.quotesaver.data

import com.hooni.quotesaver.data.model.ApiResultPojo
import com.hooni.quotesaver.data.model.Quote
import com.hooni.quotesaver.data.remote.QuotesApi

class QuoteRepository(private val quotesApi: QuotesApi) {

    fun getRandomQuotes(): List<Quote> {
        // return list of random Quotes
        return listOf()
    }

    fun getQuotesFromCategory(category: String): List<Quote> {
        // return list of quotes that match category
        return listOf()
    }

    suspend fun getQuotes(): ApiResultPojo {
        return quotesApi.getQuotes()
    }
}