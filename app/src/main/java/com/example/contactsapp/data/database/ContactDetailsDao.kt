package com.example.contactsapp.data.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.contactsapp.domain.model.CallHistoryData

@Dao
interface ContactDetailsDao {

    @Transaction
    @Query("Select * from contact_details order by name collate nocase asc")
    fun getAll(): LiveData<List<ContactWithPhone>>

    @Query("Select * from contact_details where contactId=:contactId")
    fun observeContactById(contactId: Long): LiveData<ContactWithPhone?>

    @Transaction
    suspend fun insert(contactWithPhone: ContactWithPhone): Long {

//        val id = insertContact(ContactDetails(name = contactWithPhone.contactDetails.name, email = contactWithPhone.contactDetails.email))
        val id = insertContact(contactWithPhone.contactDetails)

        Log.i("ContactsDao", id.toString())
        for (i in contactWithPhone.phoneNumbers) {
            insertPhone(ContactPhoneNumber(contactId = id, phoneNumber = i.phoneNumber))
        }
        return id
    }

    @Transaction
    suspend fun insert2(contactWithPhone: ContactWithPhone) {

//        val id = insertContact(ContactDetails(name = contactWithPhone.contactDetails.name, email = contactWithPhone.contactDetails.email))
        val id = insertContact(contactWithPhone.contactDetails)

        if (id == -1L)
            return
        Log.i("ContactsDao", id.toString())
        for (i in contactWithPhone.phoneNumbers) {
            insertPhone(ContactPhoneNumber(contactId = id, phoneNumber = i.phoneNumber))
        }
        return
    }

    @Insert
    suspend fun insertContact2(contactDetails: ContactDetails): Long

    @Insert
    suspend fun insertPhone2(contactPhoneNumber: ContactPhoneNumber)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contactDetails: ContactDetails): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhone(contactPhoneNumber: ContactPhoneNumber)

    @Insert
    suspend fun insertPhoneNumbers(contactPhoneNumber: List<ContactPhoneNumber>)

    @Transaction
    suspend fun updateContact(contactWithPhone: ContactWithPhone) {
        updateContactDetails(contactWithPhone.contactDetails)

        for (i in contactWithPhone.phoneNumbers) {
            updateContactPhoneNumber(i)
        }
    }

    @Transaction
    suspend fun updateContact2(oldContactWithPhone: ContactWithPhone, new: ContactWithPhone): Long {

        deleteContact(oldContactWithPhone.contactDetails.contactId)
        return insert(new)
    }

    @Query("SELECT contactId From phone WHERE phoneNumber=:phoneNumber LIMIT 1 ")
    suspend fun getContactId(phoneNumber: String): Long

    @Query("SELECT * FROM contact_details WHERE contactId = ( SELECT contactId FROM phone WHERE phoneNumber=:phoneNumber LIMIT 1)")
    fun getContactFromPhone(phoneNumber: String): LiveData<ContactWithPhone>

    @Query("SELECT * FROM contact_details WHERE contactId = ( SELECT contactId FROM phone WHERE phoneNumber=:phoneNumber LIMIT 1)")
    fun getContactFromPhone2(phoneNumber: String): ContactWithPhone?

    @Transaction
    @Query("SELECT * FROM contact_details WHERE favorite=1")
    fun getAllFavoriteContacts(): LiveData<List<ContactWithPhone>>

    @Query("UPDATE contact_details SET favorite=:boo WHERE contactId=:contactId")
    suspend fun updateFavourite(boo: Boolean, contactId: Long): Int

    @Delete
    suspend fun deletePhoneNumbers(contactPhoneNumber: List<ContactPhoneNumber>)

    @Update
    suspend fun updateContactDetails(contactDetails: ContactDetails)

    @Update
    suspend fun updateContactPhoneNumber(contactPhoneNumber: ContactPhoneNumber)

    @Query("Delete from contact_details where contactId=:id")
    suspend fun deleteContact(id: Long)

    @Query("select phoneNumber from phone where contactId=:id")
    suspend fun getphoneNumber(id: Long): List<String>

    @Query("select * from contact_details where contactId=:contactId")
    suspend fun getContactById(contactId: Long): ContactWithPhone

    @Query("select * from contact_details where contactId=:contactId")
    suspend fun getContactById2(contactId: Long): ContactWithPhone?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallLog(callHistory: List<CallHistory>)

    @Query("select * from call_logs")
    suspend fun getCallLog(): List<CallHistory>

    @Query("delete from call_logs where id=:id")
    suspend fun deleteCallHistory(id: Long)

    @Query("UPDATE CONTACT_DETAILS SET favorite=0 WHERE contactId=:contactId")
    suspend fun removeFavorite(contactId: Long)

    @Query("delete from contact_details")
    suspend fun nukeDb()
}
