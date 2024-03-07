package com.meow.tsukinari.model

//create a data class for the chapter (manga chapter), use the array of this data class to url of the images for each page of the chapter
data class ChapterModel(
    val chapterId: String = "",
    val fictionId: String = "",
    val chapterNumber: Int = 0,
    val chapterTitle: String = "",
    val chapterPages: List<String> = emptyList(),
    val uploadedAt: Long = System.currentTimeMillis()
)