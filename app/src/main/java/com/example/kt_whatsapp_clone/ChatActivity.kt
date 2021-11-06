package com.example.kt_whatsapp_clone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

const val UID = "uid"
const val NAME = "name"
const val IMAGE = "photo"

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
    }
}