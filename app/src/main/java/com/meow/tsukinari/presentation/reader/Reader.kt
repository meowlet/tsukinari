package com.meow.tsukinari.presentation.reader

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel? = null,
) {
    //uistate
    val readerUiState = viewModel?.readerUiState

    LaunchedEffect(key1 = Unit) {
        viewModel?.fetchChapter()
    }

    //make the reader screen inspired by the design from the Tachiyomi app
    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = readerUiState?.isToolbarVisible ?: false,
                enter = slideInVertically(initialOffsetY = { -it }),
                exit = slideOutVertically(targetOffsetY = { -it }),
                content = {
                    TopAppBar(
                        title = { Text(text = readerUiState?.chapterTitle ?: "Chapter 1") },
                        navigationIcon = {
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        },
                        actions = {
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More")
                            }
                        }
                    )
                }
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = readerUiState?.isToolbarVisible ?: false,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                content = {
                    NavigationBar {
                        viewModel!!.bottomBarItems.forEach { item ->
                            NavigationBarItem(
                                selected = false,
                                icon = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = item.title
                                    )
                                },
                                label = { Text(text = item.title) },
                                onClick = { /*TODO*/ }
                            )
                        }
                    }
                }
            )
        },
        content = {
            //make the reader screen inspired by the design from the Tachiyomi app
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { viewModel?.onToolbarVisibilityChanged() },

                content = {
                    items(readerUiState!!.chapterImages.size) { index ->
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(LocalContext.current)
                                    .data(data = readerUiState.chapterImages[index]).apply(block = fun ImageRequest.Builder.() {
                                        crossfade(true)
                                    }).build()
                            ),
                            contentDescription = null,
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        )
                    }
                }
            )
        }
    )
}


@Preview
@Composable
fun ReaderPreview() {
    ReaderScreen()
}