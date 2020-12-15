package com.hooni.quotesaver.di.modules

import com.hooni.quotesaver.repository.QuoteRepository
import com.hooni.quotesaver.data.local.FavoriteQuotesDao
import com.hooni.quotesaver.data.remote.QuotesApi
import com.hooni.quotesaver.data.remote.ResponseHandler
import org.koin.dsl.module

val repositoryModule = module {
    single { provideRepository(get(), get(), get()) }
    single { provideResponseHandler() }
}

private fun provideRepository(
    quotesApi: QuotesApi,
    favoriteQuotesDao: FavoriteQuotesDao,
    responseHandler: ResponseHandler
): QuoteRepository {
    return QuoteRepository(quotesApi, favoriteQuotesDao, responseHandler)
}

private fun provideResponseHandler(): ResponseHandler {
    return ResponseHandler()
}