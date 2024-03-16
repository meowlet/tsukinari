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

    fun getTime(uploadedAt: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
        return sdf.format(Date(uploadedAt))
    }

    //get comment of this fiction
    fun getCommentList(fictionId: String) {
        detailUiState = detailUiState.copy(isCommentLoading = true)
        repository.getFictionComments(fictionId, onError = {
            detailUiState = detailUiState.copy(isCommentLoading = false)
        }, onSuccess = {
            detailUiState = detailUiState.copy(commentList = it!!)
            detailUiState = detailUiState.copy(isCommentLoading = false)
        })
    }

    //add comment to this fiction
    fun addComment(fictionId: String, userId: String) {
        detailUiState = detailUiState.copy(isCommentLoading = true)
        repository.commentOnFiction(fictionId, detailUiState.comment) {
            detailUiState = detailUiState.copy(isCommentLoading = false)
            detailUiState = detailUiState.copy(isCommentLoading = false)
            detailUiState = detailUiState.copy(comment = "")
            getCommentList(fictionId)
        }
    }

    //get user info of this comment that return the UserModel list
    fun getCommentUserInfoList() {
        detailUiState.commentList.forEach { comment ->
            repository.getUserInfo(comment.userId, onError = {}, onSuccess = {
                detailUiState =
                    detailUiState.copy(commentUserList = detailUiState.commentUserList + it!!)
            })
        }
    }


    fun getFiction(fictionId: String) =
        repository.getFiction(fictionId, onError = {}, onSuccess = { fiction ->
            detailUiState = detailUiState.copy(title = fiction!!.title)
            detailUiState = detailUiState.copy(description = fiction.description)
            detailUiState = detailUiState.copy(imageUrl = fiction.coverLink)
            detailUiState = detailUiState.copy(uploadedAt = fiction.uploadedAt)
            detailUiState = detailUiState.copy(uploadedAt = fiction.uploadedAt)
            repository.getUploaderInfo(fiction.uploaderId, onError = {
                detailUiState = detailUiState.copy(uploader = "Unknown")
            }, onSuccess = {
                detailUiState = detailUiState.copy(uploader = it!!.userName)
            })
        })

    fun getChapterList(fictionId: String) =
        repository.getChapters(fictionId, onError = {}, onSuccess = { chapters ->
            detailUiState = detailUiState.copy(chapters = chapters!!)
        })

    fun onCommentChanged(comment: String) {
        detailUiState = detailUiState.copy(comment = comment)
    }
}


data class DetailUiState(
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val uploadedAt: Long = 0,
    val uploader: String = "",
    val chapters: List<ChapterModel> = emptyList(),
    val isLoading: Boolean = false,


    // comment
    val comment: String = "",
    val commentList: List<FictionCommentModel> = emptyList(),
    val isCommentLoading: Boolean = false,
    val commentUserList: List<UserModel> = emptyList()

)

