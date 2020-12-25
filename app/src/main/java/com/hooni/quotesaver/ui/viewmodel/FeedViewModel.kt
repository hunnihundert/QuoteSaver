package com.hooni.quotesaver.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.hooni.quotesaver.data.model.ApiQuoteResult
import com.hooni.quotesaver.data.model.ApiTagResult
import com.hooni.quotesaver.data.model.Quote
import com.hooni.quotesaver.data.remote.Resource
import com.hooni.quotesaver.data.remote.Status.*
import com.hooni.quotesaver.repository.QuoteRepository
import com.hooni.quotesaver.util.getRandomImage
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class FeedViewModel(private val quoteRepository: QuoteRepository) : ViewModel() {


    sealed class Progress {
        object Idle : Progress()
        object Loading : Progress()
        class Error(val message: String) : Progress()
    }

    internal val progress = MutableLiveData<Progress>(Progress.Idle)

    internal val quoteResultWithImages = MutableLiveData<ApiQuoteResult>()
    internal val favoriteQuotes = quoteRepository.getAllFavorites()
        .onStart {
            progress.value = Progress.Loading }
        .onEach {
            progress.value = Progress.Idle }
        .asLiveData()

    val currentSearchTerm = MutableLiveData("")
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
                    progress.value = Progress.Loading
                }
                ERROR -> {
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
        currentSearchTerm.value = randomCategory
        lastRequestedSearch = currentSearchTerm.value!!
        getQuotesByCategory()
    }

    private fun getQuotesByCategory() {
        viewModelScope.launch {
            progress.value = Progress.Loading
            val apiResult =
                quoteRepository.getQuotesByCategory(lastRequestedSearch, nextItems.value)
            when (apiResult.status) {
                LOADING -> {
                    progress.value = Progress.Loading
                }
                ERROR -> {
                    progress.value = Progress.Error(apiResult.message ?: "Unknown Error")
                }
                SUCCESS -> {
                    progress.value = Progress.Idle
                    if(isNewRequest) quoteResultWithImages.value = addImageToQuotes(apiResult)
                    else quoteResultWithImages.value = addNewApiResultToCurrent(apiResult)

                }
            }
        }
    }

    private fun addImageToQuotes(apiResult: Resource<ApiQuoteResult>): ApiQuoteResult {
        setNextPreviousItems(apiResult)
        return createQuotesWithImages(apiResult)
    }

    private fun setNextPreviousItems(apiQueryResponse: Resource<ApiQuoteResult>) {
        nextItems.value = apiQueryResponse.data!!.next
        previousItems.value = apiQueryResponse.data.previous
    }

    private fun createQuotesWithImages(apiQueryResponse: Resource<ApiQuoteResult>): ApiQuoteResult {
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
        return ApiQuoteResult(
            apiQueryResponse.data.next,
            apiQueryResponse.data.previous,
            quotesWithImages)
    }

    private fun addNewApiResultToCurrent(apiResult: Resource<ApiQuoteResult>): ApiQuoteResult {
        return ApiQuoteResult(
            quoteResultWithImages.value!!.next,
            quoteResultWithImages.value!!.previous,
            quoteResultWithImages.value!!.results
                    + addImageToQuotes(apiResult).results)
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
        if (currentSearchTerm.value.isNullOrBlank()) {
            // empty search
        } else {
            resetRequestParameters()
            lastRequestedSearch = currentSearchTerm.value!!
            getQuotesByCategory()
        }
    }

    private fun resetRequestParameters() {
        isNewRequest = true
        nextItems.value = ""
        previousItems.value = ""
    }

    internal fun addNewItems() {
        if (nextItems.value != null) getQuotesByCategory()
    }

    internal fun resetIsNewRequest() {
        isNewRequest = false
    }

    internal fun getIsNewRequest() = isNewRequest

    internal fun setFullscreenQuote(quote: Quote){
        fullScreenQuote = quote
    }

    internal fun getFullscreenQuote(): Quote {
        return fullScreenQuote
    }

}