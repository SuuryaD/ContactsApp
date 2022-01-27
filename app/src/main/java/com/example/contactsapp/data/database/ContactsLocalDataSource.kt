package com.example.contactsapp.data.database

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactsLocalDataSource(
    private val contactsDao: ContactDetailsDao
) : ContactsDataSource {
    override fun observeAllContacts(): LiveData<List<ContactWithPhone>> {
        return contactsDao.getAll()
    }

    override fun observeContactById(contactId: Long): LiveData<ContactWithPhone> {
        return contactsDao.observeContactById(contactId)
    }

    override suspend fun getContactById(contactId: Long): ContactWithPhone {
        return withContext(Dispatchers.IO){
            contactsDao.getContactById(contactId)
        }
    }

    override suspend fun insert(contactWithPhone: ContactWithPhone) {
        withContext(Dispatchers.IO){
            contactsDao.insert(contactWithPhone)
        }
    }

    override suspend fun insertPhoneNumbers(phoneNumbers: List<ContactPhoneNumber>) {
        withContext(Dispatchers.IO){
            contactsDao.insertPhoneNumbers(phoneNumbers)
        }
    }

    override suspend fun updateContact(contactWithPhone: ContactWithPhone) {
        withContext(Dispatchers.IO){
            contactsDao.updateContact(contactWithPhone)
        }
    }

    override suspend fun deletePhoneNumbers(phoneNumbers: List<ContactPhoneNumber>) {
        withContext(Dispatchers.IO){
            contactsDao.deletePhoneNumbers(phoneNumbers)
        }
    }

    override suspend fun deleteContact(contactId: Long) {
        withContext(Dispatchers.IO){
            contactsDao.deleteContact(contactId)
        }
    }

    override suspend fun nukeDb() {
        withContext(Dispatchers.IO){
            contactsDao.nukeDb()
        }
    }
}