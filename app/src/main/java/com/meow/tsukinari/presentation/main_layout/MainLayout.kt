package com.meow.tsukinari.presentation.main_layout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.meow.tsukinari.core.Navigation
import com.meow.tsukinari.presentation.authentication.AuthViewModel
import com.meow.tsukinari.presentation.browse.BrowseViewModel
import com.meow.tsukinari.presentation.detail.DetailViewModel
import com.meow.tsukinari.presentation.editor.EditorViewModel
import com.meow.tsukinari.presentation.my_fictions.MyFictionsViewModel
import com.meow.tsukinari.presentation.profile.ProfileViewModel
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
    val focusManager = LocalFocusManager.current

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val mainLayoutUiState = mainLayoutViewModel.mainLayoutUiState
    val focusRequester = remember { FocusRequester() }
    mainLayoutViewModel.checkExclusive(currentDestination)




    TsukinariTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                bottomBar = {
                    if (!mainLayoutUiState.isExclusive) {
                        NavigationBar {
                            mainLayoutViewModel.getHomeNavItems().forEachIndexed { _, item ->
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
                topBar = {
                    if (!mainLayoutUiState.isExclusive) {

                        TopAppBar(title = {
                            if (mainLayoutUiState.isSearching) {
                                TextField(
                                    textStyle = MaterialTheme.typography.bodyLarge,
                                    singleLine = true,
                                    placeholder = { Text(text = "Search here...") },
                                    value = mainLayoutUiState.searchValue,
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent
                                    ),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = { mainLayoutViewModel.changeSearchingState() }),

                                    onValueChange = { mainLayoutViewModel.changeSearchValue(it) },
                                    modifier = Modifier
                                        .padding(bottom = 3.dp)
                                )
                            } else {
                                Column {
                                    Text(
                                        text = "Meow",
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "Welcome, Meow-sama!",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        },
                            navigationIcon = {
                                if (mainLayoutUiState.isSearching) {
                                    IconButton(onClick = { mainLayoutViewModel.changeSearchingState() }) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowBack,
                                            contentDescription = ""
                                        )
                                    }
                                }
                            },
                            actions = {
                                if (mainLayoutUiState.isSearching) {
                                    IconButton(onClick = { mainLayoutViewModel.clearSearchValue() }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = ""
                                        )
                                    }
                                } else IconButton(onClick = { mainLayoutViewModel.changeSearchingState() }) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = ""
                                    )
                                }
                                IconButton(onClick = { }) {
                                    Icon(imageVector = Icons.Default.List, contentDescription = "")
                                }
                                IconButton(onClick = { mainLayoutViewModel.signOut() }) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = ""
                                    )
                                }
                            }
                        )
                    }
                }
            ) { padding ->
                Surface(modifier = if (!mainLayoutUiState.isExclusive) Modifier.padding(padding) else Modifier) {
                    Navigation(
                        navController = navController,
                        authViewModel = authViewModel,
                        browseViewModel = browseViewModel,
                        editorViewModel = editorViewModel,
                        myFictionsViewModel = myFictionsViewModel,
                        detailViewModel = detailViewModel,
                        profileViewModel = profileViewModel
                    )
                }
            }
        }
    }
}


