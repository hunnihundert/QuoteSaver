package com.hooni.quotesaver

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hooni.quotesaver.ui.view.FeedFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragment = FeedFragment()
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().add(R.id.main_fragment, fragment).commit()
    }
}