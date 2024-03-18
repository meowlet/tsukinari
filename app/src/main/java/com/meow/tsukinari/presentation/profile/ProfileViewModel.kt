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


    //update profile
    fun updateProfile() {
        val newDisplayName = profileUiState.newDisplayName
        val newProfilePicUri = profileUiState.newProfilePicUri
        val newAboutMe = profileUiState.newAboutMe

        repository.updateProfile(
            newDisplayName!!,
            newAboutMe!!,
            userId,
            newProfilePicUri!!
        )
        {
            getUserProfile()
        }

    }

    fun onNewProfilePicSelected(uri: Uri) {
        profileUiState = profileUiState.copy(newProfilePicUri = uri)
    }

    fun onNewAboutMeChanged(aboutMe: String) {
        profileUiState = profileUiState.copy(newAboutMe = aboutMe)
    }

    fun onNewDisplayNameChanged(displayName: String) {
        profileUiState = profileUiState.copy(newDisplayName = displayName)
    }

    fun changeDisplayNameEditingState() {
        profileUiState = profileUiState.copy(newDisplayName = profileUiState.displayName)
        profileUiState =
            profileUiState.copy(isDisplayNameEditing = !profileUiState.isDisplayNameEditing)
    }

    fun changeAboutMeEditingState() {
        profileUiState = profileUiState.copy(newAboutMe = profileUiState.aboutMe)
        profileUiState = profileUiState.copy(isAboutMeEditing = !profileUiState.isAboutMeEditing)
    }


    fun getUserProfile() {
        if (hasUser) {
            repository.getUserInfo(userId, {
            }) {
                profileUiState = profileUiState.copy(
                    isDisplayNameEditing = false,
                    isAboutMeEditing = false,
                    username = it?.userName,
                    displayName = it?.displayName,
                    createdAt = it?.createdAt,
                    profilePicUrl = it?.profileImageUrl,
                    email = it?.email,
                    aboutMe = it?.aboutMe,
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

    //display
    val email: String? = "",
    val uid: String? = "",
    val username: String? = "username",
    val isVerified: Boolean? = false,
    val displayName: String? = "Display Name",
    val profilePicUri: Uri? = Uri.EMPTY,
    val profilePicUrl: String? = "",
    val aboutMe: String? = "",
    val follower: Int? = 0,
    val following: Int? = 0,
    val createdAt: Long? = 0,

    val isDisplayNameEditing: Boolean = false,
    val isAboutMeEditing: Boolean = false,
    val isProfilePicEditing: Boolean = false,

    val newProfilePicUri: Uri? = Uri.EMPTY,
    val newDisplayName: String? = "",
    val newAboutMe: String? = "",
)

@Preview
@Composable
fun PreviewProfile() {
}


