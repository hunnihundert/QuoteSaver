package com.hooni.quotesaver.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.hooni.quotesaver.R
import com.hooni.quotesaver.databinding.LoadStateFooterViewItemBinding

class LoadStateViewHolder(
    private val binding: LoadStateFooterViewItemBinding,
    private val retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.textViewLoadStateFooterErrorMessage.text = loadState.error.localizedMessage
        }
        binding.progressBarLoadStateFooterProgress.isVisible = loadState is LoadState.Loading
        binding.textViewLoadStateFooterErrorMessage.isVisible = loadState !is LoadState.Loading
        binding.buttonLoadStateFooterRetry.isVisible = loadState !is LoadState.Loading
        binding.buttonLoadStateFooterRetry.setOnClickListener { retry.invoke() }
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): LoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.load_state_footer_view_item, parent, false)
            val binding = LoadStateFooterViewItemBinding.bind(view)
            return LoadStateViewHolder(binding, retry)
        }
    }
}