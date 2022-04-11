package com.example.contactsapp.domain.model

data class CallHistoryTemp(
    val id: String,
    val number: String,
    val date: Long,
    val duration: String,
    val type: Int
)
