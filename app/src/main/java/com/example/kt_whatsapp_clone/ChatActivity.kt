package com.example.kt_whatsapp_clone

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kt_whatsapp_clone.adapters.ChatAdapter
import com.example.kt_whatsapp_clone.databinding.ActivityChatBinding
import com.example.kt_whatsapp_clone.modals.*
import com.example.kt_whatsapp_clone.utils.isSameDayAs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider

const val UID = "uid"
const val NAME = "name"
const val IMAGE = "photo"
private const val TAG = "ChatActivity"

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

    private val messages = mutableListOf<ChatEvent>()
    lateinit var chatAdapter: ChatAdapter


    private val db: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*** Google Emoji Provider ***/
        EmojiManager.install(GoogleEmojiProvider())

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)


        FirebaseFirestore.getInstance().collection("users").document(mCurrentUid).get()
            .addOnSuccessListener {
                currentUser = it.toObject(User::class.java)!!
            }
        chatAdapter = ChatAdapter(messages, mCurrentUid)

        binding.rvChat.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = chatAdapter
        }
        binding.nameTv.text = name
        Picasso.get().load(image)
            .into(binding.userImageView)

        listenToMessages()

        binding.sendBtn.setOnClickListener {
            binding.msgEdtv.text?.let {
                if (it.isNotEmpty()) {
                    sendMessage(it.toString())
                    it.clear()
                }
            }
        }

    }

    private fun listenToMessages() {
        getMessages(friendId).orderByKey().addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val msg = snapshot.getValue(Message::class.java)!!
                addMessages(msg)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun addMessages(msg: Message) {
        val eventBefore = messages.lastOrNull()

        if ((eventBefore != null && !eventBefore.sentAt.isSameDayAs(msg.sentAt)) || eventBefore == null) {
            messages.add(
                DateHeader(msg.sentAt, this)
            )
        }
        messages.add(msg)
        chatAdapter.notifyItemInserted(messages.size - 1)
        binding.rvChat.scrollToPosition(messages.size - 1)

    }

    private fun sendMessage(msg: String) {
        val id = getMessages(friendId).push().key
        checkNotNull(id) { "CANT BE NULL" }
        val msgMap = Message(msg, mCurrentUid, id)
        getMessages(friendId).child(id).setValue(msgMap).addOnSuccessListener {
            Log.i(TAG, "Message send")
        }.addOnFailureListener {
            Log.i(TAG, it.localizedMessage)
        }
        updateLastMessage(msgMap)
    }

    private fun updateLastMessage(message: Message) {
        val inboxMap = Inbox(
            message.msg,
            friendId,
            name,
            image,
            count = 0,
        )
        getInbox(mCurrentUid, friendId).setValue(inboxMap).addOnSuccessListener {
            getInbox(friendId, mCurrentUid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.getValue(Inbox::class.java)

                    inboxMap.apply {
                        from = message.senderId
                        name = currentUser.name
                        image = currentUser.thumbImage
                        count = 1
                    }
                    value?.let {
                        if (it.from == message.senderId) {
                            inboxMap.count = value.count + 1
                        }
                    }
                    getInbox(friendId, mCurrentUid).setValue(inboxMap)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    private fun markAsRead() {
        getInbox(friendId, mCurrentUid).child("count").setValue(0)
    }


    private fun getMessages(friendId: String) = db.reference.child("messages/${getId(friendId)}")

    private fun getInbox(toUser: String, fromUser: String) =
        db.reference.child("chats/$toUser$fromUser")


    private fun getId(friendId: String): String {  // Messages ID'S
        return if (friendId > mCurrentUid) {
            mCurrentUid + friendId
        } else {
            friendId + mCurrentUid
        }
    }

}