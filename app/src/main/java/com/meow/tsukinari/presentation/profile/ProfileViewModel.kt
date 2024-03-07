package com.meow.tsukinari.presentation.profile

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import com.meow.tsukinari.repository.DatabaseRepository

class ProfileViewModel(
    private val repository: DatabaseRepository = DatabaseRepository(),
) : ViewModel() {


    val hasUser: Boolean
        get() = repository.hasUser()

    val userId: String
        get() = repository.getUserId()

    var profileUiState by mutableStateOf(ProfileUiState())
        private set


    fun onUsernameChange(username: String) {
        profileUiState = profileUiState.copy(usernameSetup = username)
    }

    fun onDiplayNameChange(name: String) {
        profileUiState = profileUiState.copy(displayNameSetup = name)
    }

    fun getUserProfile() {
        if (hasUser) {
            repository.getUserInfo(userId, {
            }) {
                profileUiState = profileUiState.copy(
                    username = it?.username,
                    displayName = it?.displayName,
                    createdAt = it?.createdAt,
                    profilePicUrl = it?.profileImageUrl,
                    email = it?.email,
                )
            }
        } else {
            profileUiState = profileUiState.copy(
                username = "username",
                displayName = "Display Name",
                email = "email",
            )
        }
    }
}

data class ProfileUiState(
    // setting up
    val usernameSetup: String = "",
    val displayNameSetup: String = "",
    val profilePicUriSetup: Uri = Uri.EMPTY,

    //display
    val email: String? = "",
    val uid: String? = "",
    val username: String? = "username",
    val isVerified: Boolean? = false,
    val displayName: String? = "Display Name",
    val profilePicUri: Uri? = Uri.EMPTY,
    val profilePicUrl: String? = "",
    val follower: Int? = 0,
    val following: Int? = 0,
    val createdAt: Long? = 0
)

@Preview
@Composable
fun PreviewProfile() {
}


