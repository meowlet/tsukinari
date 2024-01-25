package com.meow.tsukinari.presentation.authentication

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meow.tsukinari.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {
    val currentUser = repository.currentUser

    val hasUser: Boolean get() = repository.HasUser()

    var authUiState by mutableStateOf(AuthUiState())
        private set

    fun OnUserNameChange(userName: String) {
        authUiState = authUiState.copy(userName = userName)
        authUiState = authUiState.copy(signInError = "")
    }

    fun OnPasswordChange(password: String) {
        authUiState = authUiState.copy(password = password)
        authUiState = authUiState.copy(signInError = "")
    }

    fun OnUserNameSignUpChange(userName: String) {
        authUiState = authUiState.copy(userNameSignUp = userName)
        authUiState = authUiState.copy(signUpError = "")
    }

    fun OnPassswordSignUpChange(password: String) {
        authUiState = authUiState.copy(passwordSignUp = password)
        authUiState = authUiState.copy(signUpError = "")
    }

    fun OnConfirmPasswordSignUpChange(password: String) {
        authUiState = authUiState.copy(confirmPasswordSignUp = password)
        authUiState = authUiState.copy(signUpError = "")
    }

    fun ValidateSignInForm(): Boolean {
        return authUiState.userName.isNotEmpty() && authUiState.password.isNotEmpty()
    }

    fun ValidateSignUpForm(): Boolean {
        return authUiState.userNameSignUp.isNotEmpty() && authUiState.passwordSignUp.isNotEmpty()
    }

    fun SignUp(
        context: Context
    ) = viewModelScope.launch {
        try {
            if (!ValidateSignUpForm()) {
                throw IllegalArgumentException("Fields must not be empty.")
            }
            authUiState = authUiState.copy(isLoading = true)
            if (authUiState.passwordSignUp != authUiState.confirmPasswordSignUp) {
                throw IllegalArgumentException("Password does not match")
            }
            authUiState = authUiState.copy(signUpError = "")
            repository.SignUp(
                authUiState.userNameSignUp,
                authUiState.passwordSignUp
            ) { isCompleted ->
                if (isCompleted) {
                    Toast.makeText(context, "Successfully signed up!", Toast.LENGTH_SHORT).show()
                    authUiState = authUiState.copy(isSuccessful = true)
                } else {
                    Toast.makeText(context, "Failed signing up!", Toast.LENGTH_SHORT).show()
                    authUiState = authUiState.copy(isSuccessful = false)
                }
            }
        } catch (e: Exception) {
            authUiState = authUiState.copy(signUpError = e.localizedMessage)
            e.printStackTrace()
        } finally {
            authUiState = authUiState.copy(isLoading = false)
        }
    }

    fun SignIn(
        context: Context
    ) = viewModelScope.launch {
        try {
            if (!ValidateSignInForm()) {
                throw IllegalArgumentException("Fields must not be empty.")
            }
            authUiState = authUiState.copy(isLoading = true)
            authUiState = authUiState.copy(signInError = "")
            repository.SignIn(authUiState.userName, authUiState.password) { isCompleted ->
                if (isCompleted) {
                    Toast.makeText(context, "Successfully signed in!", Toast.LENGTH_SHORT).show()
                    authUiState = authUiState.copy(isSuccessful = true)
                } else {
                    Toast.makeText(context, "Failed signing in!", Toast.LENGTH_SHORT).show()
                    authUiState = authUiState.copy(isSuccessful = false)
                }
            }
        } catch (e: Exception) {
            authUiState = authUiState.copy(signInError = e.localizedMessage)
            e.printStackTrace()
        } finally {
            authUiState = authUiState.copy(isLoading = false)
        }
    }

    fun SignOut(
    ) = viewModelScope.launch {
        repository.SignOut()

    }
}

data class AuthUiState(
    val userName: String = "",
    val password: String = "",
    val userNameSignUp: String = "",
    val passwordSignUp: String = "",
    val confirmPasswordSignUp: String = "",
    val isLoading: Boolean = false,
    val isSuccessful: Boolean = false,
    val signInError: String = "",
    val signUpError: String = "",
)

