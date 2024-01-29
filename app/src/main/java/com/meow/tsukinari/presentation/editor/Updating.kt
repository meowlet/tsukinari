package com.meow.tsukinari.presentation.editor

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import com.meow.tsukinari.ui.theme.TsukinariTheme

@Composable
fun UpdatingScreen(
    editorViewModel: EditorViewModel? = null,
    fictionId: String,
    onNavigate: () -> Unit,
) {
    val singlePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null)
                editorViewModel?.onImageChange(uri) else {
            }
        }
    )
    val managementUiState = editorViewModel?.editorUiState
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(start = 32.dp, end = 32.dp, top = 32.dp)
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
            value = managementUiState?.title ?: "",
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
            value = managementUiState?.description ?: "",
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
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(
                    RoundedCornerShape(12.dp)
                )
                .aspectRatio(1.7f)
                .fillMaxHeight()
                .padding(14.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                .clickable {
                    singlePhotoPicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
        ) {
            Text(
                text = "Click here to upload the cover image.upload the cover image.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = { editorViewModel?.updateFiction(fictionId) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Update")
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

@Preview(showSystemUi = true, wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE)
@Composable
fun UpdatingPrev() {
    TsukinariTheme {
        UpdatingScreen(fictionId = "nigga") {}
    }
}