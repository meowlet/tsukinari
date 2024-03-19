package com.meow.tsukinari.presentation.user_profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.meow.tsukinari.repository.DatabaseRepository

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

}

data class UserProfileUiState(
    val isLoading: Boolean = false,
    val displayName: String? = "",
    val aboutMe: String? = "",
    val profileImageUrl: String? = "",
    val userName: String? = "",
)