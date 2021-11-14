package com.example.kt_whatsapp_clone.fragments.inbox

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.kt_whatsapp_clone.R
import com.example.kt_whatsapp_clone.modals.Inbox
import com.example.kt_whatsapp_clone.utils.formatAsListItem
import com.squareup.picasso.Picasso

class InboxViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(item: Inbox, onClick: (name: String, photo: String, id: String) -> Unit) =
        with(itemView) {

            val timer = findViewById<TextView>(R.id.timeTv)
            val title = findViewById<TextView>(R.id.nameTv)
            val subTitle = findViewById<TextView>(R.id.subTitleTv)
            val count = findViewById<TextView>(R.id.countTv)
            val image = findViewById<ImageView>(R.id.userImageView)

            count.isVisible = item.count > 0
            count.text = item.count.toString()
            timer.text = item.time.formatAsListItem(context)

            title.text = item.name
            subTitle.text = item.msg
            Picasso.get()
                .load(item.image)
                .placeholder(R.drawable.avatar_default)
                .error(R.drawable.avatar_default)
                .into(image)
            setOnClickListener {
                onClick.invoke(item.name, item.image, item.from)
            }
        }
}
