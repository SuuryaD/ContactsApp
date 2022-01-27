package com.example.contactsapp.data.database

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


//
//fun ContactWithPhone.asModel() : ContactDetail{
//    return ContactDetail( this.contactDetails.contactId, this.contactDetails.name, this.contactDetails.email, this.phoneNumbers)
//}
