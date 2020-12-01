package com.hooni.quotesaver.data.remote

import com.hooni.quotesaver.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain
            .request()
            .newBuilder()
            .header("Authorization", "Token ${BuildConfig.API_KEY}")
            .build()
        return chain.proceed(request)
    }
}