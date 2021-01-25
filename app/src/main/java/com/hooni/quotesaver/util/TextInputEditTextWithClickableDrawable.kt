// Modifying onTouchEvent requries performClick() to be called, a custom class needs to be
// implement which calls performClick() to satisfy the warning.

package com.hooni.quotesaver.util

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import com.google.android.material.textfield.TextInputEditText

class TextInputEditTextWithClickableDrawable(context: Context, attrs: AttributeSet) :
    TextInputEditText(context, attrs) {

    companion object {
        private const val RIGHT_DRAWABLE = 2
    }

    private var navigation: (() -> Unit)? = null

    fun setNavigationPoint(navigation: () -> Unit) {
        this.navigation = navigation
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_UP -> {
                if (event.rawX >= (this.right - this.compoundDrawables[RIGHT_DRAWABLE].bounds.width())) {
                    navigate()
                }
            }
        }
        performClick()
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    private fun navigate() {
        navigation?.let {
            navigation!!.invoke()
            Log.d("feed", "navigate")
        }
    }
}