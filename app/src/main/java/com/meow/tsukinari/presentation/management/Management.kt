package com.meow.tsukinari.presentation.management

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ManagementScreen(
    managementViewModel: ManagementViewModel
) {

    val managementUiState = managementViewModel.managementUiState


    Column {
        OutlinedTextField(
            value = managementUiState?.title ?: "",
            onValueChange = { managementViewModel.onTitleChange(it) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Info, contentDescription = ""
                )
            },
            label = {
                Text(
                    text = "Title",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = managementUiState?.description ?: "",
            onValueChange = { managementViewModel.onDescriptionChange(it) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Info, contentDescription = ""
                )
            },
            label = {
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = { managementViewModel.addNote() }) {
            Text(text = "Submit")
        }
    }

}