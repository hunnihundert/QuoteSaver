package com.hooni.quotesaver.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hooni.quotesaver.data.QuoteRepository
import com.hooni.quotesaver.data.model.ApiQuoteResult
import com.hooni.quotesaver.data.model.Quote
import com.hooni.quotesaver.util.NUMBER_OF_QUOTES_RETURNED_AT_ONCE
import kotlinx.coroutines.launch

class FeedViewModel(private val quoteRepository: QuoteRepository) : ViewModel() {

    companion object {
        private const val TAG = "FeedViewModel"
    }

    val quotes = MutableLiveData<List<Quote>>()
    val searchTerm = MutableLiveData("")

    // in case of the user having typed in a new search term but hasn't tapped the search button
    // Scrolling to the bottom triggers a new request, which is based on lastRequestSearch
    // (and not searchTerm LiveData)
    private var lastRequestedSearch = ""
    private val nextItems = MutableLiveData("")
    private val previousItems = MutableLiveData("")

    internal fun loadRandomQuotes() {
        viewModelScope.launch {
            setRandomCategory()
            getQuotesByCategory()
        }
    }

    private suspend fun setRandomCategory() {
        val randomCategory = getTags().random()
        searchTerm.value = randomCategory
        lastRequestedSearch = randomCategory
    }

    private suspend fun getTags(): List<String> {
        val tags = quoteRepository.getTags().results
        return tags.map { it.name }
    }

    private fun getQuotesByCategory() {
        if (isSearchTermEmpty()) {
            // Inform user about search term being empty
        } else {
            viewModelScope.launch {
                nextItems.value?.let {
                    val offset = getOffset(nextItems.value)
                    val apiResponse =
                        if (offset == -1) quoteRepository.getQuotesByCategory(lastRequestedSearch)
                        else quoteRepository.getQuotesByCategory(lastRequestedSearch, offset)
                    setQuotesFromApiResponse(apiResponse)
                }
            }
        }
    }

    private fun isSearchTermEmpty(): Boolean {
        return searchTerm.value.isNullOrBlank()
    }

    private fun getOffset(url: String?): Int {
        return if(url.isNullOrBlank()) -1
        else url.substringAfter("offset=").substringBeforeLast("&tags").toInt()
    }

    private fun setQuotesFromApiResponse(apiResponse: ApiQuoteResult) {
        nextItems.value = apiResponse.next
        previousItems.value = apiResponse.next
        quotes.value = apiResponse.results
    }

    internal fun startNewRequest() {
        resetRequestParameters()
        getQuotesByCategory()
    }

    private fun resetRequestParameters() {
        nextItems.value = ""
        previousItems.value = ""
        lastRequestedSearch = searchTerm.value!!
    }

    internal fun isNewRequest(): Boolean {
        return getOffset(nextItems.value) == NUMBER_OF_QUOTES_RETURNED_AT_ONCE*2
    }

    internal fun addNewItems() {
        if(nextItems.value != null) getQuotesByCategory()
    }

}