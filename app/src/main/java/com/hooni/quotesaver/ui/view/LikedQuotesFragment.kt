package com.hooni.quotesaver.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hooni.quotesaver.data.model.Quote
import com.hooni.quotesaver.databinding.FragmentLikedQuotesBinding
import com.hooni.quotesaver.ui.adapter.QuoteFeedAdapter
import com.hooni.quotesaver.ui.viewmodel.FeedViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LikedQuotesFragment: Fragment() {

    private val feedViewModel: FeedViewModel by sharedViewModel()
    private lateinit var binding: FragmentLikedQuotesBinding

    private lateinit var back: ImageView
    private lateinit var noFavoritesTextView: TextView

    private lateinit var favoriteQuotesRecyclerView: RecyclerView
    private lateinit var favoriteQuotesAdapter: QuoteFeedAdapter

    private val favoriteQuotes = mutableListOf<Quote>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLikedQuotesBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObserver()
    }

    private fun initUi() {
        initBackButton()
        initNoFavoritesText()
        initRecyclerView()
    }

    private fun initBackButton() {
        back = binding.imageViewFavoritesBack
        back.setOnClickListener {
            findNavController().navigate(LikedQuotesFragmentDirections.actionLikedQuotesFragmentToFeedFragment())
        }
    }

    private fun initNoFavoritesText() {
        noFavoritesTextView = binding.textViewFavoritesNoFavorites
        noFavoritesTextView.visibility = View.VISIBLE
    }

    private fun initRecyclerView() {
        val favoriteStatusChanger: (Quote) -> Unit = { quote ->
            if (favoriteQuotes.contains(quote)) feedViewModel.removeFromFavorites(quote)
            else feedViewModel.addToFavorites(quote)
        }

        favoriteQuotesAdapter = QuoteFeedAdapter(favoriteQuotes,favoriteQuotes,favoriteStatusChanger)
        favoriteQuotesRecyclerView = binding.recyclerViewFavoritesQuotes
        favoriteQuotesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        favoriteQuotesRecyclerView.adapter = favoriteQuotesAdapter
    }

    private fun initObserver() {
        feedViewModel.favoriteQuotes.observe(viewLifecycleOwner) { updatedFavoriteQuotes ->
            updateFavoriteQuotes(updatedFavoriteQuotes)
            switchNoResultsTextVisibility(updatedFavoriteQuotes.isEmpty())
        }
    }

    private fun updateFavoriteQuotes(updatedFavoriteQuotes: List<Quote>) {
        favoriteQuotes.clear()
        favoriteQuotes.addAll(updatedFavoriteQuotes)
        favoriteQuotesAdapter.notifyDataSetChanged()
    }

    private fun switchNoResultsTextVisibility(listIsEmpty: Boolean) {
        if(listIsEmpty) noFavoritesTextView.visibility = View.VISIBLE
        else noFavoritesTextView.visibility = View.GONE
    }
}