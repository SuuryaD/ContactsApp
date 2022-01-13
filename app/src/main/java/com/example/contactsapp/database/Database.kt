package com.example.contactsapp.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.contactsapp.model.ContactDetail


@Entity(tableName = "contact_details")
data class ContactDetails(

    @PrimaryKey(autoGenerate = true)
    val contactId: Long = 0L,

    val name: String,

    val email: String
)

@Entity(tableName = "phone",
    foreignKeys = [ForeignKey(entity = ContactDetails::class,
    parentColumns = ["contactId"],
    childColumns = ["contactId"],
    onDelete = ForeignKey.CASCADE)]
)
data class ContactPhoneNumber(

    @PrimaryKey(autoGenerate = true)
    val phoneId: Long = 0L,

    val contactId: Long,

    val phoneNumber: String
)


data class ContactWithPhone(

    @Embedded
    val contactDetails: ContactDetails,

    @Relation(entity = ContactPhoneNumber::class, parentColumn = "contactId", entityColumn = "contactId", projection = ["phoneNumber"])
    val phoneNumbers: List<String>
)

@Dao
interface ContactDetailsDao{

    @Transaction
    @Query("Select * from contact_details")
    fun getAll() : LiveData<List<ContactWithPhone>>


    @Transaction
    suspend fun insert(contactDetail: ContactDetail){

        val id = insertContact(ContactDetails(name = contactDetail.name, email = contactDetail.email))

        for(i in contactDetail.phone){
            insertPhone(ContactPhoneNumber(contactId = id, phoneNumber = i))
        }
    }

    @Insert
    suspend fun insertContact(contactDetails: ContactDetails) : Long

    @Insert
    suspend fun insertPhone(contactPhoneNumber: ContactPhoneNumber)


    @Query("Delete from contact_details where contactId=:id")
    suspend fun deleteContact(id: Long)

    @Query("select phoneNumber from phone where contactId=:id")
    suspend fun getphoneNumber(id: Long): List<String>

    @Query("select * from contact_details where contactId=:contactId")
    suspend fun getContactById(contactId: Long): ContactWithPhone

}

@Database(entities = [ContactDetails::class, ContactPhoneNumber::class], version = 2, exportSchema = false)
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


fun ContactWithPhone.asModel() : ContactDetail{
    return ContactDetail( this.contactDetails.contactId, this.contactDetails.name, this.contactDetails.email, this.phoneNumbers)
}
