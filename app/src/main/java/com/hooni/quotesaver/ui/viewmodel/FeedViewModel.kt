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
import kotlinx.coroutines.launch

class FeedViewModel(private val quoteRepository: QuoteRepository) : ViewModel() {

    companion object {
        private const val TAG = "FeedViewModel"
    }

    val favoriteQuotes = quoteRepository.getAllFavorites().asLiveData()
    val searchTerm = MutableLiveData("")
    var isNewRequest = true
    internal var lastRequestedSearch = MutableLiveData("")
    private val nextItems = MutableLiveData<String?>()
    private val previousItems = MutableLiveData<String?>()

    private val apiQueryResponse: LiveData<Resource<ApiQuoteResult>> = lastRequestedSearch.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(null))
            emit(quoteRepository.getQuotesByCategory(lastRequestedSearch.value!!, nextItems.value))
        }
    }

    val apiQueryResponseWithQuotesWithImages: LiveData<Resource<ApiQuoteResult>> = apiQueryResponse.switchMap { apiQueryResponse ->
        if (apiQueryResponse.status == Status.SUCCESS) {
            setNextPreviousItems(apiQueryResponse)
            liveData {
                emit(createQuotesWithImages(apiQueryResponse))
            }
        } else {
            liveData {
                emit(apiQueryResponse)
            }
        }
    }


    internal fun loadRandomQuotes() {
        viewModelScope.launch {
            setRandomCategory()
            getQuotesByCategory()
        }
    }

    private suspend fun setRandomCategory() {
        val randomCategory = getTags().random().name
        searchTerm.value = randomCategory
        lastRequestedSearch.value = searchTerm.value!!
    }

    private suspend fun getTags(): List<ApiTagResult.QuoteTags> {
        val tagApiResponse = quoteRepository.getTags()

        return when (tagApiResponse.status) {
            Status.LOADING -> {
                //how loading indicator
                listOf()
            }
            Status.ERROR -> {
                // inform about error
                listOf()
            }
            Status.SUCCESS -> {
                tagApiResponse.data!!.results!!
            }
        }
    }


    private fun getQuotesByCategory() {
        lastRequestedSearch.value = searchTerm.value!!
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
        if(searchTerm.value.isNullOrBlank()) {
            // empty search
        } else {
            resetRequestParameters()
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

    internal fun resetNewRequest() {
        isNewRequest = false
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
        val a = ApiQuoteResult(apiQueryResponse.data.next,apiQueryResponse.data.previous,quotesWithImages)
        return Resource(apiQueryResponse.status,a,apiQueryResponse.message)
    }

}