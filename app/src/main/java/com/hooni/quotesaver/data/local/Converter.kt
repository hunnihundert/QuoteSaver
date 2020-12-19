package com.hooni.quotesaver.data.local

import androidx.room.TypeConverter

class Converter {
    companion object {
        private const val TAG = "Converter"
    }

    @TypeConverter
    fun convertTagsListToString(tagList: List<String>): String {
        var tagListString = ""
        for(tag in tagList) tagListString += "$tag,"
        tagListString = tagListString.removeSuffix(",")
        return tagListString
    }

    @TypeConverter
    fun convertStringToTagsList(tagListString: String): List<String> {
        return tagListString.split(",").map{it.trim()}
    }
}