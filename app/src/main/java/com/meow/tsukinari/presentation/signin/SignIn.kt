package com.meow.tsukinari.presentation.signin

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun SignInScreen(
    signInViewModel: SignInViewModel? = null,
    onNavToHomePage: () -> Unit,
    onNavToSignUpPage: () -> Unit,
) {
    val signInUiState = signInViewModel?.signInUiState
    val isError = signInUiState?.signInError != ""
    val context = LocalContext.current

}