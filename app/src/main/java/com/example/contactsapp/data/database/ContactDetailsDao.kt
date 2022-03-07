package com.example.contactsapp.data.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.contactsapp.domain.model.CallHistory
import com.example.contactsapp.domain.model.CallHistoryApi

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

        for (i in contactWithPhone.phoneNumbers) {
            insertPhone(ContactPhoneNumber(contactId = id, phoneNumber = i.phoneNumber))
        }
        return id
    }

    @Insert
    suspend fun insertContact(contactDetails: ContactDetails): Long

    @Insert
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

    suspend fun getContactDetailsForCallHistory(ls: List<List<CallHistoryApi>>): List<CallHistory> {

        val ls2 = ArrayList<CallHistory>()
        for (i in ls) {

            if (i.isEmpty()) {
                continue
            }

            val id: Long? = getContactId(i[0].number)

            if (id != null) {
                val contact = getContactById(id)
                val temp = CallHistory(
                    contact.contactDetails.contactId,
                    contact.contactDetails.name,
                    contact.contactDetails.user_image,
                    i[0].number,
                    i
                )
                ls2.add(temp)

            } else {
                val temp = CallHistory(0L, i[0].number, "", i[0].number, i)
                ls2.add(temp)
            }
        }
        Log.i("ContactDetailsDao", ls2.toString())
        return ls2
    }

    @Query("SELECT contactId From phone WHERE phoneNumber=:phoneNumber LIMIT 1 ")
    suspend fun getContactId(phoneNumber: String): Long


    @Query("SELECT * FROM contact_details WHERE contactId = ( SELECT contactId FROM phone WHERE phoneNumber=:phoneNumber LIMIT 1)")
    fun getContactFromPhone(phoneNumber: String): LiveData<ContactWithPhone>


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

    @Query("delete from contact_details")
    suspend fun nukeDb()
}
