package com.hooni.quotesaver.di.modules

import com.hooni.quotesaver.data.QuoteRepository
import com.hooni.quotesaver.data.remote.QuotesApi
import org.koin.dsl.module

val repositoryModule = module {

    fun provideRepository(quotesApi: QuotesApi): QuoteRepository {
        return QuoteRepository(quotesApi)
    }

    single { provideRepository(get()) }
}