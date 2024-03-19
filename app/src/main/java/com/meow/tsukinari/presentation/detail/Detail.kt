package com.meow.tsukinari.presentation.detail

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.meow.tsukinari.model.ChapterModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    detailViewModel: DetailViewModel? = null,
    fictionId: String,
    onNavigate: () -> Unit,
    onNavToReader: (fictionId: String) -> Unit,
    onNavToProfile: (userId: String) -> Unit
) {
    val detailUiState = detailViewModel?.detailUiState ?: DetailUiState()
    LaunchedEffect(key1 = Unit) {
        detailViewModel?.getUserList()
        detailViewModel?.getFiction(fictionId)
        detailViewModel?.getCommentList(fictionId)
        detailViewModel?.getChapterList(fictionId)

    }

    LaunchedEffect(key1 = detailViewModel?.hasComments()) {
        if (detailUiState.commentList.isNotEmpty()) {
        }
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
                    IconButton(
                        //navigate back
                        onClick = { onNavigate.invoke() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = ""
                        )
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
                                text = buildAnnotatedString {
                                    append("Author: ")
                                    withStyle(
                                        SpanStyle(
                                            color = MaterialTheme.colorScheme.primary,
                                            textDecoration = TextDecoration.Underline
                                        )
                                    ) {
                                        append(detailUiState.uploader)
                                    }
                                }

                            )
                            Spacer(modifier = Modifier.size(8.dp))
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
            item {
                Text(
                    text = "Chapters List:",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 24.dp,
                        bottom = 4.dp
                    )
                )
            }
            detailUiState.chapters.forEachIndexed { index, chapter ->
                item {
                    ChapterItem(
                        chapter.chapterTitle,
                        chapter.chapterNumber,
                        detailViewModel!!.getTime(chapter.uploadedAt)
                    ) {
                        onNavToReader.invoke(chapter.chapterId)
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 24.dp,
                            bottom = 4.dp
                        )
                ) {
                    Text(
                        text = "Comments:",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        textDecoration = TextDecoration.Underline,

                        )
                    if (detailViewModel!!.hasUser) {
                        //comment input
                        TextField(value = detailUiState.comment, onValueChange = {
                            detailViewModel.onCommentChanged(it)
                        }, label = { Text("Write a comment") }, modifier = Modifier.fillMaxWidth())
                        //error message
                        if (detailUiState.commentFieldError.isNotEmpty()) {
                            Text(
                                text = detailUiState.commentFieldError,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        //submit button
                        IconButton(onClick = {
                            detailViewModel.addComment(fictionId, detailViewModel.userId)
                        }) {
                            if (detailUiState.isCommentLoading) {
                                CircularProgressIndicator()
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = ""
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Please login to comment",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            if (detailUiState.commentList.isNotEmpty()) {
                detailUiState.commentList.forEachIndexed { index, comment ->
                    if (detailUiState.commentUserList.isNotEmpty()) {
                        item {
                            CommentItem(
                                detailUiState.commentUserList[index].displayName,
                                detailUiState.commentUserList[index].userName,
                                detailUiState.commentUserList[index].profileImageUrl,
                                comment.comment,
                                detailViewModel!!.getTime(comment.commentTime)
                            ) {
                                onNavToProfile.invoke(detailUiState.commentUserList[index].userId)
                            }
                        }
                    }

                }
            }
        }

    }
}

@Composable
fun CommentItem(
    displayName: String,
    userName: String,
    avatarUrl: String,
    comment: String,
    time: String,
    onNavToProfile: (userId: String) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .fillMaxWidth()
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .clickable {
                    //navigate to user profile
                    onNavToProfile.invoke(userName)
                }
        )
        Column(
            modifier = Modifier

        ) {
            Text(
                text = "$displayName: (@$userName)",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.size(6.dp))
            Text(
                text = "$comment",
                style = MaterialTheme.typography.labelMedium
            )
            Text(text = "Commented at: $time", style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun ChapterList() {
    LazyColumn(content = {
        item {
            Text(
                text = "Chapters List:",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp)
            )
        }
        item {
            generateChapterList().forEach {
                ChapterItem(it.chapterTitle, 3, it.uploadedAt.toString()) {

                }
            }
        }
    })
}

@Preview
@Composable
fun CTLP() {
    ChapterList()
}


@Composable
fun ChapterItem(
    chapterName: String,
    chapterNum: Int,
    uploadedAt: String,
    navagateToChapter: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .fillMaxWidth()
            .clickable { navagateToChapter.invoke() }
    ) {
        Text(
            text = "Chapter $chapterNum: $chapterName",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            text = "Uploaded at: $uploadedAt",
            style = MaterialTheme.typography.labelMedium
        )
    }
}

//generate data for testing chapter list design
fun generateChapterList(): List<ChapterModel> {
    val list = mutableListOf<ChapterModel>()
    for (i in 1..10) {
        list.add(
            ChapterModel(
                chapterNumber = i,
                chapterTitle = "A very long name $i",
                uploadedAt = System.currentTimeMillis()
            )
        )
    }
    return list
}