package com.hooni.quotesaver

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hooni.quotesaver.di.modules.LocalModule
import com.hooni.quotesaver.di.modules.remoteModule
import com.hooni.quotesaver.di.modules.repositoryModule
import com.hooni.quotesaver.di.modules.viewModelModule
import com.hooni.quotesaver.ui.view.FeedFragment
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragment = FeedFragment()
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().add(R.id.main_fragment, fragment).commit()
    }
}