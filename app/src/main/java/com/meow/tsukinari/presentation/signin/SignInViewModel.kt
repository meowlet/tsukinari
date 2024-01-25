package com.meow.tsukinari.presentation.signin

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meow.tsukinari.repository.AuthRepository
import kotlinx.coroutines.launch

class SignInViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {
    val currentUser = repository.currentUser
    val hasUser: Boolean get() = repository.HasUser()

    var signInUiState by mutableStateOf(SignInUiState())
        private set

    fun onUserNameChange(userName: String) {
        signInUiState = signInUiState.copy(userName = userName)
    }

    fun onPasswordChange(password: String) {
        signInUiState = signInUiState.copy(password = password)
    }

    fun onUserNameSignUpChange(userName: String) {
        signInUiState = signInUiState.copy(userNameSignUp = userName)
    }

    fun onPassswordSignUpChange(password: String) {
        signInUiState = signInUiState.copy(passwordSignUp = password)
    }

    fun onConfirmPasswordSignUpChange(password: String) {
        signInUiState = signInUiState.copy(confirmPasswordSignUp = password)
    }

    fun validateSignInForm(): Boolean {
        return signInUiState.userName.isNotEmpty() && signInUiState.password.isNotEmpty()
    }

    fun validateSignUpForm(): Boolean {
        return signInUiState.userNameSignUp.isNotEmpty() && signInUiState.passwordSignUp.isNotEmpty()
    }

    fun SignUp(
        context: Context
    ) = viewModelScope.launch {
        try {
            if (!validateSignUpForm()) {
                throw IllegalArgumentException("Fields must not be empty.")
            }
            signInUiState = signInUiState.copy(isLoading = true)
            if (signInUiState.passwordSignUp != signInUiState.confirmPasswordSignUp) {
                throw IllegalArgumentException("Password does not match")
            }
            signInUiState = signInUiState.copy(signUpError = "")
            repository.SignUp(
                signInUiState.userNameSignUp,
                signInUiState.passwordSignUp
            ) { isCompleted ->
                if (isCompleted) {
                    Toast.makeText(context, "Successfully signed up!", Toast.LENGTH_SHORT).show()
                    signInUiState = signInUiState.copy(isSuccessful = true)
                } else {
                    Toast.makeText(context, "Failed signing up!", Toast.LENGTH_SHORT).show()
                    signInUiState = signInUiState.copy(isSuccessful = false)
                }
            }
        } catch (e: Exception) {
            signInUiState = signInUiState.copy(signUpError = e.localizedMessage)
            e.printStackTrace()
        } finally {
            signInUiState = signInUiState.copy(isLoading = false)
        }
    }

    fun SignIn(
        context: Context
    ) = viewModelScope.launch {
        try {
            if (!validateSignInForm()) {
                throw IllegalArgumentException("Fields must not be empty.")
            }
            signInUiState = signInUiState.copy(isLoading = true)
            signInUiState = signInUiState.copy(signInError = "")
            repository.SignIn(signInUiState.userName, signInUiState.password) { isCompleted ->
                if (isCompleted) {
                    Toast.makeText(context, "Successfully signed in!", Toast.LENGTH_SHORT).show()
                    signInUiState = signInUiState.copy(isSuccessful = true)
                } else {
                    Toast.makeText(context, "Failed signing in!", Toast.LENGTH_SHORT).show()
                    signInUiState = signInUiState.copy(isSuccessful = false)
                }
            }
        } catch (e: Exception) {
            signInUiState = signInUiState.copy(signInError = e.localizedMessage)
            e.printStackTrace()
        } finally {
            signInUiState = signInUiState.copy(isLoading = false)
        }
    }

}

data class SignInUiState(
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

