package com.hooni.quotesaver.data.local

import android.util.Log
import androidx.room.TypeConverter

class Converter {
    companion object {
        private const val TAG = "Converter"
    }

    @TypeConverter
    fun convertTagsListToString(tagList: List<String>): String {
        Log.d(TAG, "convertTagsListToString: StringList: $tagList")
        var tagListString = ""
        for(tag in tagList) tagListString += "$tag,"
        tagListString = tagListString.removeSuffix(",")
        Log.d(TAG, "convertTagsListToString: String: $tagListString")
        return tagListString
    }

    @TypeConverter
    fun convertStringToTagsList(tagListString: String): List<String> {
        Log.d(TAG, "convertStringToTagsList: String: $tagListString")
        val listOfTags = tagListString.split(",").map{it.trim()}
        Log.d(TAG, "convertStringToTagsList: StringList: ${listOfTags}")
        return listOfTags
    }
}