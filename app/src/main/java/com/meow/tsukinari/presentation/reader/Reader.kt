package com.meow.tsukinari.presentation.reader

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.toggleScale
import net.engawapg.lib.zoomable.zoomable

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel? = null,
    fictionId: String = "fictionId",
    onNavUp: () -> Unit
) {
    //uistate
    val readerUiState = viewModel?.readerUiState

    LaunchedEffect(key1 = Unit) {
        viewModel?.fetchChapter(fictionId)
    }

    val zoomState = rememberZoomState(
        maxScale = 5f
    )

    //make the reader screen inspired by the design from the Tachiyomi app
    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = readerUiState?.isToolbarVisible ?: false,
                content = {
                    TopAppBar(
                        title = { Text(text = readerUiState?.chapterTitle ?: "Chapter 1") },
                        navigationIcon = {
                            IconButton(onClick = {
                                onNavUp()
                            }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        },
                        actions = {
                            Box {
                                IconButton(onClick = {
                                    viewModel?.onContextMenuVisibilityChanged()
                                }) {
                                    Icon(
                                        Icons.Filled.MoreVert,
                                        contentDescription = "More"
                                    )
                                }
                                DropdownMenu(
                                    expanded = readerUiState?.contextMenuVisible ?: false,
                                    onDismissRequest = {
                                        viewModel?.onContextMenuVisibilityChanged()
                                    }
                                ) {
                                    DropdownMenuItem(
                                        onClick = {},
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(end = 8.dp)
                                            ) {
                                                Checkbox(checked = readerUiState?.verticalReader
                                                    ?: false,
                                                    onCheckedChange = { viewModel?.verticalReaderToggle() }
                                                )
                                                Text(text = "Vertical Reader")
                                            }
                                        }
                                    )
                                }
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
                        if (readerUiState?.previousChapter != null) {
                            NavigationBarItem(
                                selected = false,
                                icon = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = null
                                    )
                                },
                                label = { Text(text = "Previous chapter") },
                                onClick = {
                                    viewModel.previousChapter()
                                }
                            )
                        }
                        if (readerUiState?.nextChapter != null) {
                            NavigationBarItem(
                                selected = false,
                                icon = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null
                                    )
                                },
                                label = { Text(text = "Next chapter") },
                                onClick = {
                                    viewModel.nextChapter()
                                }
                            )
                        }
                    }
                }
            )
        }) {
        val pagerState = rememberPagerState(pageCount = {
            readerUiState?.chapterImages?.size ?: 0
        })

        LaunchedEffect(pagerState.currentPage) {
            zoomState.reset()
            viewModel?.hideToolbar()
        }

        LaunchedEffect(readerUiState?.verticalReader) {
            zoomState.reset()
            //jump to the first page
            pagerState.scrollToPage(0)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            if (readerUiState!!.verticalReader) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .zoomable(
                            zoomState = zoomState,
                            onTap = {
                                viewModel.onToolbarVisibilityChanged()
                            },
                            onDoubleTap = { position ->
                                zoomState.toggleScale(
                                    readerUiState.targetScale,
                                    position
                                )
                            }
                        ),
                    verticalArrangement = Arrangement.Center,
                ) {
                    //show images vertically
                    readerUiState.chapterImages.forEach { imageUrl ->
                        AsyncImage(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            model = imageUrl,
                            contentDescription = null,
                        )
                    }
                }
            } else {

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                ) { page ->
                    readerUiState.chapterImages.get(page).let { imageUrl ->
                        AsyncImage(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .zoomable(
                                    zoomState = zoomState,
                                    onTap = {
                                        viewModel.onToolbarVisibilityChanged()
                                    },
                                    onDoubleTap = { position ->
                                        zoomState.toggleScale(
                                            readerUiState.targetScale,
                                            position
                                        )
                                    }
                                ),
                            model = imageUrl,
                            contentDescription = null,
                        )

                    }
                }
                Text(
                    text = "Page ${pagerState.currentPage + 1} / ${pagerState.pageCount}",
//                    text = if (readerUiState.previousChapter == null){"No previous chapters"} else {"${readerUiState.previousChapter?.chapterTitle}"}  + "/" +
//                            if (readerUiState.nextChapter == null){"No more chapters"} else {"${readerUiState.nextChapter?.chapterTitle}"},
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .clip(CircleShape)
                        .padding(8.dp),
                    color = Color.White,
                )
            }
        }
    }
}



