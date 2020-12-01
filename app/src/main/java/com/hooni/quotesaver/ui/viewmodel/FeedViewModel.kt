package com.hooni.quotesaver.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hooni.quotesaver.data.QuoteRepository
import com.hooni.quotesaver.data.model.ApiResultPojo
import com.hooni.quotesaver.data.model.Quote
import kotlinx.coroutines.launch

class FeedViewModel(private val quoteRepository: QuoteRepository): ViewModel() {

    companion object {
        private const val TAG = "FeedViewModel"
    }

    val quotes = MutableLiveData<List<Quote>>()
    val searchTerm = MutableLiveData("")

    fun loadRandomQuotes() {
        quoteRepository.getRandomQuotes()
    }

    fun getQuote() {
        viewModelScope.launch {
            val a = quoteRepository.getQuotes()
            provideQuotesFromApiResponse(a)
        }

    }

    private fun provideQuotesFromApiResponse(apiResponse: ApiResultPojo) {
        val next = apiResponse.next
        val results = apiResponse.results

        quotes.value = results
        Log.d(TAG, "provideQuotesFromApiResponse: ${quotes.value}")
    }

    fun searchForCategoryQuote() {
        if(isSearchTermEmpty()) {
            // search term is empty
        } else {
            quoteRepository.getQuotesFromCategory(searchTerm.value!!)
        }
    }

    private fun isSearchTermEmpty(): Boolean {
        return searchTerm.value.isNullOrBlank()
    }

}