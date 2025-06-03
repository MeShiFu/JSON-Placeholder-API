package com.example.jsonplaceholderapi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jsonplaceholderapi.data.model.Comment
import com.example.jsonplaceholderapi.data.model.Post
import com.example.jsonplaceholderapi.data.model.User
import com.example.jsonplaceholderapi.data.repository.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: MainRepository) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _posts.value = repository.getPosts()
                _users.value = repository.getUsers()
                _comments.value = repository.getComments()
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun addComment(postId: Int, authorName: String, authorSurname: String, authorImagePath: String, content: String) {
        val newComment = Comment(
            postId = postId,
            authorName = authorName,
            authorSurname = authorSurname,
            authorImagePath = authorImagePath,
            content = content
        )
        _comments.value = _comments.value + newComment
    }
}