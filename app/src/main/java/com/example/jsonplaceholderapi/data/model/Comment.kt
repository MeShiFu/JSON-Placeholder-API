package com.example.jsonplaceholderapi.data.model

data class Comment(
    val postId: Int,
    val authorName: String,
    val authorSurname: String,
    val authorImagePath: String,
    val content: String
)