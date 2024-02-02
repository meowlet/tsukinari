package com.meow.tsukinari.presentation.main_layout

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.meow.tsukinari.core.Navigation
import com.meow.tsukinari.presentation.authentication.AuthViewModel
import com.meow.tsukinari.presentation.browse.BrowseViewModel
import com.meow.tsukinari.presentation.editor.EditorViewModel
import com.meow.tsukinari.presentation.my_fictions.MyFictionsViewModel
import com.meow.tsukinari.ui.theme.TsukinariTheme

@Composable
fun MainLayout(mainLayoutViewModel: MainLayoutViewModel) {
    val authViewModel = viewModel(modelClass = AuthViewModel::class.java)
    val editorViewModel = viewModel(modelClass = EditorViewModel::class.java)
    val myFictionsViewModel = viewModel(modelClass = MyFictionsViewModel::class.java)
    val browseViewModel = viewModel(modelClass = BrowseViewModel::class.java)

    var navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val mainLayoutUiState = mainLayoutViewModel.mainLayoutUiState



    TsukinariTheme {

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                bottomBar = {
                    mainLayoutViewModel.checkExclusive(currentDestination)
                    if (!mainLayoutUiState.isExclisive) {
                        NavigationBar {
                            mainLayoutViewModel.getHomeNavItems().forEachIndexed { index, item ->
                                NavigationBarItem(
                                    selected = mainLayoutViewModel.isNavItemSelected(
                                        currentDestination,
                                        item
                                    ),
                                    label = {
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    },
                                    onClick = {
                                        navController.navigate(item.route)
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = null
                                        )
                                    }
                                )
                            }
                        }
                    }
                },
                topBar = {}
            ) { padding ->
                Surface(modifier = Modifier.padding(padding)) {
                    Navigation(
                        navController = navController,
                        authViewModel = authViewModel,
                        browseViewModel = browseViewModel,
                        editorViewModel = editorViewModel,
                        myFictionsViewModel = myFictionsViewModel
                    )
                }
            }
        }
    }
}


