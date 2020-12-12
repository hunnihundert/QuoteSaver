package com.hooni.quotesaver.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.hooni.quotesaver.data.model.ApiQuoteResult
import com.hooni.quotesaver.data.model.Quote
import com.hooni.quotesaver.repository.QuoteRepository
import com.hooni.quotesaver.util.getRandomImage
import kotlinx.coroutines.launch

class FeedViewModel(private val quoteRepository: QuoteRepository) : ViewModel() {

    companion object {
        private const val TAG = "FeedViewModel"
    }

    val quotes = MutableLiveData<List<Quote>>()
    val favoriteQuotes = quoteRepository.getAllFavorites().asLiveData()
    val searchTerm = MutableLiveData("")
    var isNewRequest = true

    // in case of the user having typed in a new search term but hasn't tapped the search button
    // Scrolling to the bottom triggers a new request, which is based on lastRequestSearch
    // (and not searchTerm LiveData)
    internal var lastRequestedSearch = ""
    private val nextItems = MutableLiveData<String?>()
    private val previousItems = MutableLiveData<String?>()

    internal fun loadRandomQuotes() {
        viewModelScope.launch {
            setRandomCategory()
            getQuotesByCategory()
        }
    }

    private suspend fun setRandomCategory() {
        val randomCategory = getTags().random()
        searchTerm.value = randomCategory
        lastRequestedSearch = searchTerm.value!!
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
                lastRequestedSearch = searchTerm.value!!
                val offset = getOffset(nextItems.value)
                val apiResponse =
                        if (offset == -1) quoteRepository.getQuotesByCategory(lastRequestedSearch)
                        else quoteRepository.getQuotesByCategory(lastRequestedSearch, offset)
                setQuotesFromApiResponse(apiResponse)
            }
        }
    }

    private fun isSearchTermEmpty(): Boolean {
        return searchTerm.value.isNullOrBlank()
    }

    private fun getOffset(url: String?): Int {
        return if (url.isNullOrBlank()) -1
        else url.substringAfter("offset=").substringBeforeLast("&tags").toInt()
    }

    internal fun addToFavorites(quote: Quote) {
        viewModelScope.launch {
            quoteRepository.addToFavorites(quote)
        }
    }

    internal fun removeFromFavorites(quote: Quote) {
        viewModelScope.launch {
            quoteRepository.removeFromFavorites(quote)
        }
    }

    private fun setQuotesFromApiResponse(apiResponse: ApiQuoteResult) {
        nextItems.value = apiResponse.next
        previousItems.value = apiResponse.next
        val newList = mutableListOf<Quote>()
        apiResponse.results.forEach { quote ->
            newList.add(Quote(quote.quote, quote.author, quote.likes, quote.tags, quote.pk, getRandomImage().toString(), quote.language))
        }
        quotes.value = newList
    }

    internal fun startNewRequest() {
        resetRequestParameters()
        getQuotesByCategory()
    }

    private fun resetRequestParameters() {
        isNewRequest = true
        nextItems.value = ""
        previousItems.value = ""
        lastRequestedSearch = searchTerm.value!!
    }

    internal fun addNewItems() {
        if (nextItems.value != null) getQuotesByCategory()
    }

    internal fun resetNewRequest() {
        isNewRequest = false
    }

}