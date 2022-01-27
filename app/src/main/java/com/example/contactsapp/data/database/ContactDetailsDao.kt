package com.example.contactsapp.data.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ContactDetailsDao{

    @Transaction
    @Query("Select * from contact_details order by name asc")
    fun getAll() : LiveData<List<ContactWithPhone>>

    @Query("Select * from contact_details where contactId=:contactId")
    fun observeContactById(contactId: Long): LiveData<ContactWithPhone>

    @Transaction
    suspend fun insert(contactWithPhone: ContactWithPhone){

        val id = insertContact(ContactDetails(name = contactWithPhone.contactDetails.name, email = contactWithPhone.contactDetails.email))

        for(i in contactWithPhone.phoneNumbers){
            insertPhone(ContactPhoneNumber(contactId = id, phoneNumber = i.phoneNumber))
        }
    }

    @Insert
    suspend fun insertContact(contactDetails: ContactDetails) : Long

    @Insert
    suspend fun insertPhone(contactPhoneNumber: ContactPhoneNumber)

    @Insert
    suspend fun insertPhoneNumbers(contactPhoneNumber : List<ContactPhoneNumber>)

    @Transaction
    suspend fun updateContact(contactWithPhone: ContactWithPhone){
        updateContactDetails(contactWithPhone.contactDetails)

        for(i in contactWithPhone.phoneNumbers){
            updateContactPhoneNumber(i)
        }
    }

    @Transaction
    suspend fun updateContact2(contactWithPhone: ContactWithPhone){

        deleteContact(contactWithPhone.contactDetails.contactId)

        insert(contactWithPhone)
    }

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
