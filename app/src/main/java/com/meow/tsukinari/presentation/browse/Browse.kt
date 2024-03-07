package com.meow.tsukinari.presentation.browse

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.meow.tsukinari.model.FictionModel
import com.meow.tsukinari.repository.Resources

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
    browseViewModel: BrowseViewModel? = null,
    onNavToDetailPage: (id: String) -> Unit
) {


//    val browseUiState = browseViewModel?.browseUiState ?: BrowseUiState()
//    LaunchedEffect(key1 = Unit) {
//        browseViewModel?.loadFictions()
//    }


    val context = LocalContext.current
    val browseUiState = browseViewModel?.browseUiState ?: BrowseUiState()

    // Load fictions on first launch
    LaunchedEffect(key1 = Unit) {
        browseViewModel?.loadFictions()
    }

    Scaffold(

        topBar = {
            TopAppBar(title = {
                if (browseUiState.isSearching) {
                    TextField(
                        textStyle = MaterialTheme.typography.bodyLarge,
                        singleLine = true,
                        placeholder = { Text(text = "Search here...") },
                        value = browseUiState.searchValue,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { browseViewModel?.changeSearchingState() }),

                        onValueChange = {
                            browseViewModel?.onSearchValueChange(it)
                        },
                        modifier = Modifier
                            .padding(bottom = 3.dp)
                    )
                } else {
                    Column {
                        Text(
                            text = "Meow",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Welcome, Meow-sama!",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            },
                navigationIcon = {
                    if (browseUiState.isSearching) {
                        IconButton(onClick = {
                            browseViewModel?.changeSearchingState()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = ""
                            )
                        }
                    }
                },
                actions = {
                    if (browseUiState.isSearching) {
                        IconButton(onClick = { browseViewModel?.clearSearchValue() }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = ""
                            )
                        }
                    } else IconButton(onClick = { browseViewModel?.changeSearchingState() }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = ""
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = "")
                    }
                    IconButton(onClick = {
                        browseViewModel?.signOut()
                    }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = ""
                        )
                    }
                }
            )

        }

    ) {
        Surface(Modifier.padding(it)) {
            when (browseUiState.fictionsList) {
                is Resources.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(align = Alignment.Center)
                    )
                }

                is Resources.Success -> {
                    LazyVerticalGrid(
                        modifier = Modifier.padding(start = 18.dp, end = 18.dp),
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = browseUiState.fictionsList.data ?: emptyList(),
                            key = { fiction ->
                                fiction.fictionId
                            }

                        ) { fiction ->
                            FictionItem(fiction = fiction, onClick = {
                                onNavToDetailPage.invoke(fiction.fictionId)
                            })
                        }
                    }
                }

                else -> {
                    Text(
                        text = browseUiState.fictionsList.throwable?.localizedMessage
                            ?: "Unknown Error",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }


}


@Composable
fun FictionItem(fiction: FictionModel, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onClick.invoke()
            }) {
        Image(
            painter = rememberAsyncImagePainter(
                fiction.coverLink
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            Brush.verticalGradient(
                                0.5f to Color.Black.copy(alpha = 0F),
                                1F to Color.Black.copy(alpha = 0.5F)
                            )
                        )
                    }
                },
        )
        Column(
            modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(bottom = 4.dp, top = 3.dp, start = 6.dp, end = 6.dp)
            ) {
                Text(
                    text = "OnGoing", //Fiction status
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Text(
                modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 4.dp),
                text = fiction.title, //Fiction name
                color = Color.White,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
