package com.meow.tsukinari.presentation.admin

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.meow.tsukinari.model.UserModel
import com.meow.tsukinari.presentation.my_fictions.FictionItem
import com.meow.tsukinari.repository.Resources
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AdminScreen(
    adminViewModel: AdminViewModel = AdminViewModel(),
    onNavToHomePage: () -> Unit,
    onNavToUserPage: (String) -> Unit,
    onNavToFictionPage: (String) -> Unit
) {
    val adminUiState = adminViewModel.adminUiState

    val context = LocalContext.current

    val pagerState = rememberPagerState(pageCount = { 2 })

    LaunchedEffect(key1 = Unit) {
        if (adminViewModel.isAdmin) {


        } else {
            onNavToHomePage.invoke()
            Toast.makeText(context, "Know you place!", Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(key1 = adminUiState.pendingFictions) {
        adminViewModel.countPendingFictions()
    }

    LaunchedEffect(key1 = adminUiState.currentTab) {
        pagerState.animateScrollToPage(adminUiState.currentTab)
        when (adminUiState.currentTab) {
            0 -> {
                adminViewModel.getUserList()
            }

            1 -> {
                adminViewModel.getPendingFictions()
            }
        }
    }

    LaunchedEffect(key1 = pagerState.currentPage) {
        adminViewModel.changeTab(pagerState.currentPage)
        when (pagerState.currentPage) {
            0 -> {
                adminViewModel.getUserList()
                if (adminUiState.userList is Resources.Success) {
                    adminViewModel.getPendingFictions()
                }
            }

            1 -> {
                adminViewModel.getPendingFictions()
            }
        }
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            TabRow(
                selectedTabIndex = adminUiState.currentTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = adminUiState.currentTab == 0,
                    onClick = { adminViewModel.changeTab(0) }) {
                    Text(text = "Users", modifier = Modifier.padding(8.dp))
                }
                Tab(
                    selected = adminUiState.currentTab == 1,
                    onClick = { adminViewModel.changeTab(1) }) {
                    Text(text = "Pending fictions", modifier = Modifier.padding(8.dp))
                }

            }
            HorizontalPager(state = pagerState) { page ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (page) {
                        0 -> {
                            when (val userList = adminUiState.userList) {
                                is Resources.Success -> {
//                                    adminViewModel.getPendingFictions()
                                    userList.data?.forEach { user ->
                                        UserItem(
                                            user = user,
                                            onClick = {
                                                onNavToUserPage(user.userId)
                                            }
                                        )
                                    }
                                }

                                is Resources.Loading -> {
                                    CircularProgressIndicator()
                                }

                                is Resources.Error -> {
                                    Text(
                                        text = "Error: ${userList.throwable?.message}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }

                        1 -> {
                            when (val pendingFictions = adminUiState.pendingFictions) {
                                is Resources.Success -> {
                                    pendingFictions.data?.forEach { fiction ->
                                        FictionItem(
                                            fiction = fiction,
                                            onClick = {
                                                onNavToFictionPage(fiction.fictionId)
                                            },
                                            uploadedAt = adminViewModel.getTime(fiction.uploadedAt)
                                        )
                                    }
                                }

                                is Resources.Loading -> {
                                    Text(
                                        text = "Waiting for the next fiction to appear here",
                                        modifier = Modifier.padding(8.dp)
                                    )
                                    CircularProgressIndicator()
                                }

                                is Resources.Error -> {
                                    Text(
                                        text = "Error: ${pendingFictions.throwable?.message}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }

                        }
                    }
                }
            }

        }
    }
    //show the user list

}

@Composable
fun UserItem(
    user: UserModel,
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
        fun getTime(createdAt: Long): String {
            val sdf = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
            return sdf.format(Date(createdAt))
        }
        Row(
            Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    user.profileImageUrl
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
                        text = user.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = user.userName,
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
                    text = getTime(user.createdAt),
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

