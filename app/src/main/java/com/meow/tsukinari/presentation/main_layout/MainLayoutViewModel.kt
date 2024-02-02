package com.meow.tsukinari.presentation.main_layout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDestination
import com.meow.tsukinari.model.HomeNav
import com.meow.tsukinari.repository.DatabaseRepository


class MainLayoutViewModel(
    private val repository: DatabaseRepository = DatabaseRepository()
) : ViewModel() {

    var mainLayoutUiState by mutableStateOf(MainLayoutUiState())


    fun getNavItems() = listOf(
        HomeNav.Browse,
        HomeNav.More,
    )


    fun checkExclusive(currentDestination: NavDestination?) {
        val currentRoute = currentDestination?.route
        val isMatched = getNavItems().any { it.route == currentRoute }
        mainLayoutUiState = mainLayoutUiState.copy(isExclisive = !isMatched)
    }


}


data class MainLayoutUiState(
    val isExclisive: Boolean = false
)