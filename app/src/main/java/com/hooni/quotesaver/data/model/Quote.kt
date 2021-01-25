package com.hooni.quotesaver.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Quote(
    val quote: String,
    val author: String?,
    var likes: Int,
    val tags: List<String>,
    @PrimaryKey val pk: Int,
    val image: String?,
    val language: String
    ) {
    override fun equals(other: Any?): Boolean {
        if(other == null || other !is Quote) return false
        return pk == other.pk
    }

    override fun hashCode(): Int {
        return pk + quote.hashCode() + author.hashCode()
    }

    override fun toString(): String {
        return "$quote, $author"
    }
}