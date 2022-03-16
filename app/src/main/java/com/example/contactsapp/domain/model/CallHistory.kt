package com.example.contactsapp.domain.model

import android.os.Parcel
import android.os.Parcelable

data class CallHistory(
    val contactId: Long,
    val name: String,
    val userImage: String?,
    val number: String,
    val callHistoryApi: List<CallHistoryApi>
) : RecyclerViewViewType, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString().toString(),
        parcel.readString(),
        parcel.readString().toString(),
        parcel.createTypedArrayList(CallHistoryApi)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(contactId)
        parcel.writeString(name)
        parcel.writeString(userImage)
        parcel.writeString(number)
        parcel.writeTypedList(callHistoryApi)
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
