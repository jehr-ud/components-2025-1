package com.ud.hangedgame.models

import com.ud.hangedgame.models.enums.MatchState

data class Match(
    val matchId: String? = null,
    val player1Id: String? = null,
    val player2Id: String? = null,
    val player1Score: Int = 0,
    val player2Score: Int = 0,
    val winnerId: String? = null,
    val state: String = MatchState.WAITING.name
)