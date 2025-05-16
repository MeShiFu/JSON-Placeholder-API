package com.example.jsonplaceholderapi.data.repository

import com.example.jsonplaceholderapi.data.model.Post
import com.example.jsonplaceholderapi.data.model.Todo
import com.example.jsonplaceholderapi.data.model.User
import com.example.jsonplaceholderapi.data.network.ApiService

class MainRepository(private val api: ApiService) {

    suspend fun getPosts() = api.getPosts()
    suspend fun getPost(id: Int) = api.getPost(id)
    suspend fun getUsers() = api.getUsers()
    suspend fun getUser(id: Int) = api.getUser(id)
    suspend fun getTodos(userId: Int) = api.getTodos(userId)
}