package com.hooni.quotesaver.ui.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.hooni.quotesaver.R
import com.hooni.quotesaver.data.model.Quote
import com.hooni.quotesaver.databinding.ListItemQuoteBinding
import com.squareup.picasso.Picasso

class QuoteFeedAdapter(
    private val quotes: List<Quote>,
    private val favoriteQuotes: List<Quote>,
    private val favoriteClickListener: (Quote) -> Unit,
    private val fullScreenClickListener: (Quote) -> Unit
) :
    RecyclerView.Adapter<QuoteFeedAdapter.QuoteViewHolder>() {

    companion object {
        private const val TAG = "QuoteFeedAdapter"
    }

    inner class QuoteViewHolder(private val binding: ListItemQuoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(
            quote: Quote,
            favoriteClickListener: (Quote) -> Unit,
            favoriteQuotes: List<Quote>,
            fullScreenClickListener: (Quote) -> Unit
        ) {
            binding.textViewListItemQuoteQuote.text = quote.quote
            binding.imageViewListItemQuoteFavorite.setOnClickListener {
                Log.d(TAG, "bindView: add to favorites")
                addToFavorites(quote, favoriteClickListener, favoriteQuotes)
            }
            binding.imageViewListItemQuoteShare.setOnClickListener {
                shareQuote(quote)
            }
            binding.textViewListItemQuoteQuote.setOnClickListener {
                fullScreenClickListener(quote)
            }
            setBackgroundImage(binding.imageViewListItemQuoteBackgroundImage, quote.image!!.toInt())
            setFavoriteImage(favoriteQuotes.contains(quote))
        }

        private fun addToFavorites(
            quote: Quote,
            favoriteClickListener: (Quote) -> Unit,
            favoriteQuotes: List<Quote>
        ) {
            favoriteClickListener(quote)
            setFavoriteImage(favoriteQuotes.contains(quote))
        }

        private fun shareQuote(quote: Quote) {
            val intentContent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, quote.quote)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(intentContent, "Share your Quote!")
            binding.root.context.startActivity(shareIntent)
        }

        private fun setBackgroundImage(view: ImageView, resourceId: Int) {
            Picasso.get()
                .load(resourceId)
                .fit()
                .centerCrop()
                .into(view)
        }

        private fun setFavoriteImage(isFavorite: Boolean) {
            Log.d(TAG, "setFavoriteImage: isFavorite: $isFavorite")
            if (isFavorite) binding.imageViewListItemQuoteFavorite.setImageResource(R.drawable.ic_favorite)
            else binding.imageViewListItemQuoteFavorite.setImageResource(R.drawable.ic_favorite_border)
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemQuoteBinding.inflate(inflater, parent, false)
        return QuoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        val item = quotes[position]
        holder.bindView(item, favoriteClickListener, favoriteQuotes, fullScreenClickListener)
    }

    override fun getItemCount(): Int = quotes.size
}