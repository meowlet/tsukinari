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

sealed class AdminNav(
    val route: String,
    val title: String,
) {
    data object UserPage : AdminNav(
        route = "user-page",
        title = "User page",
    )

    data object FictionPage : AdminNav(
        route = "fiction-page",
        title = "Fiction page",
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

    //user profile
    object UserProfile : ExclusiveNav(
        route = "user-profile",
        title = "Profile",
    )

    data object Setup : ExclusiveNav(
        route = "setup",
        title = "Setup",
    )


    object AddChapter : ExclusiveNav(
        route = "add-chapter",
        title = "Add chapter",
    )

    object Reader : ExclusiveNav(
        route = "reader",
        title = "Reader",
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