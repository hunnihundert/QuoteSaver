package com.hooni.quotesaver.data.remote

import com.hooni.quotesaver.data.model.ApiResultPojo
import com.hooni.quotesaver.data.model.Quote
import retrofit2.http.GET

interface QuotesApi {

    @GET("?tags=motivation")
    suspend fun getQuotes(): ApiResultPojo
}