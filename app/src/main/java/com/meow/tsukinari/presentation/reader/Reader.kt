package com.meow.tsukinari.presentation.reader

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import coil.compose.AsyncImage

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel? = null,
    fictionId: String = "fictionId",
) {
    //uistate
    val readerUiState = viewModel?.readerUiState

    LaunchedEffect(key1 = Unit) {
        viewModel?.fetchChapter(fictionId)
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
                        viewModel?.bottomBarItems?.forEach { item ->
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
        }) {
        val pagerState = rememberPagerState(pageCount = {
            readerUiState?.chapterImages?.size ?: 0
        })

        HorizontalPager(
            state = pagerState,
            //if clicked the right side of the screen, go to the next page, if clicked the left side of the screen, go to the previous page

            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    viewModel?.onToolbarVisibilityChanged()
                }

        ) { page ->
            // Our page content
            readerUiState?.chapterImages?.get(page)?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                )
            }
            Text(
                text = "Page: $page",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

}

