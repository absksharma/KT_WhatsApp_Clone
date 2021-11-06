package com.example.kt_whatsapp_clone.fragments

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.kt_whatsapp_clone.R
import com.example.kt_whatsapp_clone.modals.User
import com.squareup.picasso.Picasso

class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(user: User, onClick: (name: String, photo: String, id: String) -> Unit) =
        with(itemView) {

            val timer = findViewById<TextView>(R.id.timeTv)
            val title = findViewById<TextView>(R.id.nameTv)
            val subTitle = findViewById<TextView>(R.id.subTitleTv)
            val count = findViewById<TextView>(R.id.countTv)
            val image = findViewById<ImageView>(R.id.userImageView)

            count.isVisible = false
            timer.isVisible = false

            title.text = user.name
            subTitle.text = user.Status

            Picasso.get().load(user.thumbImage).placeholder(R.drawable.avatar_default)
                .error(R.drawable.avatar_default)
                .into(image)

            setOnClickListener() {
                onClick.invoke(user.name, user.thumbImage, user.uId)
            }
        }
}

class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view)
