package com.meow.tsukinari.model

import com.google.firebase.Timestamp


val currentTimeMillis = System.currentTimeMillis()

data class FictionModel(
    val uploaderId: String = "",
    val title: String = "",
    val description: String = "",
    val uploadedAt: Timestamp = Timestamp.now()
)


data class Notes(
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val colorIndex: Int = 0,
    val documentId: String = "",
)