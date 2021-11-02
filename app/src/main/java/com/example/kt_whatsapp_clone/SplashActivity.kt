package com.example.kt_whatsapp_clone

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val auth by lazy {
            FirebaseAuth.getInstance()
        }
        super.onCreate(savedInstanceState)
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}