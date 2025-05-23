package com.example.finsight20.model

data class ChatMessage(
    val message: String,
    val isUser: Boolean // true = kullanıcı mesajı, false = bot cevabı
)