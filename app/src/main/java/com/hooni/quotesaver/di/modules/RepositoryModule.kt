package com.hooni.quotesaver.di.modules

import com.hooni.quotesaver.repository.QuoteRepository
import com.hooni.quotesaver.data.local.FavoriteQuotesDao
import com.hooni.quotesaver.data.remote.QuotesApi
import org.koin.dsl.module

val repositoryModule = module {
    single { provideRepository(get(), get()) }
}

private fun provideRepository(
    quotesApi: QuotesApi,
    favoriteQuotesDao: FavoriteQuotesDao,
): QuoteRepository {
    return QuoteRepository(quotesApi, favoriteQuotesDao)
}