package com.meow.tsukinari.presentation.editor

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.meow.tsukinari.ui.theme.TsukinariTheme
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddingScreen(
    editorViewModel: EditorViewModel? = null
) {


    val editorUiState = editorViewModel?.editorUiState ?: EditorUiState()


    LaunchedEffect(key1 = Unit) {
        editorViewModel?.resetState()
    }

    val isFormFilled = editorUiState.title.isNotBlank() &&
            editorUiState.description.isNotBlank() && editorUiState.imageUri != Uri.EMPTY

    val hasImage = editorUiState.imageUri != Uri.EMPTY

    val singlePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null)
                editorViewModel?.onImageChange(uri) else {
            }
        }
    )


    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }


    Scaffold(
        floatingActionButton = {
            if (isFormFilled) {
                FloatingActionButton(
                    onClick = {
                        editorViewModel?.addNote()
                    },
                ) {
                    if (editorUiState.isLoading)
                        CircularProgressIndicator()
                    else Icon(imageVector = Icons.Default.Check, contentDescription = "")
                }
            }
        }, snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { padding ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(start = 32.dp, end = 32.dp, top = padding.calculateTopPadding())
                .verticalScroll(rememberScrollState())
        ) {
            if (editorUiState.fictionAddedStatus) {
                scope.launch {
                    snackBarHostState.showSnackbar("Added Note Successfully")
                    editorViewModel?.resetNoteChangedStatus()
//                    onNavigate.invoke()
                }
            }
            Text(
                text = "Contribute\nyour fiction",
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

                contentAlignment = if (hasImage) {
                    Alignment.BottomEnd
                } else Alignment.Center,
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(12.dp)
                    )
                    .aspectRatio(1.7f)
                    .padding(14.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .then(if (hasImage) Modifier else {
                        Modifier
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
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
                if (hasImage) {
                    AsyncImage(
                        model = editorUiState.imageUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
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
                        imageVector = Icons.Default.Clear,
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(6.dp)
                            .clickable { editorViewModel?.resetImage() }
                    )
                } else
                    Text(
                        text = "Upload image",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

    }


}

@Preview(showSystemUi = true, wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE)
@Composable
fun AddingPrev() {
    TsukinariTheme {
        AddingScreen()
    }
}