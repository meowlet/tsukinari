package com.meow.tsukinari.model


data class FictionModel(
    val fictionId: String = "",
    val uploaderId: String = "",
    val title: String = "",
    val genre: List<String> = listOf(),
    val description: String = "",
    val uploadedAt: Long = System.currentTimeMillis(),
    val coverLink: String = "",

    //status of the fiction
    val finished: Boolean = false,
    val verified: Boolean = false,
)