package com.meow.tsukinari.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

sealed class HomeNav(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null
) {
    object Browse : HomeNav(
        route = "browse",
        title = "Browse",
        icon = Icons.Filled.Home,
        hasNews = false
    )

    object More : HomeNav(
        route = "explore",
        title = "Explore",
        icon = Icons.Filled.List,
        hasNews = true
    )

}