package com.example.kt_whatsapp_clone.modals

data class User(
    val name: String,
    val imageUrl: String,
    val thumbImage: String,
    val uid: String,
    val deviceToken: String,
    val status: String,
    val online: Boolean

) {
    constructor() : this("", "", "", "", "", "", false)
    constructor(name: String, imageUrl: String, thumbImage: String, uid: String) : this(
        name,
        imageUrl,
        thumbImage,
        uid,
        "",
        status = "hey there i'm using Whats App",
        online = false
    )
}
