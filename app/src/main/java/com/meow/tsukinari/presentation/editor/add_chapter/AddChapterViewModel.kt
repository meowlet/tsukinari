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

            //clear the error
            addChapterUiState = addChapterUiState.copy(addingChapterError = "")

            addChapterUiState = addChapterUiState.copy(isLoading = true)

            repository.addChapter(
                context,
                fictionId,
                addChapterUiState.chapterIndex.toInt(),
                addChapterUiState.chapterTitle,
                selectedImages
            ) {
                //clear the selected images
                selectedImages.clear()
                addChapterUiState = addChapterUiState.copy(isLoading = false)
            }
        } catch (e: Exception) {
            addChapterUiState = addChapterUiState.copy(addingChapterError = e.localizedMessage)
            e.printStackTrace()
        } finally {
            addChapterUiState = addChapterUiState.copy(isLoading = false)
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

    fun clearSelectedImages() {
        selectedImages.clear()
    }
}

data class AddChapterUiState(
    val chapterTitle: String = "",
    val chapterIndex: String = "",
    val addingChapterError: String = "",
    val isLoading: Boolean = false,
)