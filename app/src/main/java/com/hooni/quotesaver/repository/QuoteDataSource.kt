package com.hooni.quotesaver.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hooni.quotesaver.data.model.Quote
import com.hooni.quotesaver.data.remote.QuotesApi
import com.hooni.quotesaver.util.NUMBER_OF_QUOTES_RETURNED_AT_ONCE
import retrofit2.HttpException
import java.io.IOException

private const val INITIAL_OFFSET = 0

class QuoteDataSource(private val quoteApi: QuotesApi, private val category: String) :
    PagingSource<Int, Quote>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Quote> {
        val offset = params.key ?: INITIAL_OFFSET

        return try {
            val apiResult =
                quoteApi.getQuotesByCategory(category, NUMBER_OF_QUOTES_RETURNED_AT_ONCE, offset)
            val nextKey = getKey(apiResult.next)
            val prevKey = getKey(apiResult.previous)
            LoadResult.Page(apiResult.results, prevKey, nextKey)
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    private fun getKey(url: String?): Int? {
        var key: Int? = null
        url?.let {
            key = it.substringAfterLast("offset=").substringBeforeLast("&tags").toIntOrNull()
        }
        return key
    }

    override fun getRefreshKey(state: PagingState<Int, Quote>): Int? {
        return state.anchorPosition
    }
}