package com.hooni.quotesaver.di.modules

import com.hooni.quotesaver.repository.QuoteRepository
import com.hooni.quotesaver.ui.viewmodel.FeedViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { provideFeedViewModel(get()) }
}

private fun provideFeedViewModel(quoteRepository: QuoteRepository): FeedViewModel {
    return FeedViewModel(quoteRepository)
}