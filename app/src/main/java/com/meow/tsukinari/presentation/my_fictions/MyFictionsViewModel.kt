package com.meow.tsukinari.presentation.my_fictions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meow.tsukinari.model.FictionModel
import com.meow.tsukinari.repository.DatabaseRepository
import com.meow.tsukinari.repository.Resources
import kotlinx.coroutines.launch

class MyFictionsViewModel(
    private val repository: DatabaseRepository = DatabaseRepository()
) : ViewModel() {

    var myFictionsUiState by mutableStateOf(MyFictionsUiState())
    val user = repository.user()
    val hasUser: Boolean
        get() = repository.hasUser()
    private val userId: String
        get() = repository.getUserId()

    fun loadFictions() {
        if (hasUser) {
            if (userId.isNotBlank()) {
                getUserFictions(userId)
            }
        } else {
            myFictionsUiState = myFictionsUiState.copy(
                fictionsList = Resources.Error(
                    throwable = Throwable(message = "User is not Login")
                )
            )
        }
    }

    private fun getUserFictions(userId: String) = viewModelScope.launch {
        repository.getUserFictions(userId).collect {
            myFictionsUiState = myFictionsUiState.copy(fictionsList = it)
        }
    }

    fun deleteFiction(fictionId: String) = repository.deleteFiction(fictionId) {
        myFictionsUiState = myFictionsUiState.copy(fictionDeletedStatus = it)
    }


    fun signOut() = repository.signOut()

}

data class MyFictionsUiState(
    val fictionsList: Resources<List<FictionModel>> = Resources.Loading(),
    val fictionDeletedStatus: Boolean = false,
)