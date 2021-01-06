package com.hooni.quotesaver.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.hooni.quotesaver.data.model.ApiTagResult
import com.hooni.quotesaver.data.model.Quote
import com.hooni.quotesaver.repository.QuoteRepository
import com.hooni.quotesaver.util.getRandomImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.SocketTimeoutException

class FeedViewModel(private val quoteRepository: QuoteRepository) : ViewModel() {

    sealed class Progress {
        object Idle : Progress()
        object Loading : Progress()
        class Error(val message: String) : Progress()
    }

    internal val progress = MutableLiveData<Progress>(Progress.Idle)

    internal val favoriteQuotes = quoteRepository.getAllFavorites()
        .onStart {
            progress.value = Progress.Loading
        }
        .onEach {
            progress.value = Progress.Idle
        }
        .asLiveData()

    var currentSearchTerm: String? = null
    private var currentSearchResult: Flow<PagingData<Quote>>? = null

    private lateinit var fullScreenQuote: Quote

    private suspend fun getRandomCategory(): String {
        progress.value = Progress.Loading
        var tags: ApiTagResult? = null
        withContext(Dispatchers.IO) {
            try {
                tags = quoteRepository.getTags()
            } catch (exception: Exception) {
                when (exception) {
                    is HttpException -> {
                        progress.postValue(Progress.Error(exception.message()))
                    }
                    is SocketTimeoutException -> {
                        progress.postValue(Progress.Error(exception.message ?: "Socket Time Out"))
                    }
                    else -> {
                        progress.postValue(Progress.Error(exception.message ?: "Unknown Error"))
                    }
                }
            }
        }
        return tags?.results?.random()?.name ?: ""
    }

    fun getQuotesByCategory(query: String): Flow<PagingData<Quote>> {
        Log.d(TAG, "getQuotesByCategory: currentSearchResult: $currentSearchResult / currentSearch: $currentSearchTerm / query: $query")
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

    internal suspend fun setRandomCategoryAsSearchTerm() {
        currentSearchTerm = getRandomCategory()
    }

    companion object {
        private const val TAG = "FeedViewModel"
    }

}