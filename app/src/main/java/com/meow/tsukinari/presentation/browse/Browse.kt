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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.meow.tsukinari.model.FictionModel

@Composable
fun BrowseScreen(
    browseViewModel: BrowseViewModel? = null,
    onNavToMyFictions: () -> Unit
) {

    val browseUiState = browseViewModel?.browseUiState ?: BrowseUiState()
    LaunchedEffect(key1 = Unit) {
        browseViewModel?.loadFictions()
    }

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
            FictionItem(fiction = fiction)
        }
    }
}


@Composable
fun FictionItem(fiction: FictionModel) {
    Box(modifier = Modifier
        .fillMaxSize()
        .height(200.dp)
        .clip(RoundedCornerShape(8.dp))
        .clickable { }) {
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
