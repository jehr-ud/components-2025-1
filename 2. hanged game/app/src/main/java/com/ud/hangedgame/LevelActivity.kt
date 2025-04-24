package com.ud.hangedgame

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text


class LevelActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent{
            Column{
                Button(onClick = {
                    startActivity(Intent(this@LevelActivity, MainActivity::class.java))
                }) {
                    Text("Level 1")
                }
            }
    }
    }
}