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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.meow.tsukinari.model.ChapterModel
import com.meow.tsukinari.model.FictionCommentModel
import com.meow.tsukinari.model.UserModel

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
        detailViewModel?.getFictionStats(fictionId)
        detailViewModel?.getFiction(fictionId)
        detailViewModel?.getCommentList(fictionId)
        detailViewModel?.getChapterList(fictionId)

    }

    LaunchedEffect(key1 = detailViewModel?.hasComments()) {
        if (detailUiState.commentList.isNotEmpty()) {
        }
    }

    val context = LocalContext.current

//    LaunchedEffect(key1 = detailUiState.doesUserDislike, key2 = detailUiState.doesUserLike) {
//        detailViewModel?.getFictionStats(fictionId)
//    }


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
                            Row {
                                Text(text = "Author: ")
                                Text(
                                    text = detailUiState.uploader,
                                    color = MaterialTheme.colorScheme.primary,
                                    textDecoration = TextDecoration.Underline,

                                    modifier = Modifier.clickable {
                                        onNavToProfile.invoke(detailUiState.uploaderId)
                                    }

                                )
                            }
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
                            imageVector = if (detailUiState.doesUserLike) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                            contentDescription = null,
                            modifier = Modifier
                                .size(28.dp)
                                .padding(2.dp)
                                .clickable {
                                    detailViewModel!!.votingAction(context, fictionId, true)
                                }
                        )
                        Text(
                            text = detailUiState.likeCount.toString(),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (detailUiState.doesUserDislike) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                            contentDescription = null,
                            modifier = Modifier
                                .size(28.dp)
                                .scale(scaleX = -1f, scaleY = -1f)
                                .padding(2.dp)
                                .clickable {
                                    detailViewModel!!.votingAction(context, fictionId, false)
                                }
                        )
                        Text(
                            text = detailUiState.dislikeCount.toString(),
                            style = MaterialTheme.typography.labelLarge
                        )
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

            if (detailUiState.chapters.isEmpty()) {
                item {
                    Text(
                        text = "This fiction has no chapters yet.",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(20.dp)
                    )
                }
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

                    if (detailViewModel!!.hasUser) {
                        //comment input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(value = detailUiState.comment, onValueChange = {
                                detailViewModel.onCommentChanged(it)
                            }, label = { Text("Write a comment") }, modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.85f)
                            )
                            //error message
                            if (detailUiState.commentFieldError.isNotEmpty()) {
                                Text(
                                    text = detailUiState.commentFieldError,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            //submit button
                            IconButton(
                                onClick = {
                                    detailViewModel.addComment(fictionId, detailViewModel.userId)
                                }, modifier = Modifier
                                    .weight(0.15f)
                                    .clip(CircleShape)
                                    .padding(4.dp),
                                colors = IconButtonColors(
                                    contentColor = MaterialTheme.colorScheme.secondary,
                                    disabledContentColor = MaterialTheme.colorScheme.secondary.copy(
                                        alpha = 0.5f
                                    ),
                                    containerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent
                                )
                            ) {
                                if (detailUiState.isCommentLoading) {
                                    CircularProgressIndicator()
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Done,
                                        contentDescription = ""
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Comments:",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.secondary,
                            textDecoration = TextDecoration.Underline,

                            )
                    } else {
                        Text(
                            text = "Please login to comment",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            if (detailUiState.commentList.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No comments yet.",
                            style = MaterialTheme.typography.labelLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
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
            .fillMaxWidth(),
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .weight(0.15f)
                .padding(4.dp)
                .clip(CircleShape)
                .aspectRatio(1f)
                .clickable {
                    //navigate to user profile
                    onNavToProfile.invoke("")
                }
        )
        Spacer(modifier = Modifier.size(4.dp))
        Column(
            modifier = Modifier
                .weight(0.85f)
                .padding(top = 2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = "$displayName",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text(
                    text = time, style = MaterialTheme.typography.labelSmall,
                    textDecoration = TextDecoration.Underline
                )
            }
            //onsurface colored box
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = MaterialTheme.colorScheme.surfaceContainer
            ) {
                Text(
                    text = comment,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(10.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 10,
                    overflow = TextOverflow.Ellipsis
                )
            }

        }
    }
}

@Preview
@Composable
fun CommentSectionTest() {
    //dummy data
    val commentList = mutableListOf<FictionCommentModel>()

    for (i in 1..10) {
        commentList.add(
            FictionCommentModel(
                commentId = "commentId",
                comment = "A very long comment" +
                        "twalsdfjklaksjdf" +
                        "asdfflkjalsdjf" +
                        "asldfkjlaskjdf $i",
                commentTime = System.currentTimeMillis(),
                userId = "userId"
            )
        )
    }

    val userList = mutableListOf<UserModel>()

    for (i in 1..10) {
        userList.add(
            UserModel(
                userId = "userId",
                userName = "userName",
                displayName = "Display Name",
                profileImageUrl = "profileImageUrl"
            )
        )
    }
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            commentList.forEachIndexed { index, comment ->
                CommentItem(
                    userList[index].displayName,
                    userList[index].userName,
                    userList[index].profileImageUrl,
                    comment.comment,
                    "time"
                ) {
                }
            }

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