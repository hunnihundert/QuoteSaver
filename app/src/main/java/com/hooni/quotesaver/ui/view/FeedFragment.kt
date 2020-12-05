package com.hooni.quotesaver.ui.view

import android.os.Bundle
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

class FeedFragment : Fragment() {

    companion object {
        private const val TAG = "FeedFragment"
    }

    private lateinit var binding: FragmentFeedBinding
    private val feedViewModel: FeedViewModel by viewModel()

    private lateinit var searchTextInputLayout: TextInputLayout
    private lateinit var searchButton: ImageButton
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
    private val likeStatusChanger: (Quote) -> Unit = { quote ->
        //quote.liked = !quote.liked
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
        initObserver()
        setRandomQuoteList()
    }

    private fun initUi() {
        searchTextInputLayout = binding.textInputLayoutFeedSearch
        searchButton = binding.buttonFeedSearch
        searchButton.setOnClickListener {
            feedViewModel.startNewRequest()
        }
        initRecyclerView()
    }

    private fun initRecyclerView() {
        feedAdapter = QuoteFeedAdapter(displayedQuotes, likeStatusChanger)
        feedRecyclerView = binding.recyclerViewFeedQuoteFeed
        feedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        feedRecyclerView.adapter = feedAdapter
        feedRecyclerView.addOnScrollListener(endOfListDetector)
    }

    private fun loadNewItems() {
        feedViewModel.addNewItems()
    }

    private fun initObserver() {
        feedViewModel.quotes.observe(viewLifecycleOwner) { quoteList ->
            if(feedViewModel.isNewRequest()) resetRecyclerView()
            updateRecyclerView(quoteList)
            moveEditTextCursorToEnd()
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

    private fun setRandomQuoteList() {
        feedViewModel.loadRandomQuotes()
    }

    private fun moveEditTextCursorToEnd() {
        searchTextInputLayout.editText!!.setSelection(searchTextInputLayout.editText!!.length())
    }
}