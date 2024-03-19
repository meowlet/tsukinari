package com.meow.tsukinari.presentation.user_profile

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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.meow.tsukinari.presentation.browse.FictionItem
import com.meow.tsukinari.repository.Resources

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
        userProfileViewModel.getUserFictions(userId)
    }

    Scaffold {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

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
                        AsyncImage(
                            model = userProfileUiState.profileImageUrl ?: "",
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(128.dp)
                                .clip(CircleShape)
                        )


                    }

                }

                Spacer(modifier = Modifier.size(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "${userProfileUiState.displayName}",
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,

                        )

                }
                //small username
                Text(
                    text = "Username: ${userProfileUiState.userName}",
                    style = MaterialTheme.typography.labelMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = if (userProfileUiState.aboutMe?.isNotEmpty() == true) userProfileUiState.aboutMe else "Nothing to show here!",
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 8,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(8.dp)
                    )


                }
            }

            Spacer(modifier = Modifier.size(8.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .weight(0.1f), horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        text = "Follower: ${userProfileUiState.follower}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Following: ${userProfileUiState.following}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Box(
                    modifier = Modifier.weight(0.9f),
                ) {

                    //if the fictionlist is null, say user has no fictions
                    if (userProfileUiState.fictionsList.data?.isEmpty() == true) {
                        Text(
                            text = "User has no fictions!",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize(align = Alignment.Center)
                        )
                    }

                    when (userProfileUiState.fictionsList) {
                        is Resources.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .wrapContentSize(align = Alignment.Center)
                            )
                        }

                        is Resources.Success -> {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(
                                    items = userProfileUiState.fictionsList.data ?: emptyList()

                                ) { fiction ->
                                    FictionItem(fiction = fiction, onClick = {
                                        onNavToDetail.invoke(fiction.fictionId)
                                    })
                                }
                            }
                        }

                        //

                        else -> {
                            Text(
                                text = userProfileUiState.fictionsList.throwable?.localizedMessage
                                    ?: "Unknown Error",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                }
            }
        }

    }
}





