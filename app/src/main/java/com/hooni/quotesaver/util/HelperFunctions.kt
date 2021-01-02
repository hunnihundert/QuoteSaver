package com.hooni.quotesaver.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

internal fun getRandomImage(): Int {
    return backgroundImages.random()
}

internal fun hideKeyboard(context: Context, view: View) {
    val inputMethodManager =
        context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}