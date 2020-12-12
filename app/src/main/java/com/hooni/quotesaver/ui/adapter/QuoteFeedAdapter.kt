package com.hooni.quotesaver.ui.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.hooni.quotesaver.R
import com.hooni.quotesaver.data.model.Quote
import com.hooni.quotesaver.databinding.ListItemQuoteBinding
import com.squareup.picasso.Picasso

class QuoteFeedAdapter(private val quotes: List<Quote>, private val favoriteQuotes: List<Quote>, private val favoriteClickListener: (Quote) -> Unit) :
    RecyclerView.Adapter<QuoteFeedAdapter.QuoteViewHolder>() {

    companion object {
        private const val TAG = "QuoteFeedAdapter"
    }

    class QuoteViewHolder(private val binding: ListItemQuoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(quote: Quote, favoriteClickListener: (Quote) -> Unit, favoriteQuotes: List<Quote>) {
            binding.textViewListItemQuoteQuote.text = quote.quote
            binding.imageViewListItemQuoteLiked.setOnClickListener {
                addToFavorites(quote, favoriteClickListener, favoriteQuotes)
            }
            binding.imageViewListItemQuoteShare.setOnClickListener {
                shareQuote(quote)
            }
            setBackgroundImage(binding.imageViewListItemQuoteBackgroundImage, quote.image!!.toInt())

            if (favoriteQuotes.contains(quote)) binding.imageViewListItemQuoteLiked.setImageResource(R.drawable.ic_favorite)
            else binding.imageViewListItemQuoteLiked.setImageResource(R.drawable.ic_favorite_border)

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

        private fun addToFavorites(quote: Quote, likeClickListener: (Quote) -> Unit, favoriteQuotes: List<Quote>) {
            likeClickListener(quote)
            if (favoriteQuotes.contains(quote)) binding.imageViewListItemQuoteLiked.setImageResource(R.drawable.ic_favorite)
            else binding.imageViewListItemQuoteLiked.setImageResource(R.drawable.ic_favorite_border)
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemQuoteBinding.inflate(inflater, parent, false)
        return QuoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        val item = quotes[position]
        holder.bindView(item, favoriteClickListener, favoriteQuotes)
    }

    override fun getItemCount(): Int = quotes.size
}