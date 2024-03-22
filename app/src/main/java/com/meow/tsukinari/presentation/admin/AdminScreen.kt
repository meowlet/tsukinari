package com.meow.tsukinari.presentation.admin

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun AdminScreen(
    adminViewModel: AdminViewModel = AdminViewModel()
) {
    Text(text = "Admin Screen")
}