package com.meow.tsukinari.presentation.browse

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meow.tsukinari.model.FictionModel
import com.meow.tsukinari.repository.DatabaseRepository
import com.meow.tsukinari.repository.Resources
import kotlinx.coroutines.launch

class BrowseViewModel(
    private val repository: DatabaseRepository = DatabaseRepository()
) : ViewModel() {


    var browseUiState by mutableStateOf(BrowseUiState())

    fun getUserProfileImgae() = repository.user()?.uid

    fun loadFictions() = viewModelScope.launch {
        repository.getFictions().collect {
            browseUiState = browseUiState.copy(fictionsList = it)
        }
    }
}

data class BrowseUiState(
    val fictionsList: Resources<List<FictionModel>> = Resources.Loading(),
)