package com.example.contactsapp.data

import android.util.Log
import androidx.lifecycle.*
import com.example.contactsapp.data.database.ContactDetailsDao
import com.example.contactsapp.data.database.ContactPhoneNumber
import com.example.contactsapp.data.database.ContactWithPhone
import com.example.contactsapp.data.Result.Success
import com.example.contactsapp.data.database.CallHistory
import com.example.contactsapp.domain.model.CallHistoryData
import com.example.contactsapp.domain.model.CallHistoryTemp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactsLocalDataSource(
    private val contactsDao: ContactDetailsDao
) : ContactsDataSource {

    override fun observeAllContacts(): LiveData<Result<List<ContactWithPhone>>> {
        return contactsDao.getAll().map {
            Success(it)
        }
    }

    override fun observeContactById(contactId: Long): LiveData<ContactWithPhone?> {
        return contactsDao.observeContactById(contactId)
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

    override suspend fun getContactById2(contactId: Long): ContactWithPhone? {
        return withContext(Dispatchers.IO) {
            contactsDao.getContactById2(contactId)
        }
    }


    override suspend fun insert(contactWithPhone: ContactWithPhone): Long {
        return withContext(Dispatchers.IO) {
            contactsDao.insert(contactWithPhone)
        }
    }

    override suspend fun insert2(contactWithPhone: ContactWithPhone) {
        withContext(Dispatchers.IO) {
            contactsDao.insert2(contactWithPhone)
        }
    }

    override suspend fun insertPhoneNumbers(phoneNumbers: List<ContactPhoneNumber>) {
        withContext(Dispatchers.IO) {
            contactsDao.insertPhoneNumbers(phoneNumbers)
        }
    }

    override suspend fun updateContact(contactWithPhone: ContactWithPhone) {
        withContext(Dispatchers.IO) {
            contactsDao.updateContact(contactWithPhone)
        }
    }

    override suspend fun updateContact2(
        contactWithPhone: ContactWithPhone,
        new: ContactWithPhone
    ): Long {
        return withContext(Dispatchers.IO) {
            return@withContext contactsDao.updateContact2(contactWithPhone, new)
        }
    }

    override suspend fun updateFavourite(boolean: Boolean, contactId: Long): Int {
        return withContext(Dispatchers.IO) {
            contactsDao.updateFavourite(boolean, contactId)
        }
    }

    override fun getAllFavoriteContacts(): LiveData<List<ContactWithPhone>> {
        return contactsDao.getAllFavoriteContacts()
    }

    override suspend fun deletePhoneNumbers(phoneNumbers: List<ContactPhoneNumber>) {
        withContext(Dispatchers.IO) {
            contactsDao.deletePhoneNumbers(phoneNumbers)
        }
    }

    override suspend fun deleteContact(contactId: Long) {
        withContext(Dispatchers.IO) {
            contactsDao.deleteContact(contactId)
        }
    }

    override fun getContactFromPhone(callHistoryData: CallHistoryData): LiveData<CallHistoryData> {

        val contact = contactsDao.getContactFromPhone(callHistoryData.number)

        val x: LiveData<CallHistoryData> = Transformations.map(contact) {
            it?.let {
                return@map CallHistoryData(
                    it.contactDetails.contactId,
                    it.contactDetails.name,
                    it.contactDetails.user_image,
                    callHistoryData.number,
                    callHistoryData.callHistoryApi
                )
            }
            callHistoryData
        }

        return x

    }

    override suspend fun insertCallLog(callHistory: List<CallHistoryTemp>) {

        withContext(Dispatchers.IO) {

            val ls = ArrayList<CallHistory>()

            for (i in callHistory) {

                val contact = contactsDao.getContactFromPhone2(i.number)

                ls.add(
                    CallHistory(
                        i.id,
                        contact?.contactDetails?.contactId ?: 0L,
                        i.number,
                        contact?.contactDetails?.name ?: i.number,
                        contact?.contactDetails?.user_image ?: "",
                        i.date,
                        i.duration,
                        i.type
                    )
                )
            }
            contactsDao.insertCallLog(ls)
        }
    }

    override suspend fun getCallLog(): List<CallHistory> {

        return withContext(Dispatchers.IO) {
            contactsDao.getCallLog()
        }

    }

    override suspend fun removeFavorite(contactId: Long) {
        withContext(Dispatchers.IO) {
            contactsDao.removeFavorite(contactId)
        }
    }

    override suspend fun getUnsyncedContacts(): List<ContactWithPhone> {
//        return withContext(Dispatchers.IO){
//            contactsDao.getUnsyncedContacts()
//        }
        return emptyList()
    }

    override suspend fun nukeDb() {

        withContext(Dispatchers.IO) {
            contactsDao.nukeDb()
        }

    }

    override fun getCallLog2(): LiveData<List<CallHistory>> {
        return contactsDao.getCallLog2()
    }

    override fun getContactNames(ls: List<List<CallHistory>>): List<CallHistoryData> {

//        return withContext(Dispatchers.IO) {
            val ls2 = ArrayList<CallHistoryData>()
            for (i in ls) {

                if (i.isEmpty()) {
                    continue
                }

                val temp = CallHistoryData(
                    i[0].contactId,
                    i[0].name,
                    i[0].user_image,
                    i[0].number,
                    i
                )
                ls2.add(temp)

            }
            Log.i("ContactDetailsDao", ls2.toString())
            return ls2
//        }
    }

    override suspend fun deleteCallHistory(id: Long) {

        withContext(Dispatchers.IO) {
            contactsDao.deleteCallHistory(id)
        }

    }

}