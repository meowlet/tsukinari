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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import coil.compose.rememberAsyncImagePainter
import com.meow.tsukinari.model.FictionModel
import com.meow.tsukinari.model.UserModel
import com.meow.tsukinari.repository.Resources
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    adminViewModel: AdminViewModel = AdminViewModel(),
    onNavToHomePage: () -> Unit,
    onNavToUserPage: (String) -> Unit,
    onNavToDetailPage: (String) -> Unit
) {
    val adminUiState = adminViewModel.adminUiState

    val context = LocalContext.current

    val pagerState = rememberPagerState(pageCount = { 3 })

    LaunchedEffect(key1 = Unit) {
        if (adminViewModel.isAdmin) {
            adminViewModel.getUserList()

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

            2 -> {
                adminViewModel.getFullStats()
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

            2 -> {
                adminViewModel.getFullStats()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Admin portal") }, navigationIcon = {
                IconButton(onClick = { onNavToHomePage.invoke() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            })
        },

        ) {
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
                    Text(text = "Pending", modifier = Modifier.padding(8.dp))
                }
                Tab(
                    selected = adminUiState.currentTab == 2,
                    onClick = { adminViewModel.changeTab(2) }) {
                    Text(text = "Stats", modifier = Modifier.padding(8.dp))
                }

            }
            HorizontalPager(state = pagerState) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (page) {
                        0 -> {
                            Text(
                                text = "User list:",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
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
                            Text(
                                text = "${adminUiState.pendingFictionsCount} pending fictions:",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )

                            when (val pendingFictions = adminUiState.pendingFictions) {
                                is Resources.Success -> {
                                    pendingFictions.data?.forEach { fiction ->
                                        FictionItemm(
                                            fiction = fiction,
                                            onClick = {
                                                onNavToDetailPage(fiction.fictionId)
                                            },
                                            uploadedAt = adminViewModel.getTime(fiction.uploadedAt),
                                            onConfirmClick = {
                                                adminViewModel.verifyFiction(fiction.fictionId)
                                            }
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

                        2 -> {
                            Text(
                                text = "Full stats:",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                            when (val stats = adminUiState.stats) {
                                is Resources.Success -> {
                                    Text(
                                        text = "Total users: ${stats.data?.totalUsers}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                    Text(
                                        text = "Total fictions: ${stats.data?.totalFictions}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                    Text(
                                        text = "Total chapters: ${stats.data?.totalChapters}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                    Text(
                                        text = "Total views: ${stats.data?.totalViews}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                    Text(
                                        text = "Total likes: ${stats.data?.totalLikes}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                    Text(
                                        text = "Total dislikes: ${stats.data?.totalDislikes}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                    Text(
                                        text = "Total comments: ${stats.data?.totalComments}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                    Text(
                                        text = "Total verified fictions: ${stats.data?.totalVerifiedFictions}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                    Text(
                                        text = "Total unverified fictions: ${stats.data?.totalUnverifiedFictions}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                    Text(
                                        text = "Total active users: ${stats.data?.totalActiveUser}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                    Text(
                                        text = "Total inactive users: ${stats.data?.totalInactiveUser}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(8.dp)
                                    )

                                }

                                is Resources.Loading -> {
                                    Text(
                                        text = "Loading stats...",
                                        modifier = Modifier.padding(8.dp)
                                    )
                                    CircularProgressIndicator()
                                }

                                is Resources.Error -> {
                                    Text(
                                        text = "Error: ${stats.throwable?.message}",
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

@Composable
fun FictionItemm(
    fiction: FictionModel,
    uploadedAt: String,
    onClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    OutlinedCard(
        shape = RoundedCornerShape(8.dp),
        onClick = { onClick.invoke() },
        modifier = Modifier
            .padding(horizontal = 18.dp, vertical = 6.dp)
            .height(100.dp)
    ) {
        Row(
            Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = rememberAsyncImagePainter(fiction.coverLink),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(6.dp))
            )
            Spacer(modifier = Modifier.size(8.dp))
            Column(
                modifier = Modifier
                    .weight(0.85f)
                    .fillMaxHeight(), // Use weight here
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
                Text(
                    text = uploadedAt,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .padding(bottom = 2.dp)
                        .align(Alignment.End)
                )
            }
            Column(
                modifier = Modifier.weight(0.15f), // Use weight here
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { onConfirmClick.invoke() }
                ) {
                    Icon(imageVector = Icons.Default.Done, contentDescription = null)
                }
            }
        }
    }
}

@Preview
@Composable
fun alfsj() {
    FictionItemm(fiction = FictionModel(
        title = "alskfdjafds",
        description = "áldkfjalfdskalkfdsjlkfdsalkfdsal",
        uploadedAt = 14320
    ),
        uploadedAt = "slfdkj",
        onConfirmClick = {},
        onClick = {})
}



