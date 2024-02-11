package com.meow.tsukinari.presentation.setup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.meow.tsukinari.repository.UserRepository

class SetupViewModel(
    private val repository: UserRepository = UserRepository(),
) : ViewModel() {
    var setupUiState by mutableStateOf(SetupUiState())

    fun onUsernameChange(username: String) {
        setupUiState = setupUiState.copy(username = username)
    }

}

data class SetupUiState(
    val username: String = "Username"
)