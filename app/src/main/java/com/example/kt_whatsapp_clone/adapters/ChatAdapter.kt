package com.example.kt_whatsapp_clone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kt_whatsapp_clone.R
import com.example.kt_whatsapp_clone.modals.ChatEvent
import com.example.kt_whatsapp_clone.modals.DateHeader
import com.example.kt_whatsapp_clone.modals.Inbox
import com.example.kt_whatsapp_clone.modals.Message
import com.example.kt_whatsapp_clone.utils.formatAsTime

class ChatAdapter(private val list: MutableList<ChatEvent>, private val mCurrentUserId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflate = { layout: Int ->
            LayoutInflater.from(parent.context).inflate(layout, parent, false)
        }

        return when (viewType) {
            TEXT_MESSAGE_RECEIVED -> {
                ChatViewHolder(inflate(R.layout.list_item_chat_recieved))
            }
            TEXT_MESSAGE_SENT -> {
                ChatViewHolder(inflate(R.layout.list_item_chat_send))
            }
            DATE_HEADER -> {
                DateViewHolder(inflate(R.layout.list_item_date_header))
            }
            else -> ChatViewHolder(inflate(R.layout.list_item_chat_recieved))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val textView = holder.itemView.findViewById<TextView>(R.id.textView)
        val content = holder.itemView.findViewById<TextView>(R.id.content)
        val time = holder.itemView.findViewById<TextView>(R.id.time)

        when (val item = list[position]) {
            is DateHeader -> {
                textView.text = item.date
            }
            is Message -> {
                holder.apply {
                    content.text = item.msg
                    time.text = item.sentAt.formatAsTime()
                }
            }
        }
    }

    override fun getItemCount(): Int = list.size

    class DateViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(inbox: Inbox, function: (String, String, String) -> Unit) {

        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (val event = list[position]) {
            is Message -> {
                if (event.senderId == mCurrentUserId) {
                    TEXT_MESSAGE_SENT
                } else {
                    TEXT_MESSAGE_RECEIVED
                }
            }
            is DateHeader -> DATE_HEADER
            else -> UNSUPPORTED
        }
    }

    companion object {
        private const val UNSUPPORTED = -1
        private const val TEXT_MESSAGE_RECEIVED = 0
        private const val TEXT_MESSAGE_SENT = 1
        private const val DATE_HEADER = 2
    }
}