package com.meow.tsukinari.presentation.reader

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.meow.tsukinari.repository.DatabaseRepository

//reader view model
class ReaderViewModel(
    val chapterId: String = "-NsM6wHSrJeAomYBfYSc",
    val repository: DatabaseRepository = DatabaseRepository()
) : ViewModel() {

    var readerUiState by mutableStateOf(ReaderUiState())
        private set

    fun onToolbarVisibilityChanged() {
        readerUiState = readerUiState.copy(isToolbarVisible = !readerUiState.isToolbarVisible)
    }

    //fun to fetch the chapter
    fun fetchChapter(fictionId: String) {
        repository.getChapterData(fictionId) {
            onChapterChanged(it.chapterId, it.chapterTitle, it.chapterPages)
        }
    }

    fun onChapterChanged(chapterId: String, chapterTitle: String, chapterImages: List<String>) {
        readerUiState = readerUiState.copy(
            chapterId = chapterId,
            chapterTitle = chapterTitle,
            chapterImages = chapterImages,
            currentPage = 0,
            totalPages = chapterImages.size
        )
    }

    val bottomBarItems = listOf(
        BottomBarItem.PreviousChapter,
        BottomBarItem.NextChapter,
        BottomBarItem.Settings
    )


}

data class ReaderUiState(
    val chapterId: String = "",
    val chapterTitle: String = "",
    val chapterImages: List<String> = emptyList(),
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val isToolbarVisible: Boolean = false,
)

// bottom bar items (with icons)
sealed class BottomBarItem(val title: String, val icon: Int) {
    object PreviousChapter : BottomBarItem("Previous", 0)
    object NextChapter : BottomBarItem("Next", 0)
    object Settings : BottomBarItem("Settings", 0)
}

