package com.meow.tsukinari.presentation.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.meow.tsukinari.model.ChapterModel
import com.meow.tsukinari.model.FictionCommentModel
import com.meow.tsukinari.model.UserModel
import com.meow.tsukinari.repository.DatabaseRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
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


    private suspend fun getCommentUserInfo(): List<UserModel> {
        val userIdList = detailUiState.commentList.map { it.userId }.distinct()
        val userList = mutableListOf<UserModel>()

        val deferreds = userIdList.map { userId ->
            GlobalScope.async {
                repository.getUserInfo(userId, onError = {}, onSuccess = {
                    userList.add(it!!)
                })
            }
        }

        deferreds.awaitAll()

        return userList
    }


    fun getCommentList(fictionId: String) {
        detailUiState = detailUiState.copy(isCommentLoading = true)
        repository.getFictionComments(fictionId, onError = {}, onSuccess = { comments ->
            //reverse the list to get the latest comment first
            detailUiState = detailUiState.copy(commentList = comments!!.reversed())

            GlobalScope.launch {
                val fetchedUserList = getCommentUserInfo()

                detailUiState = detailUiState.copy(fetchedUserList = fetchedUserList)

                val userList = mutableListOf<UserModel>()
                detailUiState.commentList.forEach { comment ->
                    val user = fetchedUserList.find { it.userId == comment.userId }
                    user?.let { userList.add(it) }
                }
                detailUiState =
                    detailUiState.copy(commentUserList = userList, isCommentLoading = false)
            }
        })
    }

    //fetch the user info of the users who commented


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

    //reset the user list once the comment list is fetched


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
    val commentUserList: List<UserModel> = emptyList(),
    val fetchedUserList: List<UserModel> = emptyList()

)

