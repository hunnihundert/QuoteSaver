package com.hooni.quotesaver.util

import org.junit.Assert.*
import org.junit.Test

class HelperFunctionsKtTest{

    @Test
    fun `getRandomImage should return one item of backgroundImages`() {
        val result = getRandomImage()
        assertEquals(backgroundImages.contains(result),true)
    }
}