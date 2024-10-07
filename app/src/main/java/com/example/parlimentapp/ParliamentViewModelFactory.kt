package com.example.parlimentapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.parlimentapp.data.dao.ParliamentMemberDao
import com.example.parlimentapp.network.ParliamentApiService

class ParliamentViewModelFactory(
    private val dao: ParliamentMemberDao,
    private val apiService: ParliamentApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ParliamentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ParliamentViewModel(dao, apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
