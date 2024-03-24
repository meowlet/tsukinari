package com.meow.tsukinari.presentation.admin.user_page

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun UserPageScreen(userId: String, viewModel: UserPageViewModel? = null, onNavUp: () -> Unit) {

    val userPageUiState = viewModel?.userPageUiState
    val context = LocalContext.current

    when (userPageUiState?.isAccountActive) {
        false -> {
            AlertDialog(
                onDismissRequest = {
                    onNavUp.invoke()
                    viewModel.hideDialog()
                },
                onConfirmation = { viewModel.enableUser(userId) },
                dialogTitle = "Account Disabled",
                dialogText = "This account has been disabled. Do you want to re-enable it?"
            )
        }

        else -> {}
    }


    //single image picker
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel?.onAvatarUriChange(uri)
        }
    }
    LaunchedEffect(Unit) {
        viewModel?.getUserInfo(userId)
    }
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = if (userPageUiState?.newAvatarUri != Uri.EMPTY) userPageUiState?.newAvatarUri else userPageUiState?.avatarUrl,
                contentDescription = "User Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clickable { imagePicker.launch("image/*") }
                    .padding(24.dp)
                    .clip(
                        CircleShape
                    )
                    .background(color = MaterialTheme.colorScheme.primaryContainer)
                    .size(192.dp)
            )
            Text(text = "UserId:")
            Box(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = userPageUiState?.userId ?: "Error fetching user id",
                    modifier = Modifier.padding(4.dp),
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = "Created at: ${viewModel?.getTime(userPageUiState?.createdAt ?: 0)}")
            Text(
                text = "Username:",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 2.dp)
            )
            OutlinedTextField(
                value = userPageUiState!!.newUserName,
                onValueChange = { viewModel.onUserNameChange(it) },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    AnimatedVisibility(visible = userPageUiState.newUserName != userPageUiState.userName) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                viewModel.onUserNameChange(userPageUiState.userName)
                            })
                    }
                }
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = "Display Name:",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 2.dp)
            )
            OutlinedTextField(
                value = userPageUiState.newDisplayName,
                onValueChange = { viewModel.onDisplayNameChange(it) },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    AnimatedVisibility(visible = userPageUiState.newDisplayName != userPageUiState.displayName) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                viewModel.onDisplayNameChange(userPageUiState.displayName)
                            })
                    }
                }
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = "About Me:",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 2.dp)
            )
            OutlinedTextField(
                value = userPageUiState.newAboutMe,
                onValueChange = { viewModel.onAboutMeChange(it) },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    AnimatedVisibility(visible = userPageUiState.newAboutMe != userPageUiState.aboutMe) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                viewModel.onAboutMeChange(userPageUiState.aboutMe)
                            })
                    }
                }
            )
            Spacer(modifier = Modifier.size(16.dp))
            OutlinedButton(onClick = {
                viewModel.deleteUser(userId)
            }) {
                Text(text = "Disable account", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Preview
@Composable
fun adsflikjsfd() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        AsyncImage(
            model = Uri.EMPTY,
            contentDescription = "User Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(24.dp)
                .clip(
                    CircleShape
                )
                .background(color = MaterialTheme.colorScheme.primaryContainer)
                .size(192.dp)
        )
        //aditional info
        Text(text = "UserId: 1234567809")
        Text(text = "Created at: 2021-09-09")
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = "Username:",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 2.dp)
        )
        OutlinedTextField(
            value = "userPageUiState!!.newUserName",
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.clickable {

                    })
            },

            )
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = "Username:",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 2.dp)
        )
        OutlinedTextField(
            value = "userPageUiState!!.newUserName",
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.clickable {

                    })
            },

            )

        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = "Username:",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 2.dp)
        )
        OutlinedTextField(
            value = "userPageUiState!!.newUserName",
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.clickable {

                    })
            },
        )


    }
}


@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.Info, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Re-enable account")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Go back")
            }
        }
    )
}
