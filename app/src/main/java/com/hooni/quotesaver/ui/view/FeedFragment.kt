package com.hooni.quotesaver.ui.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.hooni.quotesaver.data.model.Quote
import com.hooni.quotesaver.databinding.FragmentFeedBinding
import com.hooni.quotesaver.ui.adapter.QuoteFeedAdapter
import com.hooni.quotesaver.ui.viewmodel.FeedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FeedFragment: Fragment() {

    companion object {
        private const val TAG = "FeedFragment"
    }

    private lateinit var binding: FragmentFeedBinding
    private val feedViewModel: FeedViewModel by viewModel()

    private lateinit var searchTextInputLayout: TextInputLayout
    private lateinit var searchButton: ImageButton
    private lateinit var feedRecyclerView: RecyclerView
    private lateinit var feedAdapter: QuoteFeedAdapter

    private val displayedQuotes = mutableListOf<Quote>()
    private val favoriteQuotes = mutableListOf<Quote>()
    private val favoriteStatusChanger: (Quote) -> Unit = { quote ->
        Log.d(TAG, "likeClickListener: favoriteQuotes: $favoriteQuotes")
        Log.d(TAG, "likeClickListener: contains? ${favoriteQuotes.contains(quote)}")
        if(favoriteQuotes.contains(quote)) feedViewModel.removeFromFavorites(quote)
        else feedViewModel.addToFavorites(quote)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedBinding.inflate(inflater,container,false)
        binding.feedViewModel = feedViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initUi()
        initObserver()
        setRandomQuoteList()
    }

    private fun initUi() {
        searchTextInputLayout = binding.textInputLayoutFeedSearch
        searchButton = binding.buttonFeedSearch
        searchButton.setOnClickListener {
            feedViewModel.getQuotesByCategory()
        }
        initRecyclerView()
    }

    private fun initRecyclerView() {
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

    private fun updateFavoriteQuotes(favoriteQuoteList: List<Quote>) {
        favoriteQuotes.clear()
        favoriteQuotes.addAll(favoriteQuoteList)
        feedAdapter.notifyDataSetChanged()
        Log.d(TAG, "updateFavoriteQuotes: $favoriteQuoteList")
    }

    private fun moveEditTextCursorToEnd() {
        searchTextInputLayout.editText!!.setSelection(searchTextInputLayout.editText!!.length())
    }

    private fun setRandomQuoteList() {
        feedViewModel.loadRandomQuotes()
    }


}