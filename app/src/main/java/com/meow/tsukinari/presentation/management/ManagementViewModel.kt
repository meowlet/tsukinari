package com.meow.tsukinari.presentation.management

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.meow.tsukinari.repository.DatabaseRepository

class ManagementViewModel(
    private val repository: DatabaseRepository = DatabaseRepository(),
) : ViewModel() {
    var managementUiState by mutableStateOf(ManagementUiState())
        private set

    private val hasUser: Boolean
        get() = repository.hasUser()

    private val user: FirebaseUser?
        get() = repository.user()


    fun onTitleChange(title: String) {
        managementUiState = managementUiState.copy(title = title)
    }

    fun onDescriptionChange(description: String) {
        managementUiState = managementUiState.copy(description = description)
    }

    fun addNote() {
        if (hasUser) {
            repository.addFiction(
                uploaderId = user!!.uid,
                title = managementUiState.title,
                description = managementUiState.description,
//                timestamp = Timestamp.now(),
                coverLink = "Cover URL"
            ) {
                managementUiState = managementUiState.copy(fictionAddedStatus = it)
            }
        }
    }

    fun resetState() {
        managementUiState = ManagementUiState()
    }

}


data class ManagementUiState(
    val title: String = "",
    val description: String = "",
    val fictionAddedStatus: Boolean = false,
)