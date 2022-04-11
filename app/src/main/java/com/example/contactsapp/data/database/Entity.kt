package com.example.contactsapp.data.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*


@Entity(tableName = "contact_details")
data class ContactDetails(

    @PrimaryKey(autoGenerate = true)
    val contactId: Long = 0L,

    val name: String,

    val email: String,

    val favorite: Boolean = false,

    val user_image: String = "",

    val color_code: String,

    val accountName: String = "",

    val accountType: String = "",

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

@Entity(
    tableName = "call_logs"
)
data class CallHistory(

    @PrimaryKey
    val id: String,
    val contactId: Long = 0L,
    val number: String,
    val name: String,
    val user_image: String,
    val date: Long,
    val duration: String,
    val type: Int

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readLong(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readLong(),
        parcel.readString().toString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeLong(contactId)
        parcel.writeString(number)
        parcel.writeString(name)
        parcel.writeString(user_image)
        parcel.writeLong(date)
        parcel.writeString(duration)
        parcel.writeInt(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CallHistory> {
        override fun createFromParcel(parcel: Parcel): CallHistory {
            return CallHistory(parcel)
        }

        override fun newArray(size: Int): Array<CallHistory?> {
            return arrayOfNulls(size)
        }
    }

}



