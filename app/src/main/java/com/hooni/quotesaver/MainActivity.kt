package com.hooni.quotesaver


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hooni.quotesaver.ui.view.FavoritesFragmentDirections
import com.hooni.quotesaver.ui.view.FeedFragmentDirections

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initBottomNavigation()
    }

    private fun initBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.feedFragment_menu -> {
                    findNavController(R.id.nav_host_fragment).navigate(
                        FavoritesFragmentDirections.actionFavoritesFragmentToFeedFragment()
                    )
                    true
                }
                R.id.favoritesFragment_menu -> {
                    findNavController(R.id.nav_host_fragment).navigate(
                        FeedFragmentDirections.actionFeedFragmentToFavoritesFragment()
                    )
                    true
                }
                else -> false
            }
        }
        bottomNavigationView.setOnNavigationItemReselectedListener {}
    }
}