package com.meow.tsukinari.presentation.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel? = null,
    onNavToMyFictions: () -> Unit,
    onNavToSignIn: () -> Unit,
) {

    val context = LocalContext.current
    val profileUiState = profileViewModel?.profileUiState


    //snackbar host
    val snackbarHostState = remember { SnackbarHostState() }

    //init the image picker
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            profileViewModel?.onProfilePicChanged(uri)
        }
    }

    LaunchedEffect(key1 = profileViewModel?.hasUser) {
        if (profileViewModel?.hasUser == true) {
            profileViewModel.getUserProfile()
        }
    }

    LaunchedEffect(
        key1 = profileUiState?.isAboutMeUpdated,
        key2 = profileUiState?.isDisplayNameUpdated
    ) {
        if (profileUiState!!.isAboutMeUpdated) {
            snackbarHostState.showSnackbar("About me updated")
            profileViewModel.resetUpdatedState()
        }
        if (profileUiState.isDisplayNameUpdated) {
            snackbarHostState.showSnackbar("Display name updated")
            profileViewModel.resetUpdatedState()
        }
    }

    LaunchedEffect(key1 = profileUiState?.isProfilePicUpdated) {
        if (profileUiState!!.isProfilePicUpdated) {
            snackbarHostState.showSnackbar("Profile pic updated")
            profileViewModel.resetUpdatedState()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp, vertical = 8.dp)
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
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(

                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(128.dp)
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)

                        ) {
                            if (profileUiState?.newProfilePicUri == Uri.EMPTY) {
                                AsyncImage(
                                    model = profileUiState?.profilePicUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(128.dp)
                                        .clip(CircleShape)
                                        .combinedClickable(
                                            onClick = {
                                            },
                                            onLongClick = { imagePicker.launch("image/*") }

                                        )
                                )
                            } else {
                                AsyncImage(
                                    model = profileUiState?.newProfilePicUri,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(128.dp)
                                        .clip(CircleShape)
                                )
                                if (profileUiState!!.isImageUploadLoading) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(128.dp)
                                            .clip(CircleShape)
                                            .background(
                                                MaterialTheme.colorScheme.background.copy(
                                                    alpha = 0.5f
                                                )
                                            )
                                    ) {
                                        CircularProgressIndicator()
                                    }

                                }
                            }
                        }
                        if (profileUiState?.newProfilePicUri != Uri.EMPTY) {
                            Column {
                                IconButton(
                                    onClick = { profileViewModel.onProfilePicChanged(Uri.EMPTY) },
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null)
                                }
                                IconButton(
                                    onClick = { profileViewModel.updateProfilePic(context) },
                                ) {
                                    Icon(Icons.Default.Done, contentDescription = null)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.size(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (profileUiState?.isDisplayNameEditing == true) {
                            OutlinedTextField(
                                value = profileUiState.newDisplayName ?: "",
                                onValueChange = { profileViewModel.onNewDisplayNameChanged(it) },
                                modifier = Modifier
                                    .weight(0.6f)
                                    .padding(8.dp),
                                maxLines = 1
                            )
                            IconButton(
                                onClick = { profileViewModel.changeDisplayNameEditingState() },
                                modifier = Modifier.weight(0.1f)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }
                            IconButton(
                                onClick = { profileViewModel.updateDisplayName() },
                                modifier = Modifier.weight(0.1f)
                            ) {
                                Icon(Icons.Default.Done, contentDescription = null)
                            }
                        } else {
                            Text(
                                text = "${profileUiState?.displayName}",
                                style = MaterialTheme.typography.titleLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.combinedClickable(
                                    onClick = { },
                                    onLongClick = { profileViewModel.changeDisplayNameEditingState() },
                                )
                            )
                        }
                    }
                    //small username
                    Text(
                        text = "Username: ${profileUiState?.username}",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "Email: ${profileUiState?.email}",
                        style = MaterialTheme.typography.labelMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (profileUiState?.isAboutMeEditing == true) {
                            OutlinedTextField(
                                value = profileUiState.newAboutMe ?: "",
                                onValueChange = { profileViewModel.onNewAboutMeChanged(it) },
                                modifier = Modifier
                                    .weight(0.8f)
                                    .padding(8.dp),
                                maxLines = 8
                            )
                            IconButton(
                                onClick = { profileViewModel.changeAboutMeEditingState() },
                                modifier = Modifier.weight(0.1f)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }
                            IconButton(
                                onClick = { profileViewModel.updateAboutMe() },
                                modifier = Modifier.weight(0.1f)
                            ) {
                                Icon(Icons.Default.Done, contentDescription = null)
                            }
                        } else {
                            Text(
                                text = if (profileUiState?.aboutMe?.isNotEmpty() == true) profileUiState.aboutMe else "Your bio goes here",
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 8,
                                textAlign = TextAlign.Center,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .combinedClickable(
                                        onClick = { },
                                        onLongClick = { profileViewModel.changeAboutMeEditingState() }
                                    )
                            )
                        }

                    }

                    //error here
                    Text(
                        text = profileUiState?.fieldError ?: "",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .weight(0.1f), horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(
                            text = "Follower: ${profileUiState?.follower}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Following: ${profileUiState?.following}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Box(
                        modifier = Modifier.weight(0.9f),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(onClick = { onNavToMyFictions.invoke() }) {
                            Text(text = "My fictions")
                        }
                    }
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