package com.hooni.quotesaver.ui.view

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.hooni.quotesaver.R
import com.hooni.quotesaver.data.model.Quote
import com.hooni.quotesaver.databinding.FragmentFeedBinding
import com.hooni.quotesaver.ui.adapter.LoadStateAdapter
import com.hooni.quotesaver.ui.adapter.QuoteFeedAdapter
import com.hooni.quotesaver.ui.viewmodel.FeedViewModel
import com.hooni.quotesaver.util.DOUBLE_BACK_TAP_EXIT_INTERVAL
import com.hooni.quotesaver.util.hideKeyboard
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FeedFragment : Fragment() {

    private lateinit var binding: FragmentFeedBinding
    private val feedViewModel: FeedViewModel by sharedViewModel()
    private var backPressedTime: Long = System.currentTimeMillis()
    private lateinit var backSnackBar: Snackbar

    private lateinit var searchTextInputLayout: TextInputEditText
    private lateinit var loadingView: LinearLayout
    private lateinit var noResultsTextView: TextView
    private lateinit var reload: Button
    private lateinit var feedRecyclerView: RecyclerView
    private lateinit var feedAdapter: QuoteFeedAdapter

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

    override fun onStop() {
        val pref = activity?.getPreferences(Context.MODE_PRIVATE)
        pref?.edit()?.putString(SAVED_SEARCH, feedViewModel.currentSearchTerm)?.apply()
        super.onStop()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initUi()
        initSearch()
        initObserver()
    }

    private fun initUi() {
        initSearchLayout()
        initLoadingAndErrorView()
        initRecyclerView()
    }

    private fun initSearchLayout() {
        searchTextInputLayout = binding.editTextFeedSearch
        searchTextInputLayout.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH, EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_GO, EditorInfo.IME_ACTION_SEND -> {
                    updateQuotesFromInput()
                    hideKeyboard(requireContext(), binding.root)
                    true
                }
                else -> {
                    true
                }
            }
        }
    }

    private fun updateFavoriteQuotes(favoriteQuoteList: List<Quote>) {
        favoriteQuotes.clear()
        favoriteQuotes.addAll(favoriteQuoteList)
        feedAdapter.notifyDataSetChanged()
    }

    private fun initLoadingAndErrorView() {
        loadingView = binding.linearLayoutFeedLoading
        noResultsTextView = binding.textViewFeedNoResults
        reload = binding.buttonFeedReload
        loadingView.visibility = View.GONE
        noResultsTextView.visibility = View.VISIBLE
        reload.visibility = View.GONE

        reload.setOnClickListener {
            feedAdapter.retry()
        }
    }

    private fun initRecyclerView() {
        feedRecyclerView = binding.recyclerViewFeedQuoteFeed

        val favoriteStatusChanger: (Quote) -> Unit = { quote ->
            if (favoriteQuotes.contains(quote)) feedViewModel.removeFromFavorites(quote)
            else feedViewModel.addToFavorites(quote)
        }
        val fullscreenOpener: (Quote) -> Unit = { quote ->
            feedViewModel.setFullscreenQuote(quote)
            findNavController().navigate(FeedFragmentDirections.actionFeedFragmentToFullscreenFragment())
        }

        feedAdapter = QuoteFeedAdapter(
            favoriteQuotes,
            favoriteStatusChanger,
            fullscreenOpener
        )
        feedAdapter.addLoadStateListener { loadState ->
            feedRecyclerView.isVisible = loadState.refresh is LoadState.NotLoading
            noResultsTextView.isVisible = loadState.refresh is LoadState.NotLoading
            loadingView.isVisible = loadState.refresh is LoadState.Loading
            reload.isVisible = loadState.refresh is LoadState.Error

            if (feedAdapter.itemCount < 1) {
                noResultsTextView.isVisible = true
                noResultsTextView.text = getString(R.string.textView_feed_noResults)
            } else noResultsTextView.isVisible = false

            val errorState = loadState.refresh as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error

            errorState?.let {
                val errorMessage = getString(R.string.textView_feed_error, it.error)
                if (loadState.refresh is LoadState.Error) {
                    noResultsTextView.text = errorMessage
                    noResultsTextView.visibility = View.VISIBLE
                    reload.visibility = View.VISIBLE
                }
                val snackBar = Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT)
                snackBar.show()
            }

        }

        feedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        feedRecyclerView.adapter =
            feedAdapter.withLoadStateFooter(LoadStateAdapter { feedAdapter.retry() })
    }

    private fun initSearch() {
        lifecycleScope.launch {
            feedAdapter.loadStateFlow
                .distinctUntilChangedBy { it.refresh }
                .filter { it.refresh is LoadState.NotLoading }
                .collectLatest { feedRecyclerView.scrollToPosition(0) }
        }
        getLastSearch()
    }

    private fun getLastSearch() {
        lifecycleScope.launch {
            if (feedViewModel.currentSearchTerm == null) {
                val prefSearch =
                    activity?.getPreferences(Context.MODE_PRIVATE)?.getString(SAVED_SEARCH, "")
                feedViewModel.currentSearchTerm = prefSearch ?: ""
                if (feedViewModel.currentSearchTerm == "") {
                    feedViewModel.setRandomCategoryAsSearchTerm()
                }
            }
            feedViewModel.search(feedViewModel.currentSearchTerm!!)
            searchTextInputLayout.setText(feedViewModel.currentSearchTerm!!)
        }
    }

    private fun initObserver() {
        feedViewModel.favoriteQuotes.observe(viewLifecycleOwner) { favoriteQuoteList ->
            updateFavoriteQuotes(favoriteQuoteList)
        }
        feedViewModel.currentSearchResultLiveData.observe(viewLifecycleOwner) { searchResults ->
            lifecycleScope.launch {
                feedAdapter.submitData(searchResults)
            }
        }
    }

    private fun updateQuotesFromInput() {
        searchTextInputLayout.text?.trim()?.let {
            if (it.isNotEmpty()) feedViewModel.search(it.toString())
        }
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

    companion object {
        private const val SAVED_SEARCH = "saved search"
        private const val TAG = "FeedFragment"
    }

}