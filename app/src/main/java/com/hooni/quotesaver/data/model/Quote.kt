package com.hooni.quotesaver.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Quote(
    val quote: String,
    val author: String,
    var likes: Int,
    val tags: List<String>,
    @PrimaryKey val pk: Int,
    val image: String?,
    val language: String
    )