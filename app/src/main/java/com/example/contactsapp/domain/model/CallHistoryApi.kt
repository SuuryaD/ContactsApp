package com.example.contactsapp.domain.model

import android.os.Parcel
import android.os.Parcelable


data class CallHistoryApi(
    val id: String,
    val number: String,
    val date: Long,
    val duration: String,
    val type: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readLong(),
        parcel.readString().toString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(number)
        parcel.writeLong(date)
        parcel.writeString(duration)
        parcel.writeInt(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CallHistoryApi> {
        override fun createFromParcel(parcel: Parcel): CallHistoryApi {
            return CallHistoryApi(parcel)
        }

        override fun newArray(size: Int): Array<CallHistoryApi?> {
            return arrayOfNulls(size)
        }
    }
}
