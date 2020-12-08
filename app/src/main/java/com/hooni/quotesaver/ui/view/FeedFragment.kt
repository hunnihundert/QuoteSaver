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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hooni.quotesaver.data.model.Quote
import com.hooni.quotesaver.databinding.FragmentFeedBinding
import com.hooni.quotesaver.ui.adapter.QuoteFeedAdapter
import com.hooni.quotesaver.ui.viewmodel.FeedViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

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

    private val displayedQuotes = mutableListOf<Quote>()
    private val favoriteQuotes = mutableListOf<Quote>()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
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
        searchTextInputLayout.setOnEditorActionListener() { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH, EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_GO, EditorInfo.IME_ACTION_SEND -> {
                    feedViewModel.getQuotesByCategory()
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
            findNavController().navigate(FeedFragmentDirections.actionFeedFragmentToLikedQuotesFragment())
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
    }

    private fun initObserver() {
        feedViewModel.quotes.observe(viewLifecycleOwner) { quoteList ->
            updateDisplayedQuotes(quoteList)
            moveEditTextCursorToEnd()
        }
        feedViewModel.favoriteQuotes.observe(viewLifecycleOwner) { favoriteQuoteList ->
            updateFavoriteQuotes(favoriteQuoteList)
        }
    }

    private fun updateDisplayedQuotes(quoteList: List<Quote>) {
        displayedQuotes.clear()
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
        Log.d(TAG, "initDisplayedQuotes: current search is empty: ${feedViewModel.currentSearchTerm.isEmpty()}")
        if(feedViewModel.currentSearchTerm.isEmpty()) setRandomQuoteList()
    }

    private fun setRandomQuoteList() {
        feedViewModel.loadRandomQuotes()
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