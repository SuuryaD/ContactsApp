package com.example.contactsapp.data

import androidx.lifecycle.LiveData
import com.example.contactsapp.data.database.ContactPhoneNumber
import com.example.contactsapp.data.database.ContactWithPhone

interface ContactsDataSource {

    fun observeAllContacts() : LiveData<Result<List<ContactWithPhone>>>

    fun observeContactById(contactId: Long) : LiveData<Result<ContactWithPhone>>

    suspend fun getContactById(contactId: Long): Result<ContactWithPhone>

    suspend fun insert(contactWithPhone: ContactWithPhone)

    suspend fun insertPhoneNumbers(phoneNumbers: List<ContactPhoneNumber>)

    suspend fun updateContact(contactWithPhone: ContactWithPhone)

    suspend fun deletePhoneNumbers(phoneNumbers: List<ContactPhoneNumber>)

    suspend fun deleteContact(contactId: Long)

    suspend fun nukeDb()

}