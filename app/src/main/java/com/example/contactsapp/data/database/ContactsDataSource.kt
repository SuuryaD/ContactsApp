package com.example.contactsapp.data.database

import androidx.lifecycle.LiveData

interface ContactsDataSource {

    fun observeAllContacts() : LiveData<List<ContactWithPhone>>

    fun observeContactById(contactId: Long) : LiveData<ContactWithPhone>

    suspend fun getContactById(contactId: Long): ContactWithPhone

    suspend fun insert(contactWithPhone: ContactWithPhone)

    suspend fun insertPhoneNumbers(phoneNumbers: List<ContactPhoneNumber>)

    suspend fun updateContact(contactWithPhone: ContactWithPhone)

    suspend fun deletePhoneNumbers(phoneNumbers: List<ContactPhoneNumber>)

    suspend fun deleteContact(contactId: Long)

    suspend fun nukeDb()

}