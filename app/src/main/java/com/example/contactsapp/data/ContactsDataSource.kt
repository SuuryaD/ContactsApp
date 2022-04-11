package com.example.contactsapp.data

import androidx.lifecycle.LiveData
import com.example.contactsapp.data.database.CallHistory
import com.example.contactsapp.data.database.ContactPhoneNumber
import com.example.contactsapp.data.database.ContactWithPhone
import com.example.contactsapp.domain.model.CallHistoryData
import com.example.contactsapp.domain.model.CallHistoryTemp

interface ContactsDataSource {

    fun observeAllContacts(): LiveData<Result<List<ContactWithPhone>>>

    fun observeContactById(contactId: Long): LiveData<ContactWithPhone?>

    suspend fun getContactById(contactId: Long): Result<ContactWithPhone>

    suspend fun getContactById2(contactId: Long): ContactWithPhone?

    suspend fun insert(contactWithPhone: ContactWithPhone): Long

    suspend fun insert2(contactWithPhone: ContactWithPhone)

    suspend fun insertPhoneNumbers(phoneNumbers: List<ContactPhoneNumber>)

    suspend fun updateContact(contactWithPhone: ContactWithPhone)

    suspend fun updateContact2(contactWithPhone: ContactWithPhone, new: ContactWithPhone): Long

    suspend fun getContactNames(ls: List<List<CallHistory>>): List<CallHistoryData>

    suspend fun deleteCallHistory(id: Long)

    suspend fun updateFavourite(boolean: Boolean, contactId: Long): Int

    fun getAllFavoriteContacts(): LiveData<List<ContactWithPhone>>

    suspend fun deletePhoneNumbers(phoneNumbers: List<ContactPhoneNumber>)

    suspend fun deleteContact(contactId: Long)

    fun getContactFromPhone(callHistoryData: CallHistoryData): LiveData<CallHistoryData>

    suspend fun insertCallLog(callHistory: List<CallHistoryTemp>)

    suspend fun getCallLog(): List<CallHistory>

    suspend fun removeFavorite(contactId: Long)

    suspend fun getUnsyncedContacts() : List<ContactWithPhone>

    suspend fun nukeDb()

}