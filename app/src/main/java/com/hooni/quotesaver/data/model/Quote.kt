package com.hooni.quotesaver.data.model

data class Quote(
    val quote: String,
    val author: String,
    var likes: Int,
    val tags: List<String>,
    val pk: Int,
    val image: String?,
    val language: String
    )