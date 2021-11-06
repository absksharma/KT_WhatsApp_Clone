package com.example.kt_whatsapp_clone.modals

data class User(
    val name: String,
    val imageUrl: String,
    val thumbImage: String,
    val uId: String,
    val deviceToken: String,
    val Status: String,
    val onlineState: String

) {
    constructor() : this("", "", "", "", "", "", "")
    constructor(name: String, imageUrl: String, thumbImage: String, uId: String) : this(
        name,
        imageUrl,
        thumbImage,
        uId,
        "",
        "Hey There! i'm using whats app",
        ""
    )
}
