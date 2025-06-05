package com.ud.hangedgame.repositories

import com.ud.hangedgame.models.Word
import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await


class WordRepository {
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val wordReference: DatabaseReference = database.getReference("words")
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
