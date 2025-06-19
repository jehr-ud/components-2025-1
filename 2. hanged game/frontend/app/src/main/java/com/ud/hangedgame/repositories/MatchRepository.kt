package com.ud.hangedgame.repositories

import com.google.firebase.database.DataSnapshot
import com.ud.hangedgame.models.Match
import com.ud.hangedgame.models.enums.MatchState

import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MatchRepository {

    private val database = Firebase.database.getReference("matches")

    /**
     * Crea una nueva partida en Firebase con el primer jugador.
     * @param player1Id El ID del jugador que crea la partida.
     * @return El ID de la partida creada o null si falla.
     */
    suspend fun createMatch(player1Id: String): String? {
        val matchId = database.push().key ?: return null

        val newMatch = Match(
            matchId = matchId,
            player1Id = player1Id,
            state = MatchState.WAITING.name
        )

        return try {
            database.child(matchId).setValue(newMatch).await()
            matchId
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Une a un segundo jugador a una partida existente.
     * @param matchId El ID de la partida a la que se unirá.
     * @param player2Id El ID del segundo jugador.
     * @return True si se unió con éxito, false en caso contrario.
     */
    suspend fun joinMatch(matchId: String, player2Id: String): Boolean {
        return try {
            val matchRef = database.child(matchId)
            val matchSnapshot = matchRef.get().await()
            val currentMatch = matchSnapshot.getValue(Match::class.java)

            if (currentMatch != null && currentMatch.player2Id == null && currentMatch.state == MatchState.WAITING.name) {
                val updates = mapOf(
                    "player2Id" to player2Id,
                    "state" to MatchState.IN_PROGRESS.name
                )
                matchRef.updateChildren(updates).await()
                true
            } else {
                // La sala ya está llena o no existe
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    fun listenForMatchUpdates(matchId: String): Flow<Match?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val match = snapshot.getValue(Match::class.java)
                trySend(match).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        database.child(matchId).addValueEventListener(listener)

        awaitClose {
            database.child(matchId).removeEventListener(listener)
        }
    }
}
