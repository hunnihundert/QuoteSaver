package com.hooni.quotesaver.ui.viewmodel

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.hooni.quotesaver.data.model.Quote
import com.hooni.quotesaver.repository.QuoteRepository
import com.hooni.quotesaver.util.firstTimeSearches
import com.hooni.quotesaver.util.getRandomImage
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class FeedViewModel(private val quoteRepository: QuoteRepository) : ViewModel() {

    internal val favoriteQuotes = quoteRepository.getAllFavorites().asLiveData()

    var currentSearchTerm: String? = null
    var currentSearchResult: Flow<PagingData<Quote>>? = null
    val currentSearchResultLiveData: LiveData<PagingData<Quote>>
        get() = _currentSearchResultLiveData
    private var _currentSearchResultLiveData = MutableLiveData<PagingData<Quote>>()

    private lateinit var fullScreenQuote: Quote
    private var searchJob: Job? = null


    private fun getQuotesByCategory(query: String) {
        val lastResult = currentSearchResult
        if (query == currentSearchTerm && lastResult != null) {
            return
        }
        currentSearchTerm = query
        val newResult = quoteRepository.getQuotesByCategory(query).map { pagingData ->
            pagingData.map {
                createQuotesWithImages(it)
            }
        }.cachedIn(viewModelScope)
        currentSearchResult = newResult
        viewModelScope.launch {
            newResult.collectLatest {
                _currentSearchResultLiveData.value = it
            }
        }
    }

    private fun createQuotesWithImages(quote: Quote): Quote {
        return Quote(
            quote.quote,
            quote.author,
            quote.likes,
            quote.tags,
            quote.pk,
            getRandomImage().toString(),
            quote.language
        )
    }


    fun search(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            Log.d(TAG, "search: $query")
            getQuotesByCategory(query)
        }
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

    internal fun setFullscreenQuote(quote: Quote) {
        fullScreenQuote = quote
    }

    internal fun getFullscreenQuote(): Quote {
        return fullScreenQuote
    }

    internal fun setRandomCategoryAsSearchTerm() {
        currentSearchTerm = firstTimeSearches.random()
    }

    companion object {
        private const val TAG = "FeedViewModel"
    }
}