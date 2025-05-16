package com.ud.hangedgame.repositories

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ud.hangedgame.models.Word
import java.io.IOException
import com.google.firebase.database.*


class WordRepository {
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val wordDatabase: DatabaseReference = database.getReference("words")
    suspend fun getRandomWordByLevel(level: String): Word? {
        return try {
            val snapshot = wordReference.child(level.uppercase()).get().await()
            val wordList = mutableListOf<Word>()
            for (childSnapshot in snapshot.children) {
                val word = childSnapshot.getValue(Word::class.java)
                word?.let { wordList.add(it) }
            }
            wordList.randomOrNull()
        } catch (e: Exception) {
            println("Error obteniendo palabras para el nivel $level: ${e.message}")
            null
        }
    }
}
