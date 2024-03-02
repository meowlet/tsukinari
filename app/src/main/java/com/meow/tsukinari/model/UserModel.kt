package com.meow.tsukinari.model

data class UserModel(
    val id: String = "",
    val username: String = "",
    val displayName: String = "",
    val createdAt: Long = 0,
    val profileImageUrl: String = "",
)