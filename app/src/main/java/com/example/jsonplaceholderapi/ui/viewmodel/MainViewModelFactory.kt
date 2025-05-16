package com.example.jsonplaceholderapi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jsonplaceholderapi.data.repository.MainRepository
import com.example.jsonplaceholderapi.data.network.RetrofitInstance

class MainViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(MainRepository(RetrofitInstance.api)) as T
    }
}