package com.meow.tsukinari.presentation.browse

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
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

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun BrowseScreen(
    browseViewModel: BrowseViewModel? = null,
    onNavToDetailPage: (id: String) -> Unit
) {


//    val browseUiState = browseViewModel?.browseUiState ?: BrowseUiState()
//    LaunchedEffect(key1 = Unit) {
//        browseViewModel?.loadFictions()
//    }


    val sheetState = rememberModalBottomSheetState()
    val pagerState = rememberPagerState(pageCount = {
        2
    })

    //scrollState
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val browseUiState = browseViewModel?.browseUiState ?: BrowseUiState()

    //pull refresh state
    val pullToRefreshState = rememberPullRefreshState(
        //resource loading
        refreshing = browseUiState.isRefreshing,
        onRefresh = {
            //chage the refreshing state
            browseViewModel?.startRefreshing()
            //reset the sort and filter if the current sort option is view or like
            if (browseUiState.sortBy == 3 || browseUiState.sortBy == 4) {
                browseViewModel?.resetSortAndFilter()
            }
            browseViewModel?.loadFictions()
        })


    // Load fictions on first launch
    LaunchedEffect(key1 = Unit) {
        browseViewModel?.getUserList()
        //load fictions only in the first launch
        //if the fictions are already loaded, do not load them again
        if (browseUiState.fictionsList.data.isNullOrEmpty()) {
            browseViewModel?.loadFictions()
        }
        pagerState.animateScrollToPage(browseUiState.selectedTab)
    }

    //fetch view and like list once the fictions are loaded
    LaunchedEffect(key1 = browseUiState.fictionsList) {
        if (browseUiState.fictionsList is Resources.Success) {
            browseViewModel?.getTotalViews()
        }
        if (browseUiState.fictionsList is Resources.Success) {
            browseViewModel?.getTotalLikes()
        }
    }





    LaunchedEffect(key1 = browseUiState.selectedTab) {
        pagerState.animateScrollToPage(browseUiState.selectedTab)
    }

    LaunchedEffect(key1 = pagerState.currentPage) {
        browseViewModel?.changeTab(pagerState.currentPage)
    }

    Scaffold(

        topBar = {
            TopAppBar(
                title = {
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
                                text = "月落聲",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Welcome, Tsukinarian-sama!",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (browseUiState.isSearching) {
                        IconButton(onClick = {
                            browseViewModel?.changeSearchingState()
                            browseViewModel?.clearSearchValue()
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
                    IconButton(onClick = {
                        browseViewModel?.showBottomSheet()
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = "")
                    }
                    IconButton(onClick = {
                        browseViewModel?.signOut(context)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = ""
                        )
                    }
                },
            )

        },

        ) {


        if (browseUiState.showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    browseViewModel?.hideBottomSheet()
                },
                sheetState = sheetState,
            ) {
                //show all sort options using radio buttons
                TabRow(
                    selectedTabIndex = browseUiState.selectedTab,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = browseUiState.selectedTab == 0,
                        onClick = { browseViewModel?.changeTab(0) }) {
                        Text(text = "Sort by", modifier = Modifier.padding(8.dp))
                    }
                    Tab(
                        selected = browseUiState.selectedTab == 1,
                        onClick = { browseViewModel?.changeTab(1) }) {
                        Text(text = "Filtered by", modifier = Modifier.padding(8.dp))
                    }

                }
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(0.5f)
                ) { page ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when (page) {
                            0 -> {
                                Row(
                                    modifier = Modifier.padding(top = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = browseUiState.sortBy == 0,
                                        onCheckedChange = {
                                            browseViewModel?.changeSortBy(0)
                                        })
                                    Text(text = "Date", style = MaterialTheme.typography.bodyMedium)
                                }
                                Row(
                                    modifier = Modifier.padding(top = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = browseUiState.sortBy == 1,
                                        onCheckedChange = {
                                            browseViewModel?.changeSortBy(1)
                                        })
                                    Text(text = "Name", style = MaterialTheme.typography.bodyMedium)
                                }
                                Row(
                                    modifier = Modifier.padding(top = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = browseUiState.sortBy == 2,
                                        onCheckedChange = {
                                            browseViewModel?.changeSortBy(2)
                                        })
                                    Text(
                                        text = "Uploader",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Row(
                                    modifier = Modifier.padding(top = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = browseUiState.sortBy == 3,
                                        onCheckedChange = {
                                            browseViewModel?.changeSortBy(3)
                                        })
                                    Text(
                                        text = "Views",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Row(
                                    modifier = Modifier.padding(top = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = browseUiState.sortBy == 4,
                                        onCheckedChange = {
                                            browseViewModel?.changeSortBy(4)
                                        })
                                    Text(
                                        text = "Likes",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            1 -> {
                                Row(
                                    modifier = Modifier.padding(top = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = browseUiState.filterBy == 0,
                                        onCheckedChange = {
                                            browseViewModel?.changeFilterBy(0)
                                        })
                                    Text(
                                        text = "Verified",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Row(
                                    modifier = Modifier.padding(top = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = browseUiState.filterBy == 1,
                                        onCheckedChange = {
                                            browseViewModel?.changeFilterBy(1)
                                        })
                                    Text(
                                        text = "Unverified",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Row(
                                    modifier = Modifier.padding(top = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = browseUiState.filterBy == 2,
                                        onCheckedChange = {
                                            browseViewModel?.changeFilterBy(2)
                                        })
                                    Text(
                                        text = "Finished",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Row(
                                    modifier = Modifier.padding(top = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = browseUiState.filterBy == 3,
                                        onCheckedChange = {
                                            browseViewModel?.changeFilterBy(3)
                                        })
                                    Text(
                                        text = "Ongoing",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }



        Box(
            Modifier
                .padding(it)
        ) {
            when (browseUiState.fictionsList) {
                is Resources.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(align = Alignment.Center)
                    )
                }

                is Resources.Success -> {


                    val filteredFictionList = when (browseUiState.filterBy) {
                        0 -> browseUiState.fictionsList.data?.filter { it.verified }
                        1 -> browseUiState.fictionsList.data?.filter { !it.verified }
                        2 -> browseUiState.fictionsList.data?.filter { it.finished }
                        3 -> browseUiState.fictionsList.data?.filter { !it.finished }
                        else -> browseUiState.fictionsList.data
                    } ?: emptyList()


                    val finalFictionList = when (browseUiState.sortBy) {
                        0 -> filteredFictionList.sortedBy { it.uploadedAt }
                        1 -> filteredFictionList.sortedBy { it.title }
                        //sort by the uploader name in the user list by index
                        2 -> filteredFictionList.sortedBy { fiction ->
                            browseViewModel?.userList?.find { user ->
                                user.userId == fiction.uploaderId
                            }?.userName
                        }

                        3 -> filteredFictionList.sortedBy {
                            browseViewModel?.viewList?.find { view ->
                                view.first == it.fictionId
                            }?.second
                        }.reversed()

                        4 -> filteredFictionList.sortedBy {
                            browseViewModel?.likeList?.find { like ->
                                like.first == it.fictionId
                            }?.second
                        }.reversed()

                        else -> filteredFictionList
                    }


                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 18.dp, end = 18.dp)
                            .pullRefresh(pullToRefreshState),
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = finalFictionList,
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
            PullRefreshIndicator(
                refreshing = browseUiState.isRefreshing,
                state = pullToRefreshState,
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }


}


@Composable
fun FictionItem(fiction: FictionModel, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 5.dp)
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
                    text = if (fiction.finished) "Finished" else "Ongoing", //Fiction status
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
