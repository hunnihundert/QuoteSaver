package com.hooni.quotesaver.data.model

data class ApiTagResult(val count: Int, val next: String, val previous: String, val results: List<QuoteTags>?) {

    inner class QuoteTags(val name: String, val slug: String)

    fun getInnerClass(): QuoteTags {
        return QuoteTags("name","slug")
    }
}
