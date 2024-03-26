package com.meow.tsukinari.presentation.admin.user_page

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.meow.tsukinari.model.FictionModel
import com.meow.tsukinari.repository.Resources

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPageScreen(
    userId: String,
    viewModel: UserPageViewModel? = null,
    onNavUp: () -> Unit,
    onNavToUpdate: (String) -> Unit
) {

    val userPageUiState = viewModel?.userPageUiState
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()

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
        viewModel?.getUserFictions(userId)
    }
    Scaffold(
        topBar = {
            TopAppBar(title = { userPageUiState?.displayName }, navigationIcon = {
                IconButton(onClick = { onNavUp.invoke() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            }, actions = {
                IconButton(onClick = {
                    viewModel?.showBottomSheet()
                }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = "")
                }
            })
        },
        floatingActionButton = {

            if (viewModel!!.validateFields()) {
                FloatingActionButton(onClick = {
                    viewModel.updateUserInfo(context, userId, userPageUiState!!.newAvatarUri)
                }) {
                    if (userPageUiState?.isLoading == true) {
                        CircularProgressIndicator()
                    } else {
                        Icon(Icons.Default.Check, contentDescription = "Save Changes")
                    }
                }
            }

        },
    ) {

        if (userPageUiState!!.showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    viewModel.hideBottomSheet()
                },
                sheetState = sheetState,
            ) {
                if (userPageUiState.fictionsList.data?.isEmpty() == true) {
                    Text(
                        text = "User has no fictions!",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(align = Alignment.Center)
                    )
                }

                when (userPageUiState.fictionsList) {
                    is Resources.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize(align = Alignment.Center)
                        )
                    }

                    is Resources.Success -> {
                        LazyColumn {
                            items(
                                userPageUiState.fictionsList.data ?: emptyList()
                            ) { fiction ->
                                FictionItem(
                                    fiction = fiction,
                                    uploadedAt = viewModel.getTime(fiction.uploadedAt)
                                ) {
                                    onNavToUpdate(fiction.fictionId)
                                }
                            }
                        }

                    }

                    //

                    else -> {
                        Text(
                            text = userPageUiState.fictionsList.throwable?.localizedMessage
                                ?: "Unknown Error",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (userPageUiState.errorMessage.isNotBlank()) {
                Text(
                    text = userPageUiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontStyle = MaterialTheme.typography.bodySmall.fontStyle,
                    modifier = Modifier.padding(8.dp)
                )
            }
            AsyncImage(
                model = if (userPageUiState.newAvatarUri != Uri.EMPTY) userPageUiState.newAvatarUri else userPageUiState.avatarUrl,
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
                    text = userPageUiState.userId ?: "Error fetching user id",
                    modifier = Modifier.padding(4.dp),
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = "Created at: ${viewModel.getTime(userPageUiState.createdAt ?: 0)}")
            Text(
                text = "Username:",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 2.dp)
            )
            OutlinedTextField(
                value = userPageUiState.newUserName,
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


@Composable
fun FictionItem(
    fiction: FictionModel,
    uploadedAt: String,
    onClick: () -> Unit
) {
    OutlinedCard(
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(8.dp),
        onClick = {
            onClick.invoke()
        },
        modifier = Modifier
            .padding(horizontal = 18.dp, vertical = 6.dp)
            .height(100.dp)

    ) {
        Row(
            Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    fiction.coverLink
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(6.dp))
            )
            Spacer(modifier = Modifier.size(8.dp))
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = fiction.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = fiction.description,
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
//                Text(
//                    text = "Description: ${fiction.description}",
//                    style = MaterialTheme.typography.bodyMedium,
//                    maxLines = 2,
//                    overflow = TextOverflow.Ellipsis
//                )
                Text(
                    text = uploadedAt,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}
