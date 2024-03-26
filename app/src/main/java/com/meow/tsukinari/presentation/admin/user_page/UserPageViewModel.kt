package com.meow.tsukinari.presentation.admin.user_page

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.meow.tsukinari.model.FictionModel
import com.meow.tsukinari.model.UserModel
import com.meow.tsukinari.presentation.admin.AdminRepository
import com.meow.tsukinari.repository.Resources
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserPageViewModel(
    private val repository: AdminRepository = AdminRepository()
) : ViewModel() {


    var userPageUiState by mutableStateOf(UserPageUiState())
        private set


    //get user info
    fun getUserInfo(userId: String) {
        repository.getUserInfoById(
            userId,
            onError = {
                it.message.let {
                    userPageUiState = userPageUiState.copy(errorMessage = it)
                }
            },
            onSuccess = {
                userPageUiState = userPageUiState.copy(
                    userId = it.userId,
                    userName = it.userName,
                    email = it.email,
                    avatarUrl = it.profileImageUrl,
                    aboutMe = it.aboutMe,
                    createdAt = it.createdAt,
                    isAccountActive = it.accountActive,
                    displayName = it.displayName,

                    //update user info
                    newAvatarUri = Uri.EMPTY,
                    newUserName = it.userName,
                    newEmail = it.email,
                    newAboutMe = it.aboutMe,
                    newDisplayName = it.displayName
                )
            }
        )
    }

    //on user name change
    fun onUserNameChange(newUserName: String) {
        userPageUiState = userPageUiState.copy(newUserName = newUserName)
    }

    //on about me change
    fun onAboutMeChange(newAboutMe: String) {
        userPageUiState = userPageUiState.copy(newAboutMe = newAboutMe)
    }

    //on display name change
    fun onDisplayNameChange(newDisplayName: String) {
        userPageUiState = userPageUiState.copy(newDisplayName = newDisplayName)
    }

    //on email change
    fun onEmailChange(newEmail: String) {
        userPageUiState = userPageUiState.copy(newEmail = newEmail)
    }

    //on avatar uri change
    fun onAvatarUriChange(newAvatarUri: Uri) {
        userPageUiState = userPageUiState.copy(newAvatarUri = newAvatarUri)
    }

    //validate fields (fields cannot be empty), at least one field must be different from the original
    fun validateFields(): Boolean {
        return userPageUiState.newUserName.isNotEmpty() &&
                userPageUiState.newDisplayName.isNotEmpty() &&
                userPageUiState.newEmail.isNotEmpty() &&
                userPageUiState.newAboutMe.isNotEmpty() &&
                (userPageUiState.newUserName != userPageUiState.userName ||
                        userPageUiState.newDisplayName != userPageUiState.displayName ||
                        userPageUiState.newEmail != userPageUiState.email ||
                        userPageUiState.newAboutMe != userPageUiState.aboutMe ||
                        userPageUiState.newAvatarUri != Uri.EMPTY)
    }

    //get date
    fun getTime(createdAt: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
        return sdf.format(Date(createdAt))
    }

    //delete user
    fun deleteUser(userId: String) {
        repository.deleteUser(
            userId,
            onSuccess = {
                userPageUiState = userPageUiState.copy(isAccountActive = false)
                getUserInfo(userId)
            },
            onError = {
                it.let {
                    userPageUiState = userPageUiState.copy(errorMessage = it)
                }
            }
        )
    }

    fun enableUser(userId: String) {
        repository.reEnableUser(
            userId,
            onSuccess = {
                userPageUiState = userPageUiState.copy(isAccountActive = true)
                getUserInfo(userId)
            },
            onError = {
                it.let {
                    userPageUiState = userPageUiState.copy(errorMessage = it)
                }
            }
        )
    }

    fun getUserFictions(userId: String) = viewModelScope.launch {
        repository.getUserFictions(userId).collect {
            userPageUiState = userPageUiState.copy(fictionsList = it)
        }
    }


    fun compressImage(imageUri: Uri, context: Context): Uri {
        if (imageUri != Uri.EMPTY) {
            val tempFile = File.createTempFile("temp", "jpg")
            val requestOptions = RequestOptions()
                .override(1080)
            Glide.with(context)
                .asBitmap()
                .load(imageUri)
                .apply(requestOptions)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        resource.compress(
                            Bitmap.CompressFormat.JPEG,
                            80,
                            FileOutputStream(tempFile)
                        )
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
            return Uri.fromFile(tempFile)
        } else {
            return Uri.EMPTY
        }
    }


    //update user info
    fun updateUserInfo(context: Context, userId: String, imageUri: Uri) = viewModelScope.launch {
        userPageUiState = userPageUiState.copy(isLoading = true)
        val usernameAvailability = repository.checkUsername(userPageUiState.newUserName)
        if (usernameAvailability) {
            throw IllegalArgumentException()
        }
        //if username is the same, dont check for availability
        if (userPageUiState.newUserName == userPageUiState.userName) {
            val newUser = UserModel(
                userId = userId,
                accountActive = userPageUiState.isAccountActive,
                email = userPageUiState.newEmail,
                userName = userPageUiState.newUserName,
                displayName = userPageUiState.newDisplayName,
                aboutMe = userPageUiState.newAboutMe,
                createdAt = userPageUiState.createdAt,
                profileImageUrl = userPageUiState.avatarUrl
            )
            repository.updateUserInfo(
                userId,
                compressImage(userPageUiState.newAvatarUri, context),
                newUser,
                onSuccess = {
                    getUserInfo(userId)
                    userPageUiState = userPageUiState.copy(isLoading = false)
                },
                onError = {
                    it.let {
                        userPageUiState = userPageUiState.copy(errorMessage = it, isLoading = false)
                    }
                }
            )
        } else {
            val newUser = UserModel(
                userId = userId,
                accountActive = userPageUiState.isAccountActive,
                email = userPageUiState.newEmail,
                userName = userPageUiState.newUserName,
                displayName = userPageUiState.newDisplayName,
                aboutMe = userPageUiState.newAboutMe,
                createdAt = userPageUiState.createdAt,
                profileImageUrl = userPageUiState.avatarUrl
            )
            repository.updateUserInfo(
                userId,
                compressImage(userPageUiState.newAvatarUri, context),
                newUser,
                onSuccess = {
                    getUserInfo(userId)
                    userPageUiState = userPageUiState.copy(isLoading = false)
                },
                onError = {
                    it.let {
                        userPageUiState = userPageUiState.copy(errorMessage = it, isLoading = false)
                    }
                }
            )
        }
    }

    //check username availability


    fun hideDialog() {
        userPageUiState = userPageUiState.copy(isAccountActive = true)
    }

    fun hideBottomSheet() {
        userPageUiState = userPageUiState.copy(showBottomSheet = false)
    }

    fun showBottomSheet() {
        userPageUiState = userPageUiState.copy(showBottomSheet = true)

    }

}

data class UserPageUiState(
    val userId: String = "",
    val userName: String = "",
    val displayName: String = "",
    val email: String = "",
    val avatarUrl: String = "",
    val aboutMe: String = "",
    val createdAt: Long = 0,
    val isAccountActive: Boolean = true,
    val errorMessage: String = "",

    //ficiton
    val fictionsList: Resources<List<FictionModel>> = Resources.Loading(),

    //update user info
    val newUserName: String = "",
    val newDisplayName: String = "",
    val newEmail: String = "",
    val newAvatarUri: Uri = Uri.EMPTY,
    val newAboutMe: String = "",

    //state
    val isLoading: Boolean = false,
    val showBottomSheet: Boolean = false
)