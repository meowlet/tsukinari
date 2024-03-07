package com.meow.tsukinari.presentation.editor.add_chapter

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage

@Composable
fun AddChapterScreen(
    addChapterViewModel: AddChapterViewModel? = null,
    fictionId: String
) {
    // center aligned surface
    Column(

        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,


        ) {
        val addChapterUiState = addChapterViewModel?.addChapterUiState
        val selectedImages = addChapterViewModel?.selectedImages

        val context = LocalContext.current

        val launcher =
            rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
                addChapterViewModel?.onImageSelected(it, context)
            }

        if (addChapterUiState?.isLoading == true) {
            CircularProgressIndicator()
        }
        if (addChapterUiState!!.addingChapterError.isNotBlank()) {
            Text(
                text = addChapterUiState.addingChapterError,
                color = MaterialTheme.colorScheme.error,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )
        }


        TextField(
            value = addChapterUiState.chapterTitle,
            onValueChange = { addChapterViewModel.onChapterTitleChange(it) },
            label = { Text("Chapter Title") }
        )

        // chapter index input
        TextField(
            value = addChapterUiState.chapterIndex,
            onValueChange = { addChapterViewModel.onChapterIndexChange(it) },
            label = { Text("Chapter Index") }
        )

        // button trigger image picker
        Button(onClick = { launcher.launch("image/*") }) {
            Text("Add Images")
        }

        Button(onClick = { addChapterViewModel.uploadChapter(context, fictionId) }) {
            Text("Upload")
        }

        // show selected images
        selectedImages?.forEach {
            AsyncImage(model = it, contentDescription = "chapter page")
        }


    }
}