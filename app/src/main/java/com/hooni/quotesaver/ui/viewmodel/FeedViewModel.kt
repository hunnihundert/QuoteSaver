package com.hooni.quotesaver.ui.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.hooni.quotesaver.data.model.ApiQuoteResult
import com.hooni.quotesaver.data.model.ApiTagResult
import com.hooni.quotesaver.data.model.Quote
import com.hooni.quotesaver.data.remote.Resource
import com.hooni.quotesaver.data.remote.Status.*
import com.hooni.quotesaver.repository.QuoteRepository
import com.hooni.quotesaver.util.getRandomImage
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class FeedViewModel(private val quoteRepository: QuoteRepository) : ViewModel() {

    companion object {
        private const val TAG = "FeedViewModel"
    }

    sealed class Progress {
        object Idle : Progress()
        object Loading : Progress()
        class Error(val message: String) : Progress()
    }

    internal val progress = MutableLiveData<Progress>(Progress.Idle)
    internal val favoriteQuotes = quoteRepository.getAllFavorites()
        .onStart {
            Log.d(TAG, "quote repo get all fav loading: ")
            progress.value = Progress.Loading }
        .onCompletion {
            Log.d(TAG, "quote repo get all fav done: ")
            progress.value = Progress.Idle }
        .asLiveData()
    internal val apiQueryResponseWithQuotesWithImages = MutableLiveData<Resource<ApiQuoteResult>>()
    val searchTerm = MutableLiveData("")
    internal var lastRequestedSearch = ""
    private var isNewRequest = true
    private val nextItems = MutableLiveData<String?>()
    private val previousItems = MutableLiveData<String?>()
    private lateinit var fullScreenQuote: Quote


    internal fun loadRandomQuotes() {
        viewModelScope.launch {
            val tags = getTags()
            when (tags.status) {
                LOADING -> {
                    Log.d(TAG, "loadRandomQuotes: get tags loading")
                    progress.value = Progress.Loading
                }
                ERROR -> {
                    progress.value = Progress.Idle
                    progress.value = Progress.Error(tags.message ?: "Unknown Error")
                }
                SUCCESS -> {
                    progress.value = Progress.Idle
                    setRandomCategory(tags.data!!.results!!)
                }
            }
        }
    }

    private suspend fun getTags(): Resource<ApiTagResult> {
        return quoteRepository.getTags()
    }

    private fun setRandomCategory(categories: List<ApiTagResult.QuoteTags>) {
        val randomCategory = categories.random().name
        searchTerm.value = randomCategory
        lastRequestedSearch = searchTerm.value!!
        getQuotesByCategory()
    }

    private fun getQuotesByCategory() {
        viewModelScope.launch {
            progress.value = Progress.Loading
            val apiResult =
                quoteRepository.getQuotesByCategory(lastRequestedSearch, nextItems.value)
            when (apiResult.status) {
                LOADING -> {
                    Log.d(TAG, "getQuotesByCategory: api result status loading")
                    progress.value = Progress.Loading
                }
                ERROR -> {
                    progress.value = Progress.Idle
                    progress.value = Progress.Error(apiResult.message ?: "Unknown Error")
                }
                SUCCESS -> {
                    progress.value = Progress.Idle
                    apiQueryResponseWithQuotesWithImages.value = addImageToQuotes(apiResult)
                }
            }
        }
    }

    private fun addImageToQuotes(apiResult: Resource<ApiQuoteResult>): Resource<ApiQuoteResult> {
        setNextPreviousItems(apiResult)
        return createQuotesWithImages(apiResult)
    }

    private fun setNextPreviousItems(apiQueryResponse: Resource<ApiQuoteResult>) {
        nextItems.value = apiQueryResponse.data!!.next
        previousItems.value = apiQueryResponse.data.previous
    }

    private fun createQuotesWithImages(apiQueryResponse: Resource<ApiQuoteResult>): Resource<ApiQuoteResult> {
        val quotesWithImages = mutableListOf<Quote>()
        apiQueryResponse.data!!.results.forEach { quote ->
            quotesWithImages.add(
                Quote(
                    quote.quote,
                    quote.author,
                    quote.likes,
                    quote.tags,
                    quote.pk,
                    getRandomImage().toString(),
                    quote.language
                )
            )
        }
        val apiQuoteResultsWithImages = ApiQuoteResult(
            apiQueryResponse.data.next,
            apiQueryResponse.data.previous,
            quotesWithImages
        )
        return Resource(
            apiQueryResponse.status,
            apiQuoteResultsWithImages,
            apiQueryResponse.message
        )
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

    internal fun startNewRequest() {
        if (searchTerm.value.isNullOrBlank()) {
            // empty search
        } else {
            resetRequestParameters()
            lastRequestedSearch = searchTerm.value!!
            getQuotesByCategory()
        }
    }

    private fun resetRequestParameters() {
        isNewRequest = true
        nextItems.value = ""
        previousItems.value = ""
    }

    internal fun addNewItems() {
        Log.d(TAG, "addNewItems: starting")
        if (nextItems.value != null) getQuotesByCategory()
    }

    internal fun resetNewRequest() {
        isNewRequest = false
    }

    internal fun getIsNewRequest() = isNewRequest

    internal fun setQuote(quote: Quote){
        Log.d(TAG, "setQuote: $quote")
        fullScreenQuote = quote
    }

    internal fun getQuote(): Quote {
        Log.d(TAG, "getQuote: $fullScreenQuote")
        return fullScreenQuote
    }

}