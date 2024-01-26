package com.meow.tsukinari.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meow.tsukinari.model.FictionModel
import com.meow.tsukinari.repository.DatabaseRepository

class UploadViewModel(
    private val repository: DatabaseRepository
) : ViewModel() {

    var dataSet by mutableStateOf(FictionModel())
        private set

    fun testPushData() = viewModelScope.run {
        {
            repository.addNote(
                dataSet.uploaderId,
                dataSet.title,
                dataSet.description,
                dataSet.uploadedAt
            ) {}
        }
    }

}