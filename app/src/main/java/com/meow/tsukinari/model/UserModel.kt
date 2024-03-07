package com.meow.tsukinari.model

data class UserModel(
    val email: String = "",
    val userName: String = "",
    val displayName: String = "Display Name",
    val createdAt: Long = 0,
    val profileImageUrl: String = "https://cdn.discordapp.com/avatars/854426321463279647/0ad6c1a4f996141c3c76de9967adf58c.webp?size=1024&format=webp&width=0&height=320",
)