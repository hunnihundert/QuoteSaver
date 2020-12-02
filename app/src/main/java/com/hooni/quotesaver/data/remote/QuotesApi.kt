package com.hooni.quotesaver.data.remote

import com.hooni.quotesaver.data.model.ApiQuoteResult
import com.hooni.quotesaver.data.model.ApiTagResult
import retrofit2.http.GET
import retrofit2.http.Query

interface QuotesApi {

    @GET("tags/?limit=100&offset=100")
    suspend fun getTags(): ApiTagResult

    @GET("quotes/")
    suspend fun getQuotesByCategory(
        @Query("tags") category: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): ApiQuoteResult
}