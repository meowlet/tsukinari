package com.meow.tsukinari.presentation.user_profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UserProfileScreen(
    userProfileViewModel: UserProfileViewModel,
    userId: String,
    onNavToDetail: (String) -> Unit,
) {
    //init ui states
    val userProfileUiState = userProfileViewModel.userProfileUiState


    LaunchedEffect(key1 = Unit) {
        userProfileViewModel.getUserProfile(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(text = userProfileUiState.displayName ?: "")
        Text(text = userProfileUiState.userName ?: "")
        Text(text = userProfileUiState.aboutMe ?: "")

    }

}