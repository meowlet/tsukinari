package com.meow.tsukinari.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext

@Composable
fun HomeScreen(
    uploadViewModel: UploadViewModel? = null,
    onNavToSignInPage: () -> Unit
) {
    Surface {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val context = LocalContext.current
            Text(text = "Home Screen")
            Button(onClick = { uploadViewModel?.testPushData() }) {
                Text(text = "Sign out")
            }
        }

    }
}