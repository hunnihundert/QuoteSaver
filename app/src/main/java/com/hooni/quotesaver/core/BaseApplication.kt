package com.hooni.quotesaver.core

import android.app.Application
import com.hooni.quotesaver.di.modules.localModule
import com.hooni.quotesaver.di.modules.remoteModule
import com.hooni.quotesaver.di.modules.repositoryModule
import com.hooni.quotesaver.di.modules.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@BaseApplication)
            modules(
                remoteModule,
                repositoryModule,
                viewModelModule,
                localModule
            )
        }
    }
}