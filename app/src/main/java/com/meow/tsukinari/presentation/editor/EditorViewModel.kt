package com.meow.tsukinari.presentation.editor

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.meow.tsukinari.repository.DatabaseRepository

class EditorViewModel(
    private val repository: DatabaseRepository = DatabaseRepository(),
) : ViewModel() {
    var editorUiState by mutableStateOf(EditorUiState())
        private set


    private val hasUser: Boolean
        get() = repository.hasUser()

    private val user: FirebaseUser?
        get() = repository.user()


    fun onTitleChange(title: String) {
        editorUiState = editorUiState.copy(title = title)
    }

    fun onImageChange(imageUri: Uri) {
        editorUiState = editorUiState.copy(imageUri = imageUri)
    }

    fun onDescriptionChange(description: String) {
        editorUiState = editorUiState.copy(description = description)
    }

    fun addNote() {
        if (hasUser) {
            editorUiState = editorUiState.copy(isLoading = true)
            repository.addFiction(
                uploaderId = user!!.uid,
                title = editorUiState.title,
                description = editorUiState.description,
//                timestamp = Timestamp.now(),
                imageUri = editorUiState.imageUri
            ) {
                editorUiState = editorUiState.copy(fictionAddedStatus = it)
                editorUiState = editorUiState.copy(isLoading = false)
            }
        }
    }

    fun deleteFiction(fictionId: String) = repository.deleteFiction(fictionId) {
        editorUiState = editorUiState.copy(fictionDeletedStatus = it)
    }

    fun updateFiction(
        fictionId: String
    ) {
        repository.updateFiction(
            title = editorUiState.title,
            description = editorUiState.description,
            fictionId = fictionId,
            imageUri = editorUiState.imageUri
        ) {
            editorUiState = editorUiState.copy(fictionUpdatedStatus = it)
        }
    }

    fun resetState() {
        editorUiState = EditorUiState()
    }

    fun resetImage() {
        editorUiState = editorUiState.copy(imageUri = Uri.EMPTY)
    }

    fun resetNoteChangedStatus() {
        editorUiState = editorUiState.copy(
            fictionAddedStatus = false,
            fictionUpdatedStatus = false,
            fictionDeletedStatus = false,
        )
    }

}


data class EditorUiState(
    val title: String = "",
    val description: String = "",
    val imageUri: Uri = Uri.EMPTY,
    val fictionAddedStatus: Boolean = false,
    val fictionUpdatedStatus: Boolean = false,
    val fictionDeletedStatus: Boolean = false,
    val isLoading: Boolean = false
)