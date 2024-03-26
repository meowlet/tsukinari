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
import com.meow.tsukinari.model.ChapterModel
import com.meow.tsukinari.repository.DatabaseRepository
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditorViewModel(
    private val repository: DatabaseRepository = DatabaseRepository(),
) : ViewModel() {
    var editorUiState by mutableStateOf(EditorUiState())
        private set



    private val hasUser: Boolean
        get() = repository.hasUser()

    private val userId: String
        get() = repository.getUserId()


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

    //get chapter list of the fiction
    fun getChapterList(fictionId: String) =
        repository.getChapters(fictionId, onError = {}, onSuccess = { chapters ->
            editorUiState = editorUiState.copy(
                chapters = chapters!!.sortedBy { it.chapterNumber }
            )
        })


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
            editorUiState = editorUiState.copy(isFinished = fiction.finished)
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
            ),
            isFinished = editorUiState.isFinished
        ) {
            editorUiState = editorUiState.copy(fictionUpdatedStatus = it)
            editorUiState = editorUiState.copy(isLoading = false)
        }
    }

    fun resetState() {
        editorUiState = EditorUiState()
    }

    fun onFinishedChange(isFinished: Boolean) {
        editorUiState = editorUiState.copy(isFinished = isFinished)
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

    fun getTime(uploadedAt: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
        return sdf.format(Date(uploadedAt))
    }

    fun showDialog() {
        editorUiState = editorUiState.copy(confirmDeleteDialog = true)
    }

    fun hideDialog() {
        editorUiState = editorUiState.copy(confirmDeleteDialog = false)
    }

}


data class EditorUiState(
    val title: String = "",
    val description: String = "",
    val imageUri: Uri = Uri.EMPTY,
    val imageUrl: String = "",
    val isFinished: Boolean = false,
    val fictionAddedStatus: Boolean = false,
    val fictionUpdatedStatus: Boolean = false,
    val fictionDeletedStatus: Boolean = false,
    val isLoading: Boolean = false,

    val chapters: List<ChapterModel> = emptyList(),
    val confirmDeleteDialog: Boolean = false,
)