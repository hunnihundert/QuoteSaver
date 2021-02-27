package com.hooni.quotesaver.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hooni.quotesaver.data.model.Quote

class FavoritesAdapter(
    private val favoriteQuotes: List<Quote>,
    private val favoriteClickListener: (Quote) -> Unit,
    private val fullScreenClickListener: (Quote) -> Unit
) : RecyclerView.Adapter<QuoteViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        return QuoteViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        val item = favoriteQuotes[position]
        holder.bindView(item, favoriteClickListener, favoriteQuotes, fullScreenClickListener)
    }

    override fun getItemCount(): Int {
        return favoriteQuotes.size
    }
}