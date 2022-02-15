package com.example.contactsapp.util

import android.content.Context
import androidx.lifecycle.Observer
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