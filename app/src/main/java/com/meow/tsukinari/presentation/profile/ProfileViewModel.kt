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


    val user = repository.user

    var profileUiState by mutableStateOf(ProfileUiState())
        private set

    fun isSetup() {
        if (repository.isSetup(user!!.uid)) {
            println("vllll")
            profileUiState = profileUiState.copy(isSetup = true)
        } else {
            println("vllllccc")
            profileUiState = profileUiState.copy(isSetup = false)
        }
    }

    fun getUserProfile() {
        if (repository.hasUser()) {
            profileUiState = profileUiState.copy(hasUser = true)
            profileUiState = profileUiState.copy(displayName = user?.displayName)
            profileUiState = profileUiState.copy(email = user?.email)
            profileUiState = profileUiState.copy(profilePicUri = user?.photoUrl)
            profileUiState = profileUiState.copy(isVerified = user?.isEmailVerified)
            profileUiState = profileUiState.copy(uid = user?.uid)
        } else {
            profileUiState = profileUiState.copy(hasUser = false)
        }
    }


}

data class ProfileUiState(

    val isSetup: Boolean = false,
    val hasUser: Boolean = false,
    val email: String? = "",
    val uid: String? = "",
    val username: String? = "username",
    val isVerified: Boolean? = false,
    val displayName: String? = "Display Name",
    val profilePicUri: Uri? = Uri.EMPTY,
    val profilePicUrl: String? = "",
    val follower: Int? = 0,
    val following: Int? = 0,
    val createdAt: String? = ""
)

@Preview
@Composable
fun PreviewProfile() {
}


