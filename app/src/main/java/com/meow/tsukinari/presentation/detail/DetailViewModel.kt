package com.meow.tsukinari.presentation.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.meow.tsukinari.repository.DatabaseRepository

class DetailViewModel(
    private val repository: DatabaseRepository = DatabaseRepository(),
) : ViewModel() {


    var detailUiState by mutableStateOf(DetailUiState())
        private set

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
}


data class DetailUiState(
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val uploadedAt: Long = 0,
    val uploader: String = "",
    val isLoading: Boolean = false
)

