package com.meow.tsukinari.presentation.editor

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun UpdatingScreen(
    editorViewModel: EditorViewModel? = null,
    fictionId: String,
    onNavToAddingChapterPage: (id: String) -> Unit,
    onNavigate: () -> Unit,
) {


    val context = LocalContext.current
    val singlePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null)
                editorViewModel?.onImageChange(uri) else {
            }
        }
    )
    val editorUiState = editorViewModel?.editorUiState ?: EditorUiState()
    val hasImage = editorUiState.imageUri != Uri.EMPTY
    LaunchedEffect(key1 = Unit) {
        editorViewModel?.getFiction(fictionId)
        editorViewModel?.resetImage()
    }


    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()



    Scaffold(
        floatingActionButton = {
            if (editorViewModel!!.isUpdatingFormFilled()) {
                FloatingActionButton(
                    onClick = {
                        editorViewModel.updateFiction(context, fictionId)
                    },
                ) {
                    if (editorUiState.isLoading)
                        CircularProgressIndicator()
                    else Icon(imageVector = Icons.Default.Check, contentDescription = "")
                }
            }
        }, snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { padding ->
        if (editorUiState.fictionUpdatedStatus) {
            scope.launch {
                editorViewModel?.resetChangedStatus()
                snackBarHostState.showSnackbar("Fiction updated successfully")
                onNavigate.invoke()
            }
        }
        if (editorUiState.fictionDeletedStatus) {
            scope.launch {
                editorViewModel?.resetChangedStatus()
                snackBarHostState.showSnackbar("Fiction deleted successfully")
                onNavigate.invoke()
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(start = 32.dp, end = 32.dp, top = padding.calculateTopPadding())
        ) {

            Text(
                text = "Update\nyour fiction",
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .fillMaxWidth()
            )

            OutlinedTextField(
                value = editorUiState.title,
                onValueChange = { editorViewModel?.onTitleChange(it) },
                label = {
                    Text(
                        text = "Title",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = editorUiState.description,
                onValueChange = { editorViewModel?.onDescriptionChange(it) },
                label = {
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(

                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(12.dp)
                    )
                    .aspectRatio(1.7f)
                    .padding(14.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .then(if (hasImage) Modifier else {
                        Modifier
                            .clickable {
                                singlePhotoPicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                    }
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(6.dp)
                        )

                    )) {
                if (!hasImage) {
                    AsyncImage(
                        model = editorUiState.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .drawWithCache {
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(
                                        Brush.verticalGradient(
                                            0f to Color.Black.copy(alpha = 0F),
                                            1F to Color.Black.copy(alpha = 0.8F)
                                        )
                                    )
                                }
                            },
                    )
                } else {
                    AsyncImage(
                        model = editorUiState.imageUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .drawWithCache {
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(
                                        Brush.verticalGradient(
                                            0f to Color.Black.copy(alpha = 0F),
                                            1F to Color.Black.copy(alpha = 0.8F)
                                        )
                                    )
                                }
                            },
                    )
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(6.dp)
                            .clickable { editorViewModel?.resetImage() }
                    )
                }


            }
            // check box for finished
            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = editorUiState.isFinished,
                    onCheckedChange = {
                        editorViewModel?.onFinishedChange(it)
                    })
                Text(text = "Finished", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { onNavToAddingChapterPage.invoke(fictionId) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Add Chapter")
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { editorViewModel?.deleteFiction(fictionId) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Delete")
            }
        }
    }


}

@Preview
@Composable
fun UpdateScreenPrev() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = true,
                onCheckedChange = {

                })
            Text(text = "Date", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

