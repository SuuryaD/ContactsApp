package com.example.contactsapp.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*


@Entity(tableName = "contact_details")
data class ContactDetails(

    @PrimaryKey(autoGenerate = true)
    val contactId: Long = 0L,

    var name: String,

    var email: String
)

@Entity(tableName = "phone",
    foreignKeys = [ForeignKey(entity = ContactDetails::class,
    parentColumns = ["contactId"],
    childColumns = ["contactId"],
    onDelete = ForeignKey.CASCADE,
    onUpdate = ForeignKey.CASCADE)]
)
data class ContactPhoneNumber(

    @PrimaryKey(autoGenerate = true)
    var phoneId: Long = 0L,

    var contactId: Long = 0L,

    var phoneNumber: String
)


data class ContactWithPhone(

    @Embedded
    val contactDetails: ContactDetails,

    @Relation(entity = ContactPhoneNumber::class, parentColumn = "contactId", entityColumn = "contactId")
    val phoneNumbers: List<ContactPhoneNumber>
)


@Dao
interface ContactDetailsDao{

    @Transaction
    @Query("Select * from contact_details order by name asc")
    fun getAll() : LiveData<List<ContactWithPhone>>



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
    suspend fun deletePhoneNumber(contactPhoneNumber: List<ContactPhoneNumber>)


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

}

@Database(entities = [ContactDetails::class, ContactPhoneNumber::class], version = 5, exportSchema = false)
abstract class ContactDatabase: RoomDatabase(){

    abstract val contactDetailsDao: ContactDetailsDao

    companion object {

        @Volatile
        private var INSTANCE: ContactDatabase? = null

        fun getInstance(context: Context): ContactDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ContactDatabase::class.java,
                        "contact_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}

//
//fun ContactWithPhone.asModel() : ContactDetail{
//    return ContactDetail( this.contactDetails.contactId, this.contactDetails.name, this.contactDetails.email, this.phoneNumbers)
//}
