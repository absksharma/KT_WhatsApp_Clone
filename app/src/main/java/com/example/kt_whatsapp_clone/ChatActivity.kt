package com.example.kt_whatsapp_clone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kt_whatsapp_clone.databinding.ActivityChatBinding
import com.example.kt_whatsapp_clone.modals.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider

const val UID = "uid"
const val NAME = "name"
const val IMAGE = "photo"

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding

    private val friendId: String by lazy<String> {
        intent.getStringExtra("uid").toString()
    }
    private val name: String by lazy<String> {
        intent.getStringExtra(NAME).toString()
    }
    private val image: String by lazy<String> {
        intent.getStringExtra(IMAGE).toString()
    }
    private val mCurrentUid: String by lazy {
        FirebaseAuth.getInstance().uid!!
    }
    private lateinit var currentUser: User

    private val db: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*** Google Emoji Provider ***/
        EmojiManager.install(GoogleEmojiProvider())

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nameTv.text = name
        Picasso.get().load(image)
            .into(binding.userImageView)

        suspend {
            FirebaseFirestore.getInstance().collection("users").document(mCurrentUid).get()
                .addOnSuccessListener {
                    currentUser = it.toObject(User::class.java)!!
                }
        }

    }
}