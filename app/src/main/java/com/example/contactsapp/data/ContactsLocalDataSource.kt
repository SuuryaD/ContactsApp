package com.example.contactsapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.contactsapp.data.database.ContactDetailsDao
import com.example.contactsapp.data.database.ContactPhoneNumber
import com.example.contactsapp.data.database.ContactWithPhone
import com.example.contactsapp.data.Result.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactsLocalDataSource(
    private val contactsDao: ContactDetailsDao
) : ContactsDataSource {


    override fun observeAllContacts(): LiveData<Result<List<ContactWithPhone>>>{
        return contactsDao.getAll().map {
            Success(it)
        }
    }

    override fun observeContactById(contactId: Long): LiveData<Result<ContactWithPhone>> {
        return contactsDao.observeContactById(contactId).map {
            Success(it)
        }
    }

    override suspend fun getContactById(contactId: Long): Result<ContactWithPhone> {
        return withContext(Dispatchers.IO) {

            return@withContext try {
                Success(contactsDao.getContactById(contactId))
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }


    override suspend fun insert(contactWithPhone: ContactWithPhone) : Long{
        return withContext(Dispatchers.IO){
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

    override suspend fun updateContact2(contactWithPhone: ContactWithPhone, new: ContactWithPhone) : Long {
        return withContext(Dispatchers.IO){
           return@withContext contactsDao.updateContact2(contactWithPhone, new)
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