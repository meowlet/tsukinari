package com.meow.tsukinari.model

data class FictionCommentModel(
    val commentId: String = "",
    val userId: String = "",
    val fictionId: String = "",
    val comment: String = "",
    val commentTime: Long = 0
)

//

data class ChapterCommentModel(
    val commentId: String = "",
    val userId: String = "",
    val chapterId: String = "",
    val comment: String = "",
    val commentTime: Long = 0
)