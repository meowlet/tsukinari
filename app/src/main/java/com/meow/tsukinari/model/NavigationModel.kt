package com.meow.tsukinari.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class HomeNav(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null
) {
    data object Browse : HomeNav(
        route = "browse",
        title = "Browse",
        icon = Icons.Filled.Home,
        hasNews = false
    )

    data object Profile : HomeNav(
        route = "profile",
        title = "Profile",
        icon = Icons.Filled.Person,
        hasNews = true
    )
}

sealed class ExclusiveNav(
    val route: String,
    val title: String,
) {
    data object MyFictions : ExclusiveNav(
        route = "my-fictions",
        title = "My Fictions",
    )

    object FictionDetail : ExclusiveNav(
        route = "explore",
        title = "Explore",
    )

    object Upload : ExclusiveNav(
        route = "upload",
        title = "Upload fictions",
    )

    object Update : ExclusiveNav(
        route = "update",
        title = "Update fictions",
    )

    object Detail : ExclusiveNav(
        route = "detail",
        title = "Fiction detail",
    )
}

sealed class NestedNav(
    val route: String,
    val title: String,
) {
    data object Authorization : NestedNav(
        route = "auth",
        title = "Authorization",
    )

    object Main : NestedNav(
        route = "main",
        title = "Main",
    )
}