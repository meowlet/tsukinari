package com.meow.tsukinari.presentation.authentication

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel? = null,
    onNavToHomePage: () -> Unit,
    onNavToSignInPage: () -> Unit,
) {
    val authUiState = authViewModel?.authUiState
    val isError = authUiState?.signUpError != null
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 32.dp, end = 32.dp, top = 32.dp)
    ) {
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.weight(0.25f)) {
            Logo()
        }
        if (authUiState?.isLoading == true) {
            CircularProgressIndicator()
        }
        if (isError) {
            Text(
                text = authUiState?.signUpError ?: "The error is unknown to the system",
                color = MaterialTheme.colorScheme.error,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier.weight(0.6f), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = authUiState?.userNameSignUp ?: "",
                onValueChange = { authViewModel?.OnUserNameSignUpChange(it) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Email, contentDescription = ""
                    )
                },
                label = {
                    Text(
                        text = "Type your email here", style = MaterialTheme.typography.bodyMedium
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = authUiState?.passwordSignUp ?: "",
                onValueChange = { authViewModel?.OnPassswordSignUpChange(it) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Info, contentDescription = ""
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                label = {
                    Text(
                        text = "Type your password here",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = authUiState?.confirmPasswordSignUp ?: "",
                onValueChange = { authViewModel?.OnConfirmPasswordSignUpChange(it) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Info, contentDescription = ""
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                label = {
                    Text(
                        text = "Confirm your password here",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Forgot password",
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodySmall,
                textDecoration = TextDecoration.Underline
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = {
                authViewModel?.SignUp(context) {
                    onNavToSignInPage.invoke()

                }
            }) {
                Text(
                    "SIGN UP", style = MaterialTheme.typography.titleMedium,

                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Have an account? Sign in now!",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable { onNavToSignInPage.invoke() })
        }
    }


    LaunchedEffect(key1 = authViewModel?.hasUser) {
        if (authViewModel?.hasUser == true) {
            onNavToHomePage.invoke()
        }
    }
}

@Preview
@Composable
fun SignUpPreview() {
    Surface {
        SignUpScreen(onNavToHomePage = { /*TODO*/ }) {

        }
    }
}