package com.example.contactsapp.domain.model

data class ContactDetail(
    val contactId: Long,
    val name: String,
    val email: String,
    val user_image: String,
    val color_code: String,
    val phone: String
)