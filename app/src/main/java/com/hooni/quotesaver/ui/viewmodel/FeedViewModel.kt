package com.hooni.quotesaver.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.hooni.quotesaver.data.model.ApiTagResult
import com.hooni.quotesaver.data.model.Quote
import com.hooni.quotesaver.repository.QuoteRepository
import com.hooni.quotesaver.util.firstTimeSearches
import com.hooni.quotesaver.util.getRandomImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedViewModel(private val quoteRepository: QuoteRepository) : ViewModel() {

    internal val favoriteQuotes = quoteRepository.getAllFavorites().asLiveData()

    var currentSearchTerm: String? = null
    private var currentSearchResult: Flow<PagingData<Quote>>? = null

    private lateinit var fullScreenQuote: Quote

    fun getQuotesByCategory(query: String): Flow<PagingData<Quote>> {
        val lastResult = currentSearchResult
        if (query == currentSearchTerm && lastResult != null) {
            return lastResult
        }
        currentSearchTerm = query
        val newResult = quoteRepository.getQuotesByCategory(query).map { pagingData ->
            pagingData.map {
                createQuotesWithImages(it)
            }
        }.cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
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
}