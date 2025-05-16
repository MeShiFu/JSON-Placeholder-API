package com.example.jsonplaceholderapi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jsonplaceholderapi.data.model.Post
import com.example.jsonplaceholderapi.data.repository.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostDetailViewModel(private val repository: MainRepository) : ViewModel() {

    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> = _post

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchPost(id: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _post.value = repository.getPost(id)
            } catch (e: Exception) {
                _post.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}