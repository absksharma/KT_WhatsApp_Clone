package com.example.kt_whatsapp_clone.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kt_whatsapp_clone.*
import com.example.kt_whatsapp_clone.databinding.FragmentChatBinding
import com.example.kt_whatsapp_clone.modals.User
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


private const val NORMAL_VIEW_TYPE = 2
private const val DELETED_VIEW_TYPE = 1

class PeopleFragment : Fragment(R.layout.fragment_chat) {
    private lateinit var binding: FragmentChatBinding

    private lateinit var mAdapter: FirestorePagingAdapter<User, RecyclerView.ViewHolder>

    val auth by lazy {
        FirebaseAuth.getInstance()
    }
    private val dataBase by lazy {
        FirebaseFirestore.getInstance()!!.collection("users")
    }

    override
    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        setupAdapter()  /* SETTING ADAPTER TO RECYCLER VIEW */
        return binding.root
    }

    private fun setupAdapter() {

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(2)
            .setPageSize(10)
            .build()


        val options = FirestorePagingOptions.Builder<User>()
            .setLifecycleOwner(this)
            .setQuery(dataBase, config, User::class.java)
            .build()

        mAdapter = object : FirestorePagingAdapter<User, RecyclerView.ViewHolder>(options) {

            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder {
                return when (viewType) {
                    NORMAL_VIEW_TYPE -> {
                        UserViewHolder(layoutInflater.inflate(R.layout.list_item, parent, false))
                    }
                    else -> EmptyViewHolder(
                        layoutInflater.inflate(
                            R.layout.empty_view,
                            parent,
                            false
                        )
                    )
                }
            }

            override fun onBindViewHolder(
                holder: RecyclerView.ViewHolder, position: Int, model: User
            ) {
                if (holder is UserViewHolder) {
                    holder.bind(user = model) { name: String, photo: String, id: String ->
                        val intent = Intent(requireContext(), ChatActivity::class.java)
                        intent.putExtra(UID, id)
                        intent.putExtra(NAME, name)
                        intent.putExtra(IMAGE, photo)
                        startActivity(intent)
                    }
                } else {

                }
            }

            override fun onLoadingStateChanged(state: LoadingState) {
                super.onLoadingStateChanged(state)
                when (state) {
                    LoadingState.ERROR -> {
                    }
                    LoadingState.FINISHED -> {
                    }
                    LoadingState.LOADED -> {
                    }
                    LoadingState.LOADING_MORE -> {
                    }
                    LoadingState.LOADING_INITIAL -> {
                    }
                }
            }

            override fun onError(e: Exception) {
                super.onError(e)
                Log.e("Profile Fragment", e.message.toString())
            }

            override fun getItemViewType(position: Int): Int {
                val item = getItem(position)?.toObject(User::class.java)
                return if (auth.uid == item!!.uid) {
                    DELETED_VIEW_TYPE
                } else {
                    NORMAL_VIEW_TYPE
                }
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvChat.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }
}

