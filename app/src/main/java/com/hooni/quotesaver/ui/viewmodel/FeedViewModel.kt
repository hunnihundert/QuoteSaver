package com.hooni.quotesaver.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hooni.quotesaver.data.QuoteRepository
import com.hooni.quotesaver.data.model.ApiQuoteResult
import com.hooni.quotesaver.data.model.Quote
import kotlinx.coroutines.launch

class FeedViewModel(private val quoteRepository: QuoteRepository) : ViewModel() {

    companion object {
        private const val TAG = "FeedViewModel"
    }

    val quotes = MutableLiveData<List<Quote>>()
    val searchTerm = MutableLiveData("")

    fun loadRandomQuotes() {
        viewModelScope.launch {
            setRandomCategory()
            getQuotesByCategory()
        }
    }

    private suspend fun setRandomCategory() {
        val randomCategory = getTags().random()
        searchTerm.value = randomCategory
    }

    fun getQuotesByCategory() {
        if (isSearchTermEmpty()) {
            // Inform user about search term being empty
        } else {
            viewModelScope.launch {
                val apiResponse = quoteRepository.getQuotesByCategory(searchTerm.value!!)
                provideQuotesFromApiResponse(apiResponse)
            }
        }
    }

    private suspend fun getTags(): List<String> {
        val tags = quoteRepository.getTags().results
        return tags.map {it.name}
    }

    private fun provideQuotesFromApiResponse(apiResponse: ApiQuoteResult) {
        val next = apiResponse.next
        val results = apiResponse.results
        quotes.value = results
    }

    private fun isSearchTermEmpty(): Boolean {
        return searchTerm.value.isNullOrBlank()
    }

}