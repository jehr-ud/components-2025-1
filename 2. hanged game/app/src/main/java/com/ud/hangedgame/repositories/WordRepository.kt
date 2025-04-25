package com.ud.hangedgame.repositories

import android.content.Context
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ud.hangedgame.models.Word
import java.io.IOException

class WordRepository(private val context: Context) {

    fun getRandomWordByLevel(level: String): Word? {
        val words = loadWordsFromJson() ?: return null
        val filtered = words.filter { it.level.equals(level, ignoreCase = true) }
        return filtered.randomOrNull()
    }

    private fun loadWordsFromJson(): List<Word>? {
        return try {
            val inputStream = context.assets.open("words.json")
            val json = inputStream.bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<Word>>() {}.type
            Gson().fromJson<List<Word>>(json, listType)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
