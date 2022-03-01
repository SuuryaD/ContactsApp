package com.example.contactsapp.util

import android.content.Context
import androidx.lifecycle.Observer
import androidx.room.migration.Migration
import com.example.contactsapp.data.database.ContactWithPhone
import java.io.File
import java.io.FileWriter

open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

}

class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {

    override fun onChanged(event: Event<T>?) {
        event?.getContentIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }
}



fun createVcfFile(contactWithPhone: ContactWithPhone, context: Context) : File {

    val f = File(context.getFilesDir(), "${contactWithPhone.contactDetails?.name!!}.vcf")
    val fw = FileWriter(f)
    fw.write("BEGIN:VCARD\r\n");
    fw.write("VERSION:4.0\r\n");
//        fw.write("N:" + contactWithPhone.getSurname() + ";" + p.getFirstName() + "\r\n");
    fw.write("FN:" + contactWithPhone.contactDetails.name + "\r\n");
//        fw.write("ORG:" + p.getCompanyName() + "\r\n");
//        fw.write("TITLE:" + p.getTitle() + "\r\n");

    for(i in contactWithPhone.phoneNumbers){
        fw.write("TEL;TYPE=WORK,VOICE:" + i.phoneNumber + "\r\n");
    }
//        fw.write("TEL;TYPE=WORK,VOICE:" + p.getWorkPhone() + "\r\n");
//        fw.write("TEL;TYPE=HOME,VOICE:" + p.getHomePhone() + "\r\n");
//        fw.write("ADR;TYPE=WORK:;;" + p.getStreet() + ";" + p.getCity() + ";" + p.getState() + ";" + p.getPostcode() + ";" + p.getCountry() + "\r\n");
    fw.write("EMAIL;TYPE=PREF,INTERNET:" + contactWithPhone.contactDetails.email + "\r\n");
    fw.write("END:VCARD\r\n");
    fw.close();
    return f
}


fun getTimeAgo(time: Long): String? {
    val SECOND_MILLIS = 1000
    val MINUTE_MILLIS = 60 * SECOND_MILLIS
    val HOUR_MILLIS = 60 * MINUTE_MILLIS
    val DAY_MILLIS = 24 * HOUR_MILLIS

    var time = time
    if (time < 1000000000000L) {
        time *= 1000
    }
    val now = System.currentTimeMillis()
    if (time > now || time <= 0) {
        return null
    }
    val diff = now - time
    return if (diff < MINUTE_MILLIS) {
        "just now"
    } else if (diff < 2 * MINUTE_MILLIS) {
        "a minute ago"
    } else if (diff < 50 * MINUTE_MILLIS) {
        diff.div(MINUTE_MILLIS).toString() + " minutes ago"
    } else if (diff < 90 * MINUTE_MILLIS) {
        "an hour ago"
    } else if (diff < 24 * HOUR_MILLIS) {
        diff.div(HOUR_MILLIS).toString() + " hours ago"
    } else if (diff < 48 * HOUR_MILLIS) {
        "yesterday"
    } else {
        diff.div(DAY_MILLIS).toString() + " days ago"
    }
}