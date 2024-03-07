package com.meow.tsukinari.presentation.editor.add_chapter

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChapterScreen(
    addChapterViewModel: AddChapterViewModel? = null,
    onNavigateUp: () -> Unit,
    fictionId: String
) {


    val addChapterUiState = addChapterViewModel?.addChapterUiState
    val selectedImages = addChapterViewModel?.selectedImages

    val context = LocalContext.current

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
            addChapterViewModel?.onImageSelected(it, context)
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Upload chapter")
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigateUp.invoke() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (addChapterViewModel!!.validateForm()) {
                FloatingActionButton(
                    onClick = { addChapterViewModel.uploadChapter(context, fictionId) },
                    content = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add"
                        )
                    }
                )
            }
        },

        ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (addChapterUiState!!.addingChapterError.isNotBlank()) {
                Text(
                    text = addChapterUiState.addingChapterError,
                    color = MaterialTheme.colorScheme.error,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row {
                OutlinedTextField(
                    value = addChapterUiState.chapterTitle,
                    onValueChange = { addChapterViewModel.onChapterTitleChange(it) },
                    label = {
                        Text(
                            text = "Chapter Title",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.weight(0.7f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                OutlinedTextField(
                    value = addChapterUiState.chapterIndex,
                    onValueChange = { addChapterViewModel.onChapterIndexChange(it) },
                    label = {
                        Text(
                            text = "Index",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(0.3f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            //preview the selected images
            Text(text = "Pages preview", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(5.dp))
            HorizontalDivider()


            if (selectedImages?.isNotEmpty() == true) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween

                ) {
                    Text(
                        text = "Reselect images",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            launcher.launch("image/*")
                        }
                    )
                    Text(
                        text = "Clear all",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable {
                            addChapterViewModel.clearSelectedImages()
                        }
                    )
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        count = selectedImages.size,
                    ) { index ->
                        PageItem(index = index, images = selectedImages)
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No images selected",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(text = "Select images")
                    }
                }

            }

        }

    }

}

@Composable
fun PageItem(index: Int, images: List<Uri> = emptyList()) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp)),
    ) {
        AsyncImage(
            model = images[index],
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize(),
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
                    text = "Page ${index + 1}", //Fiction status
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
