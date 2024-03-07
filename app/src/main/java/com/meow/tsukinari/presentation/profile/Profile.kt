package com.meow.tsukinari.presentation.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel? = null,
    onNavToMyFictions: () -> Unit,
    onNavToSignIn: () -> Unit,
) {

    val context = LocalContext.current
    val profileUiState = profileViewModel?.profileUiState

    LaunchedEffect(key1 = Unit) {
        if (profileViewModel?.hasUser == true) {
            profileViewModel.getUserProfile()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
    ) {
        if (profileViewModel?.hasUser != true) {
            Text(
                text = "Please consider signing in to enjoy these features",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(8.dp))
            Button(onClick = { onNavToSignIn.invoke() }) {
                Text(text = "Sign in now")
            }
        } else {
            Column(
                modifier = Modifier.weight(0.5f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(

                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)

                ) {
                    AsyncImage(
                        model = profileUiState?.profilePicUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                }

                Spacer(modifier = Modifier.size(12.dp))
                Text(
                    text = "${profileUiState?.displayName}",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "${profileUiState?.username}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${profileUiState?.email}",
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Column(Modifier.weight(0.5f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    Text(
                        text = "Follower: ${profileUiState?.follower}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Following: ${profileUiState?.following}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Button(onClick = { onNavToMyFictions.invoke() }) {
                    Text(text = "Edit")
                }
            }
        }

    }
}


@Preview(showSystemUi = true)
@Composable
fun ProfilePrev() {
    ProfileScreen(onNavToSignIn = {}, onNavToMyFictions = {})
}