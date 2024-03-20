package com.meow.tsukinari.presentation.reader

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.meow.tsukinari.model.ChapterModel
import com.meow.tsukinari.repository.DatabaseRepository

//reader view model
class ReaderViewModel(
    val repository: DatabaseRepository = DatabaseRepository()
) : ViewModel() {

    var readerUiState by mutableStateOf(ReaderUiState())
        private set

    //hasUser val
    val hasUser: Boolean
        get() = repository.hasUser()

    val userId: String
        get() = repository.getUserId()

    fun onToolbarVisibilityChanged() {
        readerUiState = readerUiState.copy(isToolbarVisible = !readerUiState.isToolbarVisible)
    }

    //add current user to the chapter's view list
    fun addView(fictionId: String, chapterId: String) {
        repository.addViewToChapter(fictionId, chapterId) {
            //do nothing
        }
    }

    //fun to fetch the chapter
    fun fetchChapter(chapterId: String) {
        //clear current chapter data
        readerUiState = readerUiState.copy(
            chapterImages = emptyList(),
        )
        repository.getChapterData(chapterId) {
            onChapterChanged(
                it.chapterId,
                it.chapterTitle,
                it.chapterPages,
                it.chapterNumber,
                it.fictionId
            )
            fetchChapterList(it.fictionId, it.chapterNumber)
        }
    }

    fun fetchChapterList(fictionId: String, chaperIndex: Int) {
        repository.getChapters(
            fictionId,
            onError = {},
        ) { it ->
            //get the next (the only first most close ChapterModel.index above the current index) and previous chapter of the current chapter
            val nextChapter =
                it?.sortedBy { it.chapterNumber }?.find { it.chapterNumber > chaperIndex }
            readerUiState = readerUiState.copy(nextChapter = nextChapter)

            //find in reverse order to get the previous chapter, catch the first most close ChapterModel.index below the current index
            val previousChapter =
                it?.sortedBy { it.chapterNumber }?.findLast { it.chapterNumber < chaperIndex }
            readerUiState = readerUiState.copy(previousChapter = previousChapter)
        }
    }

    //hide the toolbar
    fun hideToolbar() {
        readerUiState = readerUiState.copy(isToolbarVisible = false)
    }

    //next chapter
    fun nextChapter() {
        readerUiState.nextChapter?.let {
            fetchChapter(it.chapterId)
        }
    }

    //previous chapter
    fun previousChapter() {
        readerUiState.previousChapter?.let {
            fetchChapter(it.chapterId)
        }
    }

    fun onChapterChanged(
        chapterId: String,
        chapterTitle: String,
        chapterImages: List<String>,
        chapterIndex: Int,
        fictionId: String
    ) {
        readerUiState = readerUiState.copy(
            fictionId = fictionId,
            chapterIndex = chapterIndex,
            chapterId = chapterId,
            chapterTitle = chapterTitle,
            chapterImages = chapterImages,
            currentPage = 0,
            totalPages = chapterImages.size
        )
        addView(fictionId, chapterId)
    }


    val bottomBarItems = listOf(
        BottomBarItem.PreviousChapter,
        BottomBarItem.NextChapter,
        BottomBarItem.Settings
    )

    fun onContextMenuVisibilityChanged() {
        readerUiState = readerUiState.copy(contextMenuVisible = !readerUiState.contextMenuVisible)
    }

    fun verticalReaderToggle() {
        readerUiState = readerUiState.copy(verticalReader = !readerUiState.verticalReader)
    }

}

data class ReaderUiState(
    val chapterId: String = "",
    val fictionId: String = "",
    val chapterIndex: Int = 0,
    val chapterTitle: String = "",
    val chapterImages: List<String> = emptyList(),
    val chapterList: List<ChapterModel> = emptyList(),
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val isToolbarVisible: Boolean = false,
    val verticalReader: Boolean = false,
    val viewCount: Int = 0,

    val contextMenuVisible: Boolean = false,


    val currentChapter: ChapterModel? = null,
    val nextChapter: ChapterModel? = null,
    val previousChapter: ChapterModel? = null,


    //image properties
    val targetScale: Float = 3.0f,


    )

// bottom bar items (with icons)
sealed class BottomBarItem(val title: String, val icon: Int) {
    object PreviousChapter : BottomBarItem("Previous", 0)
    object NextChapter : BottomBarItem("Next", 0)
    object Settings : BottomBarItem("Settings", 0)
}

