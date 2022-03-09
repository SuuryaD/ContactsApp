package com.example.contactsapp.data

import androidx.lifecycle.LiveData
import com.example.contactsapp.data.database.ContactPhoneNumber
import com.example.contactsapp.data.database.ContactWithPhone
import com.example.contactsapp.domain.model.CallHistory
import com.example.contactsapp.domain.model.CallHistoryApi
import com.example.contactsapp.ui.callHistoryDetailFragment.CallHistoryDetailFragmentDirections

interface ContactsDataSource {

    fun observeAllContacts() : LiveData<Result<List<ContactWithPhone>>>

    fun observeContactById(contactId: Long) : LiveData<ContactWithPhone?>

    suspend fun getContactById(contactId: Long): Result<ContactWithPhone>

    suspend fun insert(contactWithPhone: ContactWithPhone) : Long

    suspend fun insert2(contactWithPhone: ContactWithPhone)

    suspend fun insertPhoneNumbers(phoneNumbers: List<ContactPhoneNumber>)

    suspend fun updateContact(contactWithPhone: ContactWithPhone)

    suspend fun updateContact2(contactWithPhone: ContactWithPhone, new: ContactWithPhone): Long

    suspend fun getContactNames(ls: List<List<CallHistoryApi>>): List<CallHistory>

    suspend fun updateFavourite(boolean: Boolean, contactId: Long): Int

    fun getAllFavoriteContacts(): LiveData<List<ContactWithPhone>>

    suspend fun deletePhoneNumbers(phoneNumbers: List<ContactPhoneNumber>)

    suspend fun deleteContact(contactId: Long)

    fun getContactFromPhone(callHistory: CallHistory): LiveData<CallHistory>

    suspend fun nukeDb()

}