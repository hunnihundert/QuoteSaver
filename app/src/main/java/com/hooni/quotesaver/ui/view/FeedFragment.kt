package com.hooni.quotesaver.ui.view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hooni.quotesaver.data.model.Quote
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
    private lateinit var feedRecyclerView: RecyclerView
    private lateinit var feedAdapter: QuoteFeedAdapter
    private val endOfListDetector: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!feedRecyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) loadNewItems()
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
        initDisplayedQuotes()
    }


    private fun initUi() {
        initSearchTextInput()
        initImageView()
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

    private fun initRecyclerView() {
        val favoriteStatusChanger: (Quote) -> Unit = { quote ->
            if (favoriteQuotes.contains(quote)) feedViewModel.removeFromFavorites(quote)
            else feedViewModel.addToFavorites(quote)
        }
        feedAdapter = QuoteFeedAdapter(displayedQuotes, favoriteQuotes, favoriteStatusChanger)
        feedRecyclerView = binding.recyclerViewFeedQuoteFeed
        feedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        feedRecyclerView.adapter = feedAdapter
        feedRecyclerView.addOnScrollListener(endOfListDetector)
    }


    private fun initObserver() {
        feedViewModel.quotes.observe(viewLifecycleOwner) { quoteList ->
            if(feedViewModel.isNewRequest) {
                resetRecyclerView()
                feedViewModel.resetNewRequest()
            }
            updateRecyclerView(quoteList)
            moveEditTextCursorToEnd()
        }
        feedViewModel.favoriteQuotes.observe(viewLifecycleOwner) { favoriteQuoteList ->
            updateFavoriteQuotes(favoriteQuoteList)
        }
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

    private fun updateFavoriteQuotes(favoriteQuoteList: List<Quote>) {
        favoriteQuotes.clear()
        favoriteQuotes.addAll(favoriteQuoteList)
        feedAdapter.notifyDataSetChanged()
    }


    private fun initDisplayedQuotes() {
        if(feedViewModel.lastRequestedSearch.isEmpty()) setRandomQuoteList()
    }

    private fun setRandomQuoteList() {
        feedViewModel.loadRandomQuotes()
    }


    private fun loadNewItems() {
        feedViewModel.addNewItems()
    }


    private fun Fragment.hideKeyboard() {
        view?.let {activity?.hideKeyboard(it)}
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}