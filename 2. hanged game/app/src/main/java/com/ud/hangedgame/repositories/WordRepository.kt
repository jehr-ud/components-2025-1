package com.ud.hangedgame.repositories

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ud.hangedgame.models.Word
import java.io.IOException
import com.google.firebase.database.*


class WordRepository(private val context: Context) {

    fun getRandomWordByLevel(level: String): Word? {
        val words = loadWordsFromJson() ?: return null
        val filtered = words.filter { it.level.equals(level, ignoreCase = true) }
        return filtered.randomOrNull()
    }

    private fun loadWordsFromJson(): List<Word>? {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val wordDatabase: DatabaseReference = database.getReference("words")
        val keywordList = mutableListOf<Word>()
        wordDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val word = childSnapshot.getValue(Word::class.java)
                    word?.let {
                        keywordList.add(it)
                    }
                }
                println("Palabras obtenidas: $keywordList")
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error al leer los datos: ${error.message}")
            }
        })
        return keywordList
    }
}
