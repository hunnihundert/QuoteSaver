package com.hooni.quotesaver.ui.view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.hooni.quotesaver.R
import com.hooni.quotesaver.data.model.Quote
import com.hooni.quotesaver.data.remote.Status
import com.hooni.quotesaver.databinding.FragmentFeedBinding
import com.hooni.quotesaver.ui.adapter.QuoteFeedAdapter
import com.hooni.quotesaver.ui.viewmodel.FeedViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FeedFragment : Fragment() {

    companion object {
        private const val TAG = "FeedFragment"
    }

    private lateinit var binding: FragmentFeedBinding
    private val feedViewModel: FeedViewModel by sharedViewModel()

    private lateinit var searchTextInputLayout: EditText
    private lateinit var favoritesImageView: ImageView
    private lateinit var loadingView: LinearLayout
    private lateinit var noResultsTextView: TextView
    private lateinit var feedRecyclerView: RecyclerView
    private lateinit var feedAdapter: QuoteFeedAdapter
    private val endOfListDetector: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.d(TAG, "onScrollStateChanged: end of list detected!")
                if (!feedRecyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE && feedViewModel.progress.value != FeedViewModel.Progress.Loading) loadNewItems()
            }
        }

    private val displayedQuotes = mutableListOf<Quote>()
    private val favoriteQuotes = mutableListOf<Quote>()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        binding.feedViewModel = feedViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initUi()
        initObserver()
        loadRandomQuotes()
    }


    private fun initUi() {
        initSearchTextInput()
        initImageView()
        initLoadingView()
        initErrorTextView()
        initRecyclerView()
    }

    private fun initSearchTextInput() {
        searchTextInputLayout = binding.editTextFeedSearch
        searchTextInputLayout.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH, EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_GO, EditorInfo.IME_ACTION_SEND -> {
                    feedViewModel.startNewRequest()
                    hideKeyboard()
                    true
                }
                else -> {
                    true
                }
            }
        }
    }

    private fun initImageView() {
        favoritesImageView = binding.imageViewFeedFavorites
        favoritesImageView.setOnClickListener {
            findNavController().navigate(FeedFragmentDirections.actionFeedFragmentToFavoritesFragment())
        }
    }

    private fun initLoadingView() {
        loadingView = binding.linearLayoutFeedLoading
        loadingView.visibility = View.GONE
    }

    private fun initErrorTextView() {
        noResultsTextView = binding.textViewFeedNoResults
        noResultsTextView.visibility = View.VISIBLE
    }

    private fun initRecyclerView() {
        val favoriteStatusChanger: (Quote) -> Unit = { quote ->
            Log.d(TAG, "favoriteClickListener: contains: ${favoriteQuotes.contains(quote)}")
            if (favoriteQuotes.contains(quote)) feedViewModel.removeFromFavorites(quote)
            else feedViewModel.addToFavorites(quote)
        }
        val fullscreenOpener: (Quote) -> Unit = { quote ->
            feedViewModel.setQuote(quote)
            Log.d(TAG, "fullscreenOpener: $quote")
            findNavController().navigate(FeedFragmentDirections.actionFeedFragmentToFullscreenFragment())
        }
        feedAdapter = QuoteFeedAdapter(displayedQuotes, favoriteQuotes, favoriteStatusChanger, fullscreenOpener)
        feedRecyclerView = binding.recyclerViewFeedQuoteFeed
        feedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        feedRecyclerView.adapter = feedAdapter
        feedRecyclerView.addOnScrollListener(endOfListDetector)
    }


    private fun initObserver() {
        feedViewModel.favoriteQuotes.observe(viewLifecycleOwner) { favoriteQuoteList ->
            updateFavoriteQuotes(favoriteQuoteList)
        }
        feedViewModel.apiQueryResponseWithQuotesWithImages.observe(viewLifecycleOwner) { apiResultsQuotes ->
            when(apiResultsQuotes.status) {
                Status.SUCCESS -> {
                    if(feedViewModel.getIsNewRequest()) {
                        resetRecyclerView()
                        feedViewModel.resetNewRequest()
                    }
                    updateRecyclerView(apiResultsQuotes.data!!.results)
                    moveEditTextCursorToEnd()
                    switchNoResultsTextVisibility(apiResultsQuotes.data.results.isEmpty())
                }
                Status.ERROR -> {
                    Log.d(TAG, "quotes, error: ${apiResultsQuotes.status}, ${apiResultsQuotes.message}")
                    noResultsTextView.text = getString(R.string.textView_feed_error, apiResultsQuotes.message)
                    noResultsTextView.visibility = View.VISIBLE
                }
                Status.LOADING -> {
                    Log.d(TAG, "quotes, loading: $apiResultsQuotes.status, ${apiResultsQuotes.message}")
                }
            }
        }
        feedViewModel.progress.observe(viewLifecycleOwner) { progress ->
            Log.d(TAG, "initObserver: observe progress")
            when(progress) {
                is FeedViewModel.Progress.Loading -> {
                    Log.d(TAG, "progress: loading")
                    loadingView.visibility = View.VISIBLE
                }
                is FeedViewModel.Progress.Error -> {
                    Log.d(TAG, "progress: error")
                    loadingView.visibility = View.GONE
                    progress.message
                    showError(progress.message)
                }
                is FeedViewModel.Progress.Idle -> {
                    Log.d(TAG, "progress: idle")
                    loadingView.visibility = View.GONE
                }
            }
        }
    }

    private fun showError(message: String?) {
        val errorMessage = getString(R.string.textView_feed_error, message ?: "Unknown Error")
        noResultsTextView.text = errorMessage
        noResultsTextView.visibility = View.VISIBLE
        val snackBar = Snackbar.make(binding.root,errorMessage,Snackbar.LENGTH_SHORT)
        snackBar.show()
    }

    private fun resetRecyclerView() {
        feedRecyclerView.scrollToPosition(0)
        displayedQuotes.clear()
    }

    private fun updateRecyclerView(quoteList: List<Quote>) {
        displayedQuotes.addAll(quoteList)
        feedAdapter.notifyDataSetChanged()
    }

    private fun moveEditTextCursorToEnd() {
        searchTextInputLayout.setSelection(searchTextInputLayout.length())
    }

    private fun switchNoResultsTextVisibility(listIsEmpty: Boolean) {
        if(listIsEmpty) {
            noResultsTextView.visibility = View.VISIBLE
            noResultsTextView.text = getString(R.string.textView_feed_noResults)
        }
        else noResultsTextView.visibility = View.GONE
    }

    private fun updateFavoriteQuotes(favoriteQuoteList: List<Quote>) {
        favoriteQuotes.clear()
        favoriteQuotes.addAll(favoriteQuoteList)
        feedAdapter.notifyDataSetChanged()
    }


    private fun loadRandomQuotes() {
        if(feedViewModel.lastRequestedSearch.isEmpty()) feedViewModel.loadRandomQuotes()
    }


    private fun loadNewItems() {
        Log.d(TAG, "loadNewItems: loading new items!")
        feedViewModel.addNewItems()
    }


    private fun Fragment.hideKeyboard() {
        view?.let {activity?.hideKeyboard(it)}
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}