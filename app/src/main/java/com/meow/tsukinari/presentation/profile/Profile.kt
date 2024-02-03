package com.meow.tsukinari.presentation.profile

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ProfileScreen(
    onNavToMyFictions: () -> Unit
) {
    Text(text = "Here is your profile")
    Button(onClick = { onNavToMyFictions.invoke() }) {
        Text(text = "To My fictions")
    }
}