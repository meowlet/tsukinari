package com.meow.tsukinari.presentation.my_fictions

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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.meow.tsukinari.model.FictionModel
import com.meow.tsukinari.repository.Resources
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun getTime(uploadedAt: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
    return sdf.format(Date(uploadedAt))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyFictionsScreen(
    myFictionsViewModel: MyFictionsViewModel? = null,
    onNavToSignInPage: () -> Unit,
    onNavToUpdatingPage: (id: String) -> Unit,
    onNavToAddingPage: () -> Unit,
) {


    var myFictionsUiState = myFictionsViewModel?.myFictionsUiState ?: MyFictionsUiState()

    if (myFictionsViewModel?.hasUser != true) onNavToSignInPage.invoke()

    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val scaffoldState = rememberBottomSheetScaffoldState()

    LaunchedEffect(key1 = Unit) {
        myFictionsViewModel?.loadFictions()
    }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavToAddingPage.invoke() }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                )
            }
        },
        topBar = {
            TopAppBar(
                title = { "My Fictions" },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "")
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Default.List, contentDescription = "")
                    }
                    IconButton(onClick = {
                        myFictionsViewModel?.signOut()
                        onNavToSignInPage.invoke()
                    }) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { padding ->

        when (myFictionsUiState.fictionsList) {
            is Resources.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(align = Alignment.Center)
                )
            }

            is Resources.Success -> {
                LazyColumn(
                    modifier = Modifier.padding(padding)
                ) {
                    items(
                        myFictionsUiState.fictionsList.data ?: emptyList()
                    ) { fiction ->
                        FictionItem(
                            fiction = fiction,
                            onClick = {
                                onNavToUpdatingPage.invoke(fiction.fictionId)
                            }
                        )
                    }
                }
            }

            else -> {
                Text(
                    text = myFictionsUiState
                        .fictionsList.throwable?.localizedMessage ?: "Unknown Error",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FictionItem(
    fiction: FictionModel,
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
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
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
                    text = "${getTime(fiction.uploadedAt)}",
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


