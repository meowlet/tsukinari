package com.meow.tsukinari.presentation.editor.add_chapter

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meow.tsukinari.repository.DatabaseRepository
import kotlinx.coroutines.launch

class AddChapterViewModel(val repository: DatabaseRepository = DatabaseRepository()) : ViewModel() {

    var addChapterUiState by mutableStateOf(AddChapterUiState())
        private set

    var selectedImages = mutableStateListOf<Uri>()

    fun onChapterTitleChange(chapterTitle: String) {
        addChapterUiState = addChapterUiState.copy(chapterTitle = chapterTitle)
    }

    fun onImageSelected(chapterPages: List<Uri>, context: Context) {
        selectedImages.apply {
            clear()
            addAll(chapterPages)
        }
    }


    //upload chapter images to storage
    fun uploadChapter(context: Context, fictionId: String) = viewModelScope.launch {

        try {
            if (!validateForm()) {
                throw IllegalArgumentException("Please fill in all fields")
            }

            if (!validateTitle()) {
                throw IllegalArgumentException("Title must be filled")
            }

            if (!validateIndex()) {
                throw IllegalArgumentException("Please provide a valid index")
            }

            addChapterUiState = addChapterUiState.copy(isLoading = true)
            //clear the error
            addChapterUiState = addChapterUiState.copy(addingChapterError = "")

            repository.addChapter(
                context,
                fictionId,
                addChapterUiState.chapterIndex.toInt(),
                addChapterUiState.chapterTitle,
                selectedImages,
            ) { success ->
                //clear the selected images
                // Update isLoading state based on the success of addChapter
                addChapterUiState = addChapterUiState.copy(isLoading = !success)
                // Update the uploadedChapterStatus based on the success of addChapter
                addChapterUiState = addChapterUiState.copy(uploadedChapterStatus = success)
            }
        } catch (e: Exception) {
            addChapterUiState = addChapterUiState.copy(addingChapterError = e.localizedMessage)
            e.printStackTrace()
        }
    }


    //validate title field
    fun validateTitle(): Boolean {
        return addChapterUiState.chapterTitle.isNotEmpty()
    }

    //validate index field (not empty and is a number)
    fun validateIndex(): Boolean {
        return addChapterUiState.chapterIndex.isNotEmpty() && addChapterUiState.chapterIndex.toIntOrNull() != null
    }

    // validate the adding chapter form
    fun validateForm(): Boolean {
        return validateTitle() && addChapterUiState.chapterIndex.isNotEmpty() && selectedImages.isNotEmpty()
    }

    fun onChapterIndexChange(index: String) {
        addChapterUiState = addChapterUiState.copy(chapterIndex = index)
    }

    fun clearForm() {
        selectedImages.clear()
        addChapterUiState = addChapterUiState.copy(
            chapterTitle = "",
            chapterIndex = "",
            addingChapterError = ""
        )
    }

    fun resetSnackbar() {
        addChapterUiState = addChapterUiState.copy(uploadedChapterStatus = false)
    }

    fun clearAllImages() {
        selectedImages.clear()
    }
}

data class AddChapterUiState(
    val chapterTitle: String = "",
    val chapterIndex: String = "",
    val addingChapterError: String = "",
    val uploadedChapterStatus: Boolean = false,
    val isLoading: Boolean = false,
)