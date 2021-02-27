package com.hooni.quotesaver.ui.adapter

import android.content.Intent
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
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
        initFavoriteImage(favoriteQuotes.contains(quote))
    }

    private fun addToFavorites(
        quote: Quote,
        favoriteClickListener: (Quote) -> Unit,
        favoriteQuotes: List<Quote>
    ) {
        favoriteClickListener(quote)
        switchFavoriteImage(favoriteQuotes.contains(quote))
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

    private fun switchFavoriteImage(isFavorite: Boolean) {
        if (!isFavorite) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.imageViewListItemQuoteFavorite.visibility = View.GONE
                binding.imageViewListItemQuoteFavoriteAnimation.visibility = View.VISIBLE
                val animation =
                    binding.imageViewListItemQuoteFavoriteAnimation.drawable as AnimatedVectorDrawable
                animation.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                    override fun onAnimationEnd(drawable: Drawable?) {
                        binding.imageViewListItemQuoteFavorite.setImageResource(R.drawable.ic_favorite)
                        binding.imageViewListItemQuoteFavorite.visibility = View.VISIBLE
                        binding.imageViewListItemQuoteFavoriteAnimation.visibility = View.GONE
                    }
                })
                animation.start()
            } else {
                binding.imageViewListItemQuoteFavorite.setImageResource(R.drawable.ic_favorite)
            }
        } else {
            binding.imageViewListItemQuoteFavorite.setImageResource(R.drawable.ic_favorite_border)
        }
    }

    private fun initFavoriteImage(isFavorite: Boolean) {
        if (isFavorite) {
            binding.imageViewListItemQuoteFavorite.setImageResource(R.drawable.ic_favorite)
        } else {
            binding.imageViewListItemQuoteFavorite.setImageResource(R.drawable.ic_favorite_border)
        }
    }

    companion object {
        fun create(parent: ViewGroup): QuoteViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ListItemQuoteBinding.inflate(inflater, parent, false)
            return QuoteViewHolder(binding)
        }
        private const val TAG = "QuoteViewHolder"
    }

}