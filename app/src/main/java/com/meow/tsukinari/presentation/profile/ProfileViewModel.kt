package com.meow.tsukinari.presentation.profile

import android.content.Context
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

    //validate displayName field
    fun validateDisplayName(): Boolean {
        val newDisplayName = profileUiState.newDisplayName
        return newDisplayName!!.isNotBlank() && newDisplayName.length <= 20
    }

    //validate aboutMe field
    fun validateAboutMe(): Boolean {
        val newAboutMe = profileUiState.newAboutMe
        return newAboutMe!!.isNotBlank()
    }


    //update display name
    fun updateDisplayName() {
        try {
            if (!validateDisplayName()) {
                throw IllegalArgumentException("Invalid display name")
            }
            profileUiState = profileUiState.copy(isLoading = true)
            repository.updateDisplayName(profileUiState.newDisplayName!!) {
                profileUiState = profileUiState.copy(
                    isLoading = false,
                    isDisplayNameUpdated = true
                )
                //reset state
                profileUiState = profileUiState.copy(
                    isDisplayNameEditing = false
                )
                getUserProfile()
            }
        } catch (e: Exception) {
            profileUiState = profileUiState.copy(fieldError = e.message)
        }
    }

    //update about me
    fun updateAboutMe() {
        try {
            if (!validateAboutMe()) {
                throw IllegalArgumentException("Invalid about me")
            }
            profileUiState = profileUiState.copy(isLoading = true)
            repository.updateAboutMe(profileUiState.newAboutMe!!) {
                //reset state

                profileUiState = profileUiState.copy(
                    isLoading = false,
                    isAboutMeUpdated = true
                )

                profileUiState = profileUiState.copy(
                    isAboutMeEditing = false
                )
                getUserProfile()
            }
        } catch (e: Exception) {
            profileUiState = profileUiState.copy(fieldError = e.message)
        }
    }

    //update profile pic
    fun updateProfilePic(context: Context) {
        try {
            profileUiState = profileUiState.copy(isImageUploadLoading = true)
            repository.updateProfilePic(context, profileUiState.newProfilePicUri!!) {
                //reset state
                profileUiState = profileUiState.copy(
                    newProfilePicUri = Uri.EMPTY,
                    isProfilePicUpdated = true
                )
                profileUiState = profileUiState.copy(isImageUploadLoading = false)
                getUserProfile()
            }
        } catch (e: Exception) {
            profileUiState = profileUiState.copy(fieldError = e.message)
        }
    }


    fun onNewAboutMeChanged(aboutMe: String) {
        profileUiState = profileUiState.copy(newAboutMe = aboutMe)
    }

    fun onNewDisplayNameChanged(displayName: String) {
        profileUiState = profileUiState.copy(newDisplayName = displayName)
    }

    fun changeDisplayNameEditingState() {
        profileUiState = profileUiState.copy(isAboutMeEditing = false)
        //reset error
        profileUiState = profileUiState.copy(fieldError = "")
        profileUiState = profileUiState.copy(newDisplayName = profileUiState.displayName)
        profileUiState =
            profileUiState.copy(isDisplayNameEditing = !profileUiState.isDisplayNameEditing)
    }

    fun changeAboutMeEditingState() {
        profileUiState = profileUiState.copy(isDisplayNameEditing = false)

        profileUiState = profileUiState.copy(fieldError = "")
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

    fun onProfilePicChanged(uri: Uri?) {
        profileUiState = profileUiState.copy(newProfilePicUri = uri)
    }

    fun resetUpdatedState() {
        profileUiState = profileUiState.copy(
            isProfilePicUpdated = false,
            isDisplayNameUpdated = false,
            isAboutMeUpdated = false,
        )
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


    val fieldError: String? = null,

    //loading
    val isLoading: Boolean = false,
    val isImageUploadLoading: Boolean = false,


    //snackbar trigger
    val isProfilePicUpdated: Boolean = false,
    val isDisplayNameUpdated: Boolean = false,
    val isAboutMeUpdated: Boolean = false,
)

@Preview
@Composable
fun PreviewProfile() {
}


