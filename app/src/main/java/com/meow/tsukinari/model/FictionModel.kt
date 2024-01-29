package com.meow.tsukinari.model


data class FictionModel(
    val fictionId: String = "",
    val uploaderId: String = "",
    val title: String = "",
    val description: String = "",
    val uploadedAt: Long = System.currentTimeMillis(),
    val coverLink: String = ""
)