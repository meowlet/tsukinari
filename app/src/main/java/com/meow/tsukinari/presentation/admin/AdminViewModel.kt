package com.meow.tsukinari.presentation.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meow.tsukinari.model.AllStatsModel
import com.meow.tsukinari.model.FictionModel
import com.meow.tsukinari.model.UserModel
import com.meow.tsukinari.repository.Resources
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminViewModel(
    private val repository: AdminRepository = AdminRepository()
) : ViewModel() {
    //is admin
    val isAdmin: Boolean
        get() = repository.isAdmin()
    var adminUiState by mutableStateOf(AdminUiState())
        private set

    //get user list
    fun getUserList() = viewModelScope.launch {
        adminUiState = adminUiState.copy(userList = repository.getAllUsers())
    }

    //unlock the admin page
    fun unlockAdmin() {
        adminUiState = adminUiState.copy(isLocked = false)
    }

    //lock the admin page
    fun lockAdmin() {
        adminUiState = adminUiState.copy(isLocked = true)
    }

    fun getTime(createdAt: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
        return sdf.format(Date(createdAt))
    }

    //get all pending (unverified) fictions
    fun getPendingFictions() = viewModelScope.launch {
        adminUiState = adminUiState.copy(pendingFictions = repository.getAllUnverifiedFictions())
    }

    fun changeTab(tabIndex: Int) {
        adminUiState = adminUiState.copy(currentTab = tabIndex)
    }

    //count the number of pending fictions
    fun countPendingFictions() = viewModelScope.launch {
        adminUiState =
            adminUiState.copy(pendingFictionsCount = adminUiState.pendingFictions.data?.size ?: 0)
    }

    fun verifyFiction(fictionId: String) = viewModelScope.launch {
        repository.verifyFiction(
            fictionId,
            onError = {
            },
            onSuccess = {
                getPendingFictions()
            })

    }

    //get full stats of everything
    fun getFullStats() = viewModelScope.launch {
        adminUiState = adminUiState.copy(stats = repository.getFullStats())
    }
}

data class AdminUiState(
    val isLocked: Boolean = true,
    val searchValue: String = "",
    val userList: Resources<List<UserModel>> = Resources.Loading(),
    val pendingFictions: Resources<List<FictionModel>> = Resources.Loading(),
    val pendingFictionsCount: Int = 0,
    val currentTab: Int = 0, //0 for user list, 1 for pending fictions

    val stats: Resources<AllStatsModel> = Resources.Loading(),
)