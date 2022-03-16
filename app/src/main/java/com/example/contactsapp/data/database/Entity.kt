package com.example.contactsapp.data.database

import android.net.Uri
import androidx.room.*


@Entity(tableName = "contact_details")
data class ContactDetails(

    @PrimaryKey(autoGenerate = true)
    val contactId: Long = 0L,

    val name: String,

    val email: String,

    val favorite: Boolean = false,

    val user_image: String = ""

)

@Entity(
    tableName = "phone",
    foreignKeys = [ForeignKey(
        entity = ContactDetails::class,
        parentColumns = ["contactId"],
        childColumns = ["contactId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class ContactPhoneNumber(

    @PrimaryKey(autoGenerate = true)
    val phoneId: Long = 0L,

    val contactId: Long = 0L,

    val phoneNumber: String
)

data class ContactWithPhone(

    @Embedded
    val contactDetails: ContactDetails,

    @Relation(
        entity = ContactPhoneNumber::class,
        parentColumn = "contactId",
        entityColumn = "contactId"
    )
    val phoneNumbers: List<ContactPhoneNumber>
)


//
//fun ContactWithPhone.asModel() : ContactDetail{
//    return ContactDetail( this.contactDetails.contactId, this.contactDetails.name, this.contactDetails.email, this.phoneNumbers)
//}
