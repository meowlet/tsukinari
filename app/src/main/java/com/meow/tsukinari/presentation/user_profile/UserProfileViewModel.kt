package com.meow.tsukinari.presentation.user_profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meow.tsukinari.model.FictionModel
import com.meow.tsukinari.repository.DatabaseRepository
import com.meow.tsukinari.repository.Resources
import kotlinx.coroutines.launch

//user profile view model
class UserProfileViewModel(
    private val repository: DatabaseRepository = DatabaseRepository(),
) : ViewModel() {

    //init states
    var userProfileUiState by mutableStateOf(UserProfileUiState())
        private set

    //get user id
    //get user profile
    fun getUserProfile(uid: String) {
        repository.getUserInfo(uid, {
        }) {
            userProfileUiState = userProfileUiState.copy(
                displayName = it?.displayName,
                aboutMe = it?.aboutMe,
                profileImageUrl = it?.profileImageUrl,
                userName = it?.userName
            )
        }
    }

    //get fictions of this user
    fun getUserFictions(userId: String) = viewModelScope.launch {
        repository.getUserFictions(userId).collect {
            userProfileUiState = userProfileUiState.copy(fictionsList = it)
        }
    }

}

data class UserProfileUiState(
    val isLoading: Boolean = false,
    val displayName: String? = "",
    val aboutMe: String? = "",
    val profileImageUrl: String? = "",
    val userName: String? = "",
    val follower: Int = 0,
    val following: Int = 0,
    val fictionsList: Resources<List<FictionModel>> = Resources.Loading(),
)