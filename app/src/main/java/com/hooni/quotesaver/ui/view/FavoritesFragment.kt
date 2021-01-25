package com.hooni.quotesaver.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hooni.quotesaver.data.model.Quote
import com.hooni.quotesaver.databinding.FragmentFavoriteQuotesBinding
import com.hooni.quotesaver.ui.adapter.FavoritesAdapter
import com.hooni.quotesaver.ui.viewmodel.FeedViewModel
import com.hooni.quotesaver.util.hideKeyboard
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FavoritesFragment: Fragment() {

    private val feedViewModel: FeedViewModel by sharedViewModel()
    private lateinit var binding: FragmentFavoriteQuotesBinding

    private lateinit var back: ImageView
    private lateinit var noFavoritesTextView: TextView
    private lateinit var loadingView: LinearLayout

    private lateinit var favoriteQuotesRecyclerView: RecyclerView
    private lateinit var favoriteQuotesAdapter: FavoritesAdapter

    private val favoriteQuotes = mutableListOf<Quote>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteQuotesBinding.inflate(inflater,container,false)
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
        initLoadingView()
        initNoFavoritesText()
        initRecyclerView()
        hideKeyboard(requireContext(),binding.root)
    }

    private fun initBackButton() {
        back = binding.imageViewFavoritesBack
        back.setOnClickListener {
            findNavController().navigate(FavoritesFragmentDirections.actionFavoritesFragmentToFeedFragment())
        }
    }

    private fun initLoadingView() {
        loadingView = binding.linearLayoutFavoritesLoading
        loadingView.visibility = View.GONE
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
        val fullscreenOpener: (Quote) -> Unit = { quote ->
            feedViewModel.setFullscreenQuote(quote)
            findNavController().navigate(FavoritesFragmentDirections.actionFavoritesFragmentToFullscreenFragment())
        }

        favoriteQuotesAdapter = FavoritesAdapter(favoriteQuotes,favoriteStatusChanger,fullscreenOpener)
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