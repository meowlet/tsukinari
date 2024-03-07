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


    val hasUser: Boolean get() = repository.hasUser()

    var authUiState by mutableStateOf(AuthUiState())
        private set

    fun onEmailChange(email: String) {
        authUiState = authUiState.copy(email = email)
        authUiState = authUiState.copy(signInError = "")
    }

    fun onPasswordChange(password: String) {
        authUiState = authUiState.copy(password = password)
        authUiState = authUiState.copy(signInError = "")
    }

    fun onUsernameSignUpChange(username: String) {
        authUiState = authUiState.copy(usernameSignUp = username)
        authUiState = authUiState.copy(signUpError = "")
    }

    fun onEmailSignUpChange(email: String) {
        authUiState = authUiState.copy(emailSignUp = email)
        authUiState = authUiState.copy(signUpError = "")
    }

    fun onPassswordSignUpChange(password: String) {
        authUiState = authUiState.copy(passwordSignUp = password)
        authUiState = authUiState.copy(signUpError = "")
    }

    fun onConfirmPasswordSignUpChange(password: String) {
        authUiState = authUiState.copy(confirmPasswordSignUp = password)
        authUiState = authUiState.copy(signUpError = "")
    }

    fun onEmailResetChange(emailReset: String) {
        authUiState = authUiState.copy(emailReset = emailReset)
        authUiState = authUiState.copy(resetPasswordError = "")
    }

    fun validateSignInForm(): Boolean {
        return authUiState.email.isNotEmpty() && authUiState.password.isNotEmpty()
    }

    fun validateSignUpForm(): Boolean {
        return authUiState.emailSignUp.isNotEmpty() && authUiState.passwordSignUp.isNotEmpty() && authUiState.usernameSignUp.isNotEmpty()
    }


    //rewrite the signUp function in an optimized way (imporve the code quality and readability)
    fun signUp(
        context: Context,
        onSignUpCompleted: () -> Unit
    ) = viewModelScope.launch {
        try {
//            if (!validateSignUpForm()) {
//                throw IllegalArgumentException("Fields must not be empty.")
//            }
//            if (authUiState.passwordSignUp != authUiState.confirmPasswordSignUp) {
//                throw IllegalArgumentException("Password does not match")
//            }
//            authUiState = authUiState.copy(isLoading = true)
//            authUiState = authUiState.copy(signUpError = "")

            if (repository.checkUsername(authUiState.usernameSignUp)) {
                throw IllegalArgumentException("Username is already taken")
            }

            repository.signUp(
                authUiState.emailSignUp,
                authUiState.passwordSignUp
            ) { isCompleted ->
                if (isCompleted) {
                    Toast.makeText(context, repository.getUserId(), Toast.LENGTH_SHORT).show()
                    repository.registerUser(
                        repository.getUserId(),
                        repository.getUserEmail(),
                        authUiState.usernameSignUp,
                        System.currentTimeMillis()
                    ) { isRegistered ->
                        if (isRegistered) {
                            Toast.makeText(context, "Successfully signed up!", Toast.LENGTH_SHORT)
                                .show()
                            authUiState = authUiState.copy(isSuccessful = true)
                            signOut()
                            onSignUpCompleted.invoke()
                        } else {
                            Toast.makeText(context, "Failed signing up!", Toast.LENGTH_SHORT).show()
                            authUiState = authUiState.copy(isSuccessful = false)
                        }
                    }
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


    fun clearSignUpForm() {
        authUiState = authUiState.copy(emailSignUp = "")
        authUiState = authUiState.copy(passwordSignUp = "")
        authUiState = authUiState.copy(confirmPasswordSignUp = "")
    }


    fun signIn(
        context: Context
    ) = viewModelScope.launch {
        try {
            if (!validateSignInForm()) {
                throw IllegalArgumentException("Fields must not be empty.")
            }
            authUiState = authUiState.copy(isLoading = true)
            authUiState = authUiState.copy(signInError = "")
            repository.signIn(authUiState.email, authUiState.password) { isCompleted ->
                if (isCompleted) {
                    Toast.makeText(context, "Successfully signed in!", Toast.LENGTH_SHORT).show()
                    authUiState = authUiState.copy(isSuccessful = true)
                } else {
                    Toast.makeText(context, "Failed signing in!", Toast.LENGTH_SHORT).show()
                    authUiState = authUiState.copy(isSuccessful = false)
                }
            }
        } catch (e: Exception) {
            authUiState = authUiState.copy(signInError = e.localizedMessage as String)
            e.printStackTrace()
        } finally {
            authUiState = authUiState.copy(isLoading = false)
        }
    }

    fun resetPassword(
        context: Context,
        onResetPasswordCompleted: () -> Unit
    ) = viewModelScope.launch {
        try {
            authUiState = authUiState.copy(isLoading = true)
            repository.resetPassword(authUiState.emailReset) { isCompleted ->
                if (isCompleted) {
                    Toast.makeText(
                        context,
                        "Password reset email is sent to ${authUiState.emailReset}",
                        Toast.LENGTH_SHORT
                    ).show()
                    authUiState = authUiState.copy(isSuccessful = true)
                    onResetPasswordCompleted.invoke()
                } else {
                    Toast.makeText(
                        context,
                        "Failed to send password reset email!",
                        Toast.LENGTH_SHORT
                    ).show()
                    authUiState = authUiState.copy(isSuccessful = false)
                }
            }
        } catch (e: Exception) {
            authUiState = authUiState.copy(resetPasswordError = e.localizedMessage)
            e.printStackTrace()
        } finally {
            authUiState = authUiState.copy(isLoading = false)
        }
    }

    fun signOut() = viewModelScope.launch {
        repository.signOut()
    }
}

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val usernameSignUp: String = "",
    val emailSignUp: String = "",
    val passwordSignUp: String = "",
    val confirmPasswordSignUp: String = "",
    val emailReset: String = "",
    val isLoading: Boolean = false,
    val isSuccessful: Boolean = false,
    val signInError: String = "",
    val signUpError: String = "",
    val resetPasswordError: String = "",
)
