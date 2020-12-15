package com.hooni.quotesaver.ui.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.hooni.quotesaver.data.model.ApiQuoteResult
import com.hooni.quotesaver.data.model.ApiTagResult
import com.hooni.quotesaver.data.model.Quote
import com.hooni.quotesaver.data.remote.Resource
import com.hooni.quotesaver.data.remote.Status
import com.hooni.quotesaver.repository.QuoteRepository
import com.hooni.quotesaver.util.getRandomImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class FeedViewModel(private val quoteRepository: QuoteRepository) : ViewModel() {

    companion object {
        private const val TAG = "FeedViewModel"
    }

    val quotes = MutableLiveData<List<Quote>>()
    val favoriteQuotes = quoteRepository.getAllFavorites().asLiveData()
    val searchTerm = MutableLiveData("")
    var isNewRequest = true

    private val justQuotes: LiveData<Resource<ApiQuoteResult>> = searchTerm.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(null))
            emit(quoteRepository.getQuotesByCategory(lastRequestedSearch, nextItems.value))
        }
    }


    val changedQuotes = justQuotes.switchMap { justQuotes ->
        nextItems.value = justQuotes.data?.next
        previousItems.value = justQuotes.data?.previous
        val newList = mutableListOf<Quote>()
        justQuotes.data?.results?.forEach { quote ->
            newList.add(Quote(quote.quote, quote.author, quote.likes, quote.tags, quote.pk, getRandomImage().toString(), quote.language))
        }
        liveData<List<Quote>> {
            emit(newList)
        }
    }

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
        val randomCategory = getTags().random().name
        searchTerm.value = randomCategory
        lastRequestedSearch = searchTerm.value!!
    }

    private suspend fun getTags(): List<ApiTagResult.QuoteTags> {
        val tagApiResponse = quoteRepository.getTags()

        when(tagApiResponse.status) {
            Status.LOADING -> {}
            Status.ERROR -> {}
            Status.SUCCESS -> {}
        }

        return tagApiResponse.data!!.results!!
    }


    private fun getQuotesByCategory() {
        if (isSearchTermEmpty()) {
            // Inform user about search term being empty
        } else {
            viewModelScope.launch {
                lastRequestedSearch = searchTerm.value!!
                val apiResponse = quoteRepository.getQuotesByCategory(lastRequestedSearch, nextItems.value)
                //setQuotesFromApiResponse(apiResponse)
            }
        }
    }

    private fun isSearchTermEmpty(): Boolean {
        return searchTerm.value.isNullOrBlank()
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
        Log.d(
            TAG,
            "setQuotesFromApiResponse: next: ${apiResponse.next}, previous: ${apiResponse.previous}, result: ${apiResponse.results}")
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