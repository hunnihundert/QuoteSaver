package com.hooni.quotesaver.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hooni.quotesaver.data.model.Quote
import com.hooni.quotesaver.databinding.FragmentFullscreenBinding
import com.hooni.quotesaver.ui.viewmodel.FeedViewModel
import com.squareup.picasso.Picasso
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FullscreenFragment: Fragment() {

    private lateinit var binding: FragmentFullscreenBinding
    private val feedViewModel: FeedViewModel by sharedViewModel()
    private lateinit var quoteText: TextView
    private lateinit var quote: Quote

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFullscreenBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setQuote()
        initUI()
    }

    private fun setQuote() {
        quote = feedViewModel.getQuote()
    }

    private fun initUI() {
        setText()
        setImage(binding.imageViewFullscreenBackground, quote.image!!.toInt())
    }

    private fun setText() {
        quoteText = binding.textViewFullscreenQuote
        quoteText.text = quote.quote
    }

    private fun setImage(view: ImageView, resourceId: Int) {
        Picasso.get()
            .load(resourceId)
            .fit()
            .centerCrop()
            .into(view)
    }
}