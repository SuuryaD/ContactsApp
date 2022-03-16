package com.example.contactsapp.domain.model

data class ContactDetail(
    val contactId: Long,
    val name: String,
    val email: String,
    val phone: List<String>
)