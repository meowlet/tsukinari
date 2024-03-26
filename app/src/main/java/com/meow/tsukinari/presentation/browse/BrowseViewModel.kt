package com.meow.tsukinari.presentation.browse

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meow.tsukinari.model.FictionModel
import com.meow.tsukinari.model.UserModel
import com.meow.tsukinari.repository.DatabaseRepository
import com.meow.tsukinari.repository.Resources
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask

class BrowseViewModel(
    private val repository: DatabaseRepository = DatabaseRepository()
) : ViewModel() {

    var userList = emptyList<UserModel>()
    var browseUiState by mutableStateOf(BrowseUiState())
        private set

    fun getUserList() {
        repository.getAllUserInfo(onError = {}, onSuccess = { users ->
            userList = users!!
        })
    }

    //get total views of the fiction
    var viewList = mutableListOf<Pair<String, Int>>()
    var likeList = mutableListOf<Pair<String, Int>>()

    //suspend fun get total views of the list of fictions
    suspend fun getTotalViews() = viewModelScope.launch {
        viewList = mutableListOf()
        browseUiState.fictionsList.data?.forEach { fiction ->
            val totalViews = repository.getTotalViews(fiction.fictionId)
            viewList.add(Pair(fiction.fictionId, totalViews))
        }
    }

    //suspend fun get total likes of the list of fictions
    suspend fun getTotalLikes() = viewModelScope.launch {
        likeList = mutableListOf()
        browseUiState.fictionsList.data?.forEach { fiction ->
            val totalLikes = repository.getTotalLikes(fiction.fictionId)
            likeList.add(Pair(fiction.fictionId, totalLikes))
        }
    }

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
        searchFictions("")
    }

    fun signOut(context: Context) = viewModelScope.launch {
        repository.signOut()
        Toast.makeText(context, "Successfully signed out", Toast.LENGTH_SHORT).show()
    }


    //ui state rememberSaveable for the whole page to not reload the data when the page is recomposed


    //load fictions
    fun loadFictions() = viewModelScope.launch {
        browseUiState = browseUiState.copy(fictionsList = repository.getFictions())
        stopRefreshing()
    }

    fun hideBottomSheet() {
        browseUiState = browseUiState.copy(showBottomSheet = false)
    }

    fun showBottomSheet() {
        browseUiState = browseUiState.copy(showBottomSheet = true)
    }

    fun changeTab(tabIndex: Int) {
        browseUiState = browseUiState.copy(selectedTab = tabIndex)
    }

    fun changeSortBy(option: Int) {
        browseUiState = browseUiState.copy(sortBy = option)

    }

    fun changeFilterBy(option: Int) {
        //if the option parameter is 1, show warning dialog
        browseUiState = if (option == 1) {
            browseUiState.copy(showUnverifiedWarningDialog = true, filterBy = option)
        } else {
            browseUiState.copy(filterBy = option)
        }
    }

    //stop refreshing
    fun stopRefreshing() {
        browseUiState = browseUiState.copy(isRefreshing = false)
    }

    fun resetSortAndFilter() {
        browseUiState = browseUiState.copy(sortBy = 0, filterBy = 1)
    }

    fun startRefreshing() {
        browseUiState = browseUiState.copy(isRefreshing = true)
    }


    //show sign out dialog
    fun showSignOutDialog() {
        browseUiState = browseUiState.copy(showSignOutDialog = true)
    }

    //hide sign out dialog
    fun hideSignOutDialog() {
        browseUiState = browseUiState.copy(showSignOutDialog = false)
    }

    fun hideWarningDialog() {
        browseUiState = browseUiState.copy(showUnverifiedWarningDialog = false)
    }


}

data class BrowseUiState(
    val fictionsList: Resources<List<FictionModel>> = Resources.Loading(),
    val isSearching: Boolean = false,
    val searchValue: String = "",
    val showBottomSheet: Boolean = false,
    val selectedTab: Int = 0,
    val isRefreshing: Boolean = false,
    //sort by 0 is by date, 1 is by name, 2 is by author, 3 is by views, 4 is by likes
    val sortBy: Int = 0,
    val filterBy: Int = 0,
    val userList: List<UserModel>? = emptyList(),

    //signing out confirmation dialog
    val showSignOutDialog: Boolean = false,
    val showUnverifiedWarningDialog: Boolean = false
)