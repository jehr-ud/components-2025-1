package com.ud.hangedgame.models

import com.google.gson.annotations.SerializedName

data class Word(
    val word: String = "",
    val sound: Sound = Sound(),
    val level: String = "",
    @SerializedName("description_en")
    val descriptionEn: String = "",
    @SerializedName("description_es")
    val descriptionEs: String = ""
)