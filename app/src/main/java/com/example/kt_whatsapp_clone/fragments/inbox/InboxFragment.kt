package com.example.kt_whatsapp_clone.fragments.inbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kt_whatsapp_clone.*
import com.example.kt_whatsapp_clone.adapters.ChatAdapter
import com.example.kt_whatsapp_clone.modals.Inbox
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

class InboxFragment : Fragment() {
    private lateinit var mAdapter: FirebaseRecyclerAdapter<Inbox, ChatAdapter.ChatViewHolder>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val mDatabase by lazy {
        FirebaseDatabase.getInstance()
    }
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewManager = LinearLayoutManager(requireContext())
        setupAdapter()
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    private fun setupAdapter() {

        val baseQuery: Query =
            mDatabase.reference.child("chats").child(auth.uid!!)

        val options = FirebaseRecyclerOptions.Builder<Inbox>()
            .setLifecycleOwner(viewLifecycleOwner)
            .setQuery(baseQuery, Inbox::class.java)
            .build()

        /** Paging Adapter **/


        mAdapter = object : FirebaseRecyclerAdapter<Inbox, ChatAdapter.ChatViewHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): ChatAdapter.ChatViewHolder {
                val inflater = layoutInflater
                return ChatAdapter.ChatViewHolder(
                    inflater.inflate(
                        R.layout.list_item,
                        parent,
                        false
                    )
                )
            }

            override fun onBindViewHolder(
                viewHolder: ChatAdapter.ChatViewHolder,
                position: Int,
                inbox: Inbox
            ) {

                viewHolder.bind(inbox) { name: String, photo: String, id: String ->
                    startActivity(
                        ChatActivity.createChatActivity(
                            requireContext(),
                            id,
                            name,
                            photo
                        )
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter.stopListening()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvChat.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = mAdapter
        }
    }
}

