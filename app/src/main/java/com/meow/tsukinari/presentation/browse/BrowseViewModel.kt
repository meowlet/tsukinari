package com.meow.tsukinari.presentation.browse

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meow.tsukinari.model.FictionModel
import com.meow.tsukinari.repository.DatabaseRepository
import com.meow.tsukinari.repository.Resources
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask

class BrowseViewModel(
    private val repository: DatabaseRepository = DatabaseRepository()
) : ViewModel() {

    fun changeSearchingState() {
        browseUiState = browseUiState.copy(isSearching = !browseUiState.isSearching)
    }

    //search fictions function
    fun searchFictions(searchValue: String) = viewModelScope.launch {
        browseUiState = browseUiState.copy(fictionsList = repository.searchFictions(searchValue))
    }

    // on search value change, search for the fiction after not typing for 500ms
    fun onSearchValueChange(searchValue: String) {
        val newSearchValue = searchValue

        // Khởi tạo bộ đếm thời gian
        val timer = Timer()

        // Lên lịch nhiệm vụ tìm kiếm sau 1 giây
        timer.schedule(object : TimerTask() {
            override fun run() {
                // Kiểm tra nếu giá trị tìm kiếm không thay đổi
                if (newSearchValue == browseUiState.searchValue) {
                    // Thực hiện tìm kiếm
                    if (newSearchValue.isNotEmpty()) {
                        searchFictions(newSearchValue)
                    } else {
                        loadFictions()
                    }
                }
            }
        }, 500)

        // Cập nhật trạng thái UI với giá trị tìm kiếm mới
        browseUiState = browseUiState.copy(searchValue = newSearchValue)
    }

    fun clearSearchValue() {
        browseUiState = browseUiState.copy(searchValue = "")
    }

    fun signOut() = viewModelScope.launch {
        repository.signOut()
    }


    //ui state rememberSaveable for the whole page to not reload the data when the page is recomposed
    var browseUiState by mutableStateOf(BrowseUiState())
        private set


    //load fictions
    fun loadFictions() = viewModelScope.launch {
        browseUiState = browseUiState.copy(fictionsList = repository.getFictions())
    }

    //search fictions


}

data class BrowseUiState(
    val fictionsList: Resources<List<FictionModel>> = Resources.Loading(),
    val isSearching: Boolean = false,
    val searchValue: String = ""
)