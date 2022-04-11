package com.example.contactsapp.domain.model

import android.os.Parcel
import android.os.Parcelable
import com.example.contactsapp.data.database.CallHistory
import java.util.ArrayList

data class CallHistoryData(
    val contactId: Long,
    val name: String,
    val userImage: String?,
    val number: String,
    val callHistoryApi: List<CallHistory>
) : RecyclerViewViewType(), Parcelable{

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString().toString(),
        parcel.readString(),
        parcel.readString().toString(),
        parcel.createTypedArrayList(CallHistory)!!
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

    companion object CREATOR : Parcelable.Creator<CallHistoryData> {
        override fun createFromParcel(parcel: Parcel): CallHistoryData {
            return CallHistoryData(parcel)
        }

        override fun newArray(size: Int): Array<CallHistoryData?> {
            return arrayOfNulls(size)
        }
    }

}
