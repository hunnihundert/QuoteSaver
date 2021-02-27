package com.hooni.quotesaver


import android.os.Bundle
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
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

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
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.fullscreenFragment) {
                bottomNavigationView.visibility = View.GONE
            } else {
                bottomNavigationView.visibility = View.VISIBLE
            }
        }
    }
}