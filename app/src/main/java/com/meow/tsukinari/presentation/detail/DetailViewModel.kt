package com.meow.tsukinari.presentation.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.meow.tsukinari.model.ChapterModel
import com.meow.tsukinari.model.FictionCommentModel
import com.meow.tsukinari.model.UserModel
import com.meow.tsukinari.repository.DatabaseRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailViewModel(
    private val repository: DatabaseRepository = DatabaseRepository(),
) : ViewModel() {


    var detailUiState by mutableStateOf(DetailUiState())
        private set

    //get user id using get() function
    val userId: String
        get() = repository.getUserId()

    //hasUser val
    val hasUser: Boolean
        get() = repository.hasUser()

    //get all user info
    var userList = emptyList<UserModel>()

    //get all user info
    fun getUserList() {
        repository.getAllUserInfo(onError = {}, onSuccess = { users ->
            userList = users!!
        })
    }

    fun hasComments(): Boolean {
        return detailUiState.commentList.isNotEmpty()
    }

    fun getTime(uploadedAt: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
        return sdf.format(Date(uploadedAt))
    }

    //get comment of this fiction in reverse order
    fun getCommentList(fictionId: String) {
        detailUiState = detailUiState.copy(isCommentLoading = true)
        repository.getFictionComments(fictionId, onError = {}, onSuccess = { comments ->
            //reverse the list to get the latest comment first

            val finalUserList = mutableListOf<UserModel>()
            comments?.forEach { comment ->
                val user = userList.find { it.userId == comment.userId }
                if (user != null) {
                    finalUserList.add(user)
                }
            }
            detailUiState = detailUiState.copy(commentList = comments!!.reversed())
            detailUiState = detailUiState.copy(commentUserList = finalUserList.reversed())
            detailUiState = detailUiState.copy(isCommentLoading = false)
        })
    }

    //try catch validate the comment field
    fun validateCommentField(): Boolean {
        return detailUiState.comment.isNotEmpty()
    }

    fun validateCommentSize(comment: String): Boolean {
        return comment.length <= 100
    }

    //like this fiction
    fun likeFiction(fictionId: String) {
        repository.likeFiction(fictionId, onComplete = { isSuccessful, likeCount ->
            if (isSuccessful) {
                detailUiState = detailUiState.copy(likeCount = likeCount)
                getFictionStats(fictionId)
            }
        })
    }


    //load fiction stats


    //add comment to this fiction
    fun addComment(fictionId: String, userId: String) {

        try {
            if (!validateCommentField()) {
                throw IllegalArgumentException("Fields must not be empty.")
            }
            if (!validateCommentSize(detailUiState.comment)) {
                throw IllegalArgumentException("Comment must not exceed 100 characters.")
            }
            detailUiState = detailUiState.copy(isCommentLoading = true)

            detailUiState = detailUiState.copy(commentFieldError = "")
            repository.commentOnFiction(fictionId, detailUiState.comment) {
                if (it) {
                    detailUiState = detailUiState.copy(comment = "")
                    getCommentList(fictionId)
                }
            }

        } catch (e: Exception) {
            detailUiState = detailUiState.copy(commentFieldError = e.localizedMessage as String)
        } finally {
            detailUiState = detailUiState.copy(isCommentLoading = false)
        }
    }

    //like count


    //reset the user list once the comment list is fetched
    fun getCommentUserInfo() {
        // Get info of all the users who commented (don't get a user info more than once)
        // Get user info of all the users who commented
        //for each user id, call the suspend function to get the user info


    }


    fun getFiction(fictionId: String) {
        repository.getFiction(
            fictionId, onError = {},
            onSuccess = { fiction ->
                detailUiState = detailUiState.copy(title = fiction!!.title)
                detailUiState = detailUiState.copy(description = fiction.description)
                detailUiState = detailUiState.copy(imageUrl = fiction.coverLink)
                detailUiState = detailUiState.copy(uploadedAt = fiction.uploadedAt)
                detailUiState = detailUiState.copy(uploadedAt = fiction.uploadedAt)
                detailUiState = detailUiState.copy(uploaderId = fiction.uploaderId)
                repository.getUploaderInfo(fiction.uploaderId, onError = {
                    detailUiState = detailUiState.copy(uploader = "Unknown")
                }, onSuccess = {
                    detailUiState = detailUiState.copy(uploader = it!!.userName)
                })
            },
        )
    }

    //get fiction stats
    fun getFictionStats(fictionId: String) {
        repository.getFictionStats(fictionId, onError = {}, onSuccess = { stats ->
            if (stats != null) {

                detailUiState = detailUiState.copy(
                    likeCount = stats.likedBy.size,
                    dislikeCount = stats.dislikedBy.size
                )
                if (stats.likedBy.contains(userId)) {
                    detailUiState = detailUiState.copy(doesUserLike = true)
                } else {
                    detailUiState = detailUiState.copy(doesUserLike = false)
                }
                if (stats.dislikedBy.contains(userId)) {
                    detailUiState = detailUiState.copy(doesUserDislike = true)
                } else {
                    detailUiState = detailUiState.copy(doesUserDislike = false)
                }
            } else {
                detailUiState = detailUiState.copy(
                    likeCount = 0,
                    dislikeCount = 0,
                    doesUserDislike = false,
                    doesUserLike = false
                )
            }
        })
    }

    fun getChapterList(fictionId: String) =
        repository.getChapters(fictionId, onError = {}, onSuccess = { chapters ->
            detailUiState = detailUiState.copy(chapters = chapters!!)
        })

    fun onCommentChanged(comment: String) {
        detailUiState = detailUiState.copy(comment = comment)
        detailUiState = detailUiState.copy(commentFieldError = "")
    }

    fun dislikeFiction(fictionId: String) {
        repository.dislikeFiction(fictionId, onComplete = { isSuccessful, dislikeCount ->
            if (isSuccessful) {
                detailUiState = detailUiState.copy(dislikeCount = dislikeCount)
                getFictionStats(fictionId)
            }
        })
    }
}


data class DetailUiState(
    val title: String = "",
    val description: String = "",
    val uploaderId: String = "",
    val imageUrl: String = "",
    val uploadedAt: Long = 0,
    val uploader: String = "",
    val chapters: List<ChapterModel> = emptyList(),
    val isLoading: Boolean = false,
    val likeCount: Int? = 0,
    val dislikeCount: Int? = 0,

    val doesUserLike: Boolean = false,
    val doesUserDislike: Boolean = false,


    // comment
    val comment: String = "",
    val commentFieldError: String = "",
    val commentList: List<FictionCommentModel> = emptyList(),
    val isCommentLoading: Boolean = false,
    val commentUserList: List<UserModel> = emptyList(),
    val fetchedUserList: List<UserModel> = emptyList()

)

