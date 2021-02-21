package com.hooni.quotesaver.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.hooni.quotesaver.R
import com.hooni.quotesaver.data.model.Quote
import com.hooni.quotesaver.databinding.ListItemQuoteBinding
import com.hooni.quotesaver.util.PicassoTransformationDarken
import com.squareup.picasso.Picasso

class QuoteViewHolder(private val binding: ListItemQuoteBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindView(
        quote: Quote,
        favoriteClickListener: (Quote) -> Unit,
        favoriteQuotes: List<Quote>,
        fullScreenClickListener: (Quote) -> Unit
    ) {
        binding.textViewListItemQuoteQuote.text = quote.quote
        binding.imageViewListItemQuoteFavorite.setOnClickListener {
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

        val shareIntent = Intent.createChooser(
            intentContent,
            binding.root.context.getString(R.string.shareIntent_quoteViewHolder_title)
        )
        binding.root.context.startActivity(shareIntent)
    }

    private fun setBackgroundImage(view: ImageView, resourceId: Int) {
        Picasso.get()
            .load(resourceId)
            .transform(PicassoTransformationDarken())
            .fit()
            .centerCrop()
            .into(view)
    }

    private fun setFavoriteImage(isFavorite: Boolean) {
        if (isFavorite) binding.imageViewListItemQuoteFavorite.setImageResource(R.drawable.ic_favorite)
        else binding.imageViewListItemQuoteFavorite.setImageResource(R.drawable.ic_favorite_border)
    }

    companion object {
        fun create(parent: ViewGroup): QuoteViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ListItemQuoteBinding.inflate(inflater, parent, false)
            return QuoteViewHolder(binding)
        }
    }

}