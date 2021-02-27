package com.hooni.quotesaver.ui.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.hooni.quotesaver.data.model.Quote

class QuoteFeedAdapter(
    private val favoriteQuotes: List<Quote>,
    private val favoriteClickListener: (Quote) -> Unit,
    private val fullScreenClickListener: (Quote) -> Unit
) :
    PagingDataAdapter<Quote, QuoteViewHolder>(REPO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        return QuoteViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.bindView(item, favoriteClickListener, favoriteQuotes, fullScreenClickListener)
        }
    }

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<Quote>() {
            override fun areItemsTheSame(oldItem: Quote, newItem: Quote): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: Quote, newItem: Quote): Boolean =
                oldItem.quote == newItem.quote
        }
    }
}