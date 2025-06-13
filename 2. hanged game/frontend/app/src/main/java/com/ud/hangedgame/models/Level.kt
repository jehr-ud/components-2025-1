package com.ud.hangedgame.models

import com.google.gson.annotations.SerializedName

data class Level (
    @SerializedName("_id")
    val id: String = "",
    val name: String = "",
    val isUsable: Boolean = false
)

data class LevelsResponse(
    val allLevels: List<Level>
)