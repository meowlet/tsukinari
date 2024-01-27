package com.meow.tsukinari.presentation.editor

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

    fun onDescriptionChange(description: String) {
        editorUiState = editorUiState.copy(description = description)
    }

    fun addNote() {
        if (hasUser) {
            repository.addFiction(
                uploaderId = user!!.uid,
                title = editorUiState.title,
                description = editorUiState.description,
//                timestamp = Timestamp.now(),
                coverLink = "Cover URL"
            ) {
                editorUiState = editorUiState.copy(fictionAddedStatus = it)
            }
        }
    }

    fun resetState() {
        editorUiState = EditorUiState()
    }

}


data class EditorUiState(
    val title: String = "",
    val description: String = "",
    val fictionAddedStatus: Boolean = false,
)