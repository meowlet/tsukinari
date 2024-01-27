package com.meow.tsukinari.presentation.my_fictions

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.meow.tsukinari.presentation.authentication.AuthViewModel

@Composable
fun MyFictionsScreen(
    authViewModel: AuthViewModel? = null,
    onNavToSignInPage: () -> Unit
) {
    Surface {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val context = LocalContext.current
            Text(text = "Home Screen")
            Button(onClick = {
                authViewModel?.signOut()
                onNavToSignInPage.invoke()
            }) {
                Text(text = "Sign out")
            }
        }

    }
}