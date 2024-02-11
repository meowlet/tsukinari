package com.meow.tsukinari.presentation.editor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.meow.tsukinari.repository.DatabaseRepository
import java.io.File
import java.io.FileOutputStream

class EditorViewModel(
    private val repository: DatabaseRepository = DatabaseRepository(),
) : ViewModel() {
    var editorUiState by mutableStateOf(EditorUiState())
        private set


    private val hasUser: Boolean
        get() = repository.hasUser()

    private val userId: String
        get() = repository.userId


    fun onTitleChange(title: String) {
        editorUiState = editorUiState.copy(title = title)
    }

    fun onImageChange(imageUri: Uri) {
        editorUiState = editorUiState.copy(imageUri = imageUri)
    }

    fun onDescriptionChange(description: String) {
        editorUiState = editorUiState.copy(description = description)
    }

    fun isFormFilled(): Boolean = editorUiState.title.isNotBlank() &&
            editorUiState.description.isNotBlank() && editorUiState.imageUri != Uri.EMPTY

    fun isUpdatingFormFilled(): Boolean = editorUiState.title.isNotBlank() &&
            editorUiState.description.isNotBlank()


    fun compressImage(imageUri: Uri, context: Context): Uri {
        val tempFile = File.createTempFile("temp", "jpg")
        val requestOptions = RequestOptions()
            .override(1080)
        Glide.with(context)
            .asBitmap()
            .load(imageUri)
            .apply(requestOptions)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    resource.compress(Bitmap.CompressFormat.JPEG, 80, FileOutputStream(tempFile))
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
        return Uri.fromFile(tempFile)
    }

    fun addFiction(context: Context) {
        if (hasUser) {
            editorUiState = editorUiState.copy(isLoading = true)
            repository.addFiction(
                uploaderId = userId,
                title = editorUiState.title,
                description = editorUiState.description,
                imageUri = compressImage(editorUiState.imageUri, context)
            ) {
                editorUiState = editorUiState.copy(fictionAddedStatus = it)
                editorUiState = editorUiState.copy(isLoading = false)
            }
        }
    }

    fun deleteFiction(fictionId: String) = repository.deleteFiction(fictionId) {
        editorUiState = editorUiState.copy(fictionDeletedStatus = it)
    }

    fun getFiction(fictionId: String) =
        repository.getFiction(fictionId, onError = {}, onSuccess = { fiction ->
            editorUiState = editorUiState.copy(title = fiction!!.title)
            editorUiState = editorUiState.copy(description = fiction.description)
            editorUiState = editorUiState.copy(imageUrl = fiction.coverLink)
        })


    fun updateFiction(
        context: Context,
        fictionId: String
    ) {
        editorUiState = editorUiState.copy(isLoading = true)
        repository.updateFiction(
            title = editorUiState.title,
            description = editorUiState.description,
            fictionId = fictionId,
            imageUri = if (editorUiState.imageUri == Uri.EMPTY) Uri.EMPTY else compressImage(
                editorUiState.imageUri,
                context
            )
        ) {
            editorUiState = editorUiState.copy(fictionUpdatedStatus = it)
            editorUiState = editorUiState.copy(isLoading = false)
        }
    }

    fun resetState() {
        editorUiState = EditorUiState()
    }

    fun resetImage() {
        editorUiState = editorUiState.copy(imageUri = Uri.EMPTY)
    }

    fun resetChangedStatus() {
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
    val imageUrl: String = "",
    val fictionAddedStatus: Boolean = false,
    val fictionUpdatedStatus: Boolean = false,
    val fictionDeletedStatus: Boolean = false,
    val isLoading: Boolean = false
)