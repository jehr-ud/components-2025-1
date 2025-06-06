package com.ud.hangedgame.repositories

import com.ud.hangedgame.models.Word
import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await


class WordRepository {
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val wordReference: DatabaseReference = database.getReference("words")

    fun getRandomWordByLevel(level: String, callback: (Word?, Exception?) -> Unit) {
        wordReference.child(level.uppercase()).get().addOnSuccessListener { snapshot ->
            val wordList = mutableListOf<Word>()
            for (childSnapshot in snapshot.children) {
                val word = childSnapshot.getValue(Word::class.java)
                word?.let { wordList.add(it) }
            }
            val randomWord = wordList.randomOrNull()
            callback(randomWord, null)
        }.addOnFailureListener { exception ->
            println("Error obteniendo palabras para el nivel $level: ${exception.message}")
            callback(null, exception)
        }
    }
}
