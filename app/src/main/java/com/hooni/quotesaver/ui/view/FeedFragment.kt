package com.hooni.quotesaver.ui.view

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.hooni.quotesaver.R
import com.hooni.quotesaver.data.model.Quote
import com.hooni.quotesaver.databinding.FragmentFeedBinding
import com.hooni.quotesaver.ui.adapter.QuoteFeedAdapter
import com.hooni.quotesaver.ui.viewmodel.FeedViewModel
import com.hooni.quotesaver.util.DOUBLE_BACK_TAP_EXIT_INTERVAL
import com.hooni.quotesaver.util.KEY_RECYCLERVIEW_STATE
import com.hooni.quotesaver.util.TextInputEditTextWithClickableDrawable
import com.hooni.quotesaver.util.hideKeyboard
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FeedFragment : Fragment() {

    private lateinit var binding: FragmentFeedBinding
    private val feedViewModel: FeedViewModel by sharedViewModel()
    private var backPressedTime: Long = System.currentTimeMillis()
    private lateinit var backSnackBar: Snackbar

    private lateinit var searchTextInputLayout: TextInputEditTextWithClickableDrawable
    private lateinit var loadingView: LinearLayout
    private lateinit var noResultsTextView: TextView
    private lateinit var feedRecyclerView: RecyclerView
    private lateinit var feedAdapter: QuoteFeedAdapter

    private val displayedQuotes = mutableListOf<Quote>()
    private val favoriteQuotes = mutableListOf<Quote>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    exitOnDoubleBackTap()
                }
            })
    }

    private fun exitOnDoubleBackTap() {
        if (backPressedTime + DOUBLE_BACK_TAP_EXIT_INTERVAL >= System.currentTimeMillis()) {
            backSnackBar.dismiss()
            requireActivity().finishAffinity()
        } else {
            backPressedTime = System.currentTimeMillis()
            backSnackBar = Snackbar.make(
                binding.root,
                "Press again to exit",
                Snackbar.LENGTH_SHORT
            )
            backSnackBar.show()
        }
    }

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

    override fun onPause() {
        super.onPause()
        saveRecyclerViewState()
    }

    private fun saveRecyclerViewState() {
        feedViewModel.feedRecyclerViewState = feedRecyclerView.layoutManager?.onSaveInstanceState()
        feedViewModel.feedRecyclerViewStateBundle = Bundle()
        feedViewModel.feedRecyclerViewStateBundle!!.putParcelable(
            KEY_RECYCLERVIEW_STATE,
            feedViewModel.feedRecyclerViewState
        )
    }

    override fun onResume() {
        super.onResume()
        loadRecyclerViewState()
    }

    private fun loadRecyclerViewState() {
        feedViewModel.feedRecyclerViewStateBundle?.let {
            feedViewModel.feedRecyclerViewState = it.getParcelable(KEY_RECYCLERVIEW_STATE)
            feedRecyclerView.layoutManager?.onRestoreInstanceState(feedViewModel.feedRecyclerViewState)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initUi()
        initObserver()
        loadRandomQuotes()
    }


    private fun initUi() {
        initSearchLayout()
        initLoadingView()
        initErrorTextView()
        initRecyclerView()
    }

    private fun initSearchLayout() {
        searchTextInputLayout = binding.editTextFeedSearch
        searchTextInputLayout.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH, EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_GO, EditorInfo.IME_ACTION_SEND -> {
                    feedViewModel.startNewRequest()
                    hideKeyboard(requireContext(), binding.root)
                    true
                }
                else -> {
                    true
                }
            }
        }
        searchTextInputLayout.setNavigationPoint {
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
            if (favoriteQuotes.contains(quote)) feedViewModel.removeFromFavorites(quote)
            else feedViewModel.addToFavorites(quote)
        }
        val fullscreenOpener: (Quote) -> Unit = { quote ->
            feedViewModel.setFullscreenQuote(quote)
            findNavController().navigate(FeedFragmentDirections.actionFeedFragmentToFullscreenFragment())
        }
        val endOfListDetector: RecyclerView.OnScrollListener =
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!feedRecyclerView.canScrollVertically(1) &&
                        newState == RecyclerView.SCROLL_STATE_IDLE &&
                        feedViewModel.progress.value != FeedViewModel.Progress.Loading
                    ) loadNewItems()
                }
            }

        feedAdapter = QuoteFeedAdapter(
            displayedQuotes,
            favoriteQuotes,
            favoriteStatusChanger,
            fullscreenOpener
        )
        feedRecyclerView = binding.recyclerViewFeedQuoteFeed
        feedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        feedRecyclerView.adapter = feedAdapter
        feedRecyclerView.addOnScrollListener(endOfListDetector)
    }


    private fun initObserver() {
        feedViewModel.favoriteQuotes.observe(viewLifecycleOwner) { favoriteQuoteList ->
            updateFavoriteQuotes(favoriteQuoteList)
        }

        feedViewModel.quoteResultWithImages.observe(viewLifecycleOwner) { quoteResultWithImages ->
            loadingView.visibility = View.GONE
            if (feedViewModel.getIsNewRequest()) {
                feedRecyclerView.scrollToPosition(0)
                feedViewModel.resetIsNewRequest()
            }
            updateRecyclerView(quoteResultWithImages.results)
            moveEditTextCursorToEnd()
            switchNoResultsTextVisibility(quoteResultWithImages.results.isEmpty())
        }

        feedViewModel.progress.observe(viewLifecycleOwner) { progress ->
            when (progress) {
                is FeedViewModel.Progress.Loading -> {
                    loadingView.visibility = View.VISIBLE
                }
                is FeedViewModel.Progress.Error -> {
                    loadingView.visibility = View.GONE
                    progress.message
                    showError(progress.message)
                }
                is FeedViewModel.Progress.Idle -> {
                    loadingView.visibility = View.GONE
                }
            }
        }
    }

    private fun showError(message: String?) {
        val errorMessage = getString(R.string.textView_feed_error, message ?: "Unknown Error")
        noResultsTextView.text = errorMessage
        noResultsTextView.visibility = View.VISIBLE
        val snackBar = Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT)
        snackBar.show()
    }

    private fun updateRecyclerView(quoteList: List<Quote>) {
        displayedQuotes.clear()
        displayedQuotes.addAll(quoteList)
        feedAdapter.notifyDataSetChanged()
    }

    private fun moveEditTextCursorToEnd() {
        searchTextInputLayout.setSelection(searchTextInputLayout.length())
    }

    private fun switchNoResultsTextVisibility(listIsEmpty: Boolean) {
        if (listIsEmpty) {
            noResultsTextView.visibility = View.VISIBLE
            noResultsTextView.text = getString(R.string.textView_feed_noResults)
        } else noResultsTextView.visibility = View.GONE
    }

    private fun updateFavoriteQuotes(favoriteQuoteList: List<Quote>) {
        favoriteQuotes.clear()
        favoriteQuotes.addAll(favoriteQuoteList)
        feedAdapter.notifyDataSetChanged()
    }


    private fun loadRandomQuotes() {
        if (feedViewModel.lastRequestedSearch.isEmpty()) feedViewModel.loadRandomQuotes()
    }


    private fun loadNewItems() {
        feedViewModel.addNewItems()
    }
}