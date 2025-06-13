// src/main/java/com/ud/hangedgame/repositories/ScoreRepository.kt
package com.ud.hangedgame.repositories

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.ud.hangedgame.models.Score // Importa Score
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await // Para convertir Tareas a suspend functions

class ScoreRepository {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    fun saveScore(userId: String, score: Score, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userScoresReference: DatabaseReference = database.getReference("users")
            .child(userId)
            .child("scores")

        val scoreId = userScoresReference.push().key
        if (scoreId != null) {
            userScoresReference.child(scoreId).setValue(score)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        } else {
            onFailure(Exception("Could not generate a unique key for the score."))
        }
    }

    /**
     * Obtiene el score total para un usuario dado.
     * Utiliza Flow para escuchar cambios en tiempo real.
     */
    fun getTotalScoreForUser(userId: String): Flow<Int> = callbackFlow {
        val userScoresReference: DatabaseReference = database.getReference("users")
            .child(userId)
            .child("scores")

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalScore = 0
                for (childSnapshot in snapshot.children) {
                    val score = childSnapshot.getValue(Score::class.java)
                    score?.let {
                        totalScore += it.scoreValue
                    }
                }
                trySend(totalScore).isSuccess // Envía el score total
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error
                println("Error al leer scores: ${error.message}")
                close(error.toException()) // Cerrar el flujo con la excepción
            }
        }

        userScoresReference.addValueEventListener(valueEventListener)

        // Cuando el flow se cancela o no hay más suscriptores, elimina el listener
        awaitClose { userScoresReference.removeEventListener(valueEventListener) }
    }
}