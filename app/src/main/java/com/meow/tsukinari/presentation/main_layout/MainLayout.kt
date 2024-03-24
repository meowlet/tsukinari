package com.meow.tsukinari.presentation.main_layout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.meow.tsukinari.core.Navigation
import com.meow.tsukinari.presentation.admin.AdminViewModel
import com.meow.tsukinari.presentation.admin.user_page.UserPageViewModel
import com.meow.tsukinari.presentation.authentication.AuthViewModel
import com.meow.tsukinari.presentation.browse.BrowseViewModel
import com.meow.tsukinari.presentation.detail.DetailViewModel
import com.meow.tsukinari.presentation.editor.EditorViewModel
import com.meow.tsukinari.presentation.editor.add_chapter.AddChapterViewModel
import com.meow.tsukinari.presentation.my_fictions.MyFictionsViewModel
import com.meow.tsukinari.presentation.profile.ProfileViewModel
import com.meow.tsukinari.presentation.reader.ReaderViewModel
import com.meow.tsukinari.presentation.user_profile.UserProfileViewModel
import com.meow.tsukinari.ui.theme.TsukinariTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(mainLayoutViewModel: MainLayoutViewModel) {
    val authViewModel = viewModel(modelClass = AuthViewModel::class.java)
    val editorViewModel = viewModel(modelClass = EditorViewModel::class.java)
    val myFictionsViewModel = viewModel(modelClass = MyFictionsViewModel::class.java)
    val browseViewModel = viewModel(modelClass = BrowseViewModel::class.java)
    val detailViewModel = viewModel(modelClass = DetailViewModel::class.java)
    val profileViewModel = viewModel(modelClass = ProfileViewModel::class.java)
    val addChapterViewModel = viewModel(modelClass = AddChapterViewModel::class.java)
    val readerViewModel = viewModel(modelClass = ReaderViewModel::class.java)
    val userProfileViewModel = viewModel(modelClass = UserProfileViewModel::class.java)
    val adminViewModel = viewModel(modelClass = AdminViewModel::class.java)
    val userPageViewModel = viewModel(modelClass = UserPageViewModel::class.java)
    val focusManager = LocalFocusManager.current

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val mainLayoutUiState = mainLayoutViewModel.mainLayoutUiState
    val focusRequester = remember { FocusRequester() }
    mainLayoutViewModel.checkExclusive(currentDestination)

    LaunchedEffect(Unit) {
        mainLayoutViewModel.isAdmin()
    }




    TsukinariTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                bottomBar = {
                    AnimatedVisibility(
                        visible = !mainLayoutUiState.isExclusive,
                        enter = slideInVertically(initialOffsetY = { it }),
                        exit = slideOutVertically(targetOffsetY = { it }),
                        content = {
                            NavigationBar {
                                if (!mainLayoutUiState.isAdmin) {
                                    mainLayoutViewModel.getHomeNavItems().forEach { item ->
                                        NavigationBarItem(
                                            icon = {
                                                Icon(
                                                    imageVector = item.icon,
                                                    contentDescription = item.route
                                                )
                                            },
                                            label = { Text(text = item.title) },
                                            selected = mainLayoutViewModel.isNavItemSelected(
                                                currentDestination,
                                                item
                                            ),
                                            onClick = {
                                                navController.navigate(item.route) {
                                                    popUpTo(navController.graph.startDestinationId) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        )
                                    }
                                } else {
                                    mainLayoutViewModel.getAdminNavItems().forEach { item ->
                                        NavigationBarItem(
                                            icon = {
                                                Icon(
                                                    imageVector = item.icon,
                                                    contentDescription = item.route
                                                )
                                            },
                                            label = { Text(text = item.title) },
                                            selected = mainLayoutViewModel.isNavItemSelected(
                                                currentDestination,
                                                item
                                            ),
                                            onClick = {
                                                navController.navigate(item.route) {
                                                    popUpTo(navController.graph.startDestinationId) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        })
                },
            ) { padding ->
                Surface(modifier = if (!mainLayoutUiState.isExclusive) Modifier.padding(bottom = padding.calculateBottomPadding()) else Modifier) {
                    Navigation(
                        navController = navController,
                        authViewModel = authViewModel,
                        browseViewModel = browseViewModel,
                        editorViewModel = editorViewModel,
                        myFictionsViewModel = myFictionsViewModel,
                        detailViewModel = detailViewModel,
                        profileViewModel = profileViewModel,
                        addChapterViewModel = addChapterViewModel,
                        readerViewModel = readerViewModel,
                        userProfileViewModel = userProfileViewModel,
                        adminViewModel = adminViewModel,
                        userPageViewModel = userPageViewModel,
                    )
                }
            }
        }
    }
}


