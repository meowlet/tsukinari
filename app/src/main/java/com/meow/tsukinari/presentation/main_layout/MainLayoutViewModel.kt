package com.meow.tsukinari.presentation.main_layout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.meow.tsukinari.model.HomeNav
import com.meow.tsukinari.repository.DatabaseRepository
import kotlinx.coroutines.launch


class MainLayoutViewModel(
    private val repository: DatabaseRepository = DatabaseRepository()
) : ViewModel() {


    var mainLayoutUiState by mutableStateOf(MainLayoutUiState())
        private set

    fun getHomeNavItems() = listOf(
        HomeNav.Browse,
        HomeNav.Profile,
    )

    fun isNavItemSelected(currentDestination: NavDestination?, item: HomeNav) =
        currentDestination?.hierarchy?.any { it.route == item.route } == true

    fun checkExclusive(currentDestination: NavDestination?) {
        val currentRoute = currentDestination?.route
        val isMatched = getHomeNavItems().any { it.route == currentRoute }
        mainLayoutUiState = mainLayoutUiState.copy(isExclusive = !isMatched)
    }

    fun signOut() = viewModelScope.launch {
        repository.signOut()
    }

}


data class MainLayoutUiState(
    val searchValue: String = "",
    val isExclusive: Boolean = false,
    val isSearching: Boolean = false
)