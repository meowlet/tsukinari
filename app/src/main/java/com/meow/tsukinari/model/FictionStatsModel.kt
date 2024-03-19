package com.meow.tsukinari.model

data class FictionStatsModel(
    val fictionId: String = "",
    val totalViews: Int = 0,
    val dislikedBy: List<String> = emptyList(),
    val likedBy: List<String> = emptyList(),
)