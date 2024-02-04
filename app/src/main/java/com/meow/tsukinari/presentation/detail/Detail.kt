package com.meow.tsukinari.presentation.detail

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    detailViewModel: DetailViewModel? = null,
    fictionId: String,
    onNavigate: () -> Unit,
) {
    val detailUiState = detailViewModel?.detailUiState ?: DetailUiState()
    LaunchedEffect(key1 = Unit) {
        detailViewModel?.getFiction(fictionId)
    }


    val gradient = Brush.verticalGradient(
        0f to MaterialTheme.colorScheme.surface.copy(alpha = 0.8F),
        1F to MaterialTheme.colorScheme.surface.copy(alpha = 1F)
    )

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { onNavigate.invoke() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "")
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = ""
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(0F),
                    scrolledContainerColor = MaterialTheme.colorScheme.inverseOnSurface
                ),
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .padding(bottom = padding.calculateBottomPadding())
        ) {
            item {
                Box(
                    contentAlignment = Alignment.BottomStart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    AsyncImage(
                        model = detailUiState.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .drawWithCache {
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(gradient)
                                }
                            }
                    )

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {
                        AsyncImage(
                            model = detailUiState.imageUrl,

                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .height(150.dp)
                                .aspectRatio(9f / 14f)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Column(
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier
                                .padding(start = 12.dp, bottom = 32.dp)
                                .fillMaxHeight()
                        ) {
                            Text(
                                text = detailUiState.title,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = detailUiState.uploader,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = "Status: Ongoing",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp), horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.Favorite, contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(text = "Love", style = MaterialTheme.typography.labelMedium)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(text = "Subscribe", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = detailUiState.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

    }
}

@Preview
@Composable
fun DetailPreview() {
    DetailScreen(fictionId = "-NpSa55XzRjb7kqCnrkp") {
    }
}