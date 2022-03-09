package com.example.contactsapp.ui.contactsFragment

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.contactsapp.data.ContactsDataSource
import com.example.contactsapp.data.Result
import com.example.contactsapp.data.database.*
import com.example.contactsapp.util.Event
import ezvcard.Ezvcard
import kotlinx.coroutines.*
import java.io.InputStream

class ContactsListFragmentViewModel(val datasource: ContactsDataSource, val context: Context) :
    ViewModel() {

    private val TAG = "ContactListViewModel"

    private val _contacts = datasource.observeAllContacts().map {
        if (it is Result.Success)
            it.data
        else
            null
    }

    val contacts: LiveData<List<ContactWithPhone>> = Transformations.map(_contacts) {
//        CoroutineScope(Dispatchers.IO).launch {
//            datasource.nukeDb()
//        }
        val ls = ArrayList<ContactWithPhone>()
        it?.let {
            if (it.isEmpty())
                return@map ls
            ls.add(
                ContactWithPhone(
                    ContactDetails(
                        name = it[0].contactDetails.name.substring(0, 1).uppercase(),
                        email = ""
                    ),
                    listOf(ContactPhoneNumber(phoneNumber = ""))
                )
            )
            ls.add(it[0])
            for (i in 1 until it.size) {
                if (it[i - 1].contactDetails.name.substring(0, 1)
                        .lowercase() != it[i].contactDetails.name.substring(0, 1).lowercase()
                ) {
                    ls.add(
                        ContactWithPhone(
                            ContactDetails(
                                name = it[i].contactDetails.name.substring(0, 1).uppercase(),
                                email = ""
                            ),
                            listOf(ContactPhoneNumber(phoneNumber = ""))
                        )
                    )
                    ls.add(it[i])
                } else {
                    ls.add(it[i])
                }
            }
            return@map ls
        }
    }

    private val _navigateToAddContact = MutableLiveData<Event<Unit>>()
    val navigateToAddContact: LiveData<Event<Unit>>
        get() = _navigateToAddContact

    fun onAddButtonClick() {
        _navigateToAddContact.value = Event(Unit)
    }

    fun addContact(name: String, email: String, phoneNumbers: List<String>) {

        val ls = ArrayList<ContactPhoneNumber>()
        for (i in phoneNumbers) {
            ls.add(ContactPhoneNumber(phoneNumber = i))
        }

        val temp = ContactWithPhone(ContactDetails(name = name, email = email), ls)

        CoroutineScope(Dispatchers.Main).launch {

            val id = withContext(Dispatchers.IO) {
                datasource.insert(temp)
            }

        }
    }

    fun importContacts(inputStream: InputStream?): Int {

        val vCards = Ezvcard.parse(inputStream).all()
        var cnt = 0
        for (i in vCards) {

            val name = i.formattedName.value
            val number = i.telephoneNumbers.map {
                if (it.text.startsWith('+')) {
                    it.text.replaceAfter('+', it.text.replace("[\\D]".toRegex(), ""))
                } else {
                    it.text.replace("[\\D]".toRegex(), "")
                }
            }
            val email = i.emails.first().value

            if (name.isNullOrEmpty())
                continue

            cnt++
            addContact(name, email, number)
        }
        return cnt
    }

    @SuppressLint("Range")
    fun import() {

        CoroutineScope(Dispatchers.IO).launch {

            val cres = context.contentResolver

            val contactsCursor: Cursor? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                cres.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    null,
                    null,
                    null
                )
            } else {
                null
            }

            while (contactsCursor != null && contactsCursor.moveToNext()) {

                val id =
                    contactsCursor.getLong(contactsCursor.getColumnIndex(ContactsContract.Contacts._ID))
                Log.i("ContactListViewModel", id.toString())

                val name =
                    contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                Log.i(TAG, name.toString())

                val ls = ArrayList<String>()

                if (contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                        .toInt() > 0
                ) {

                    val phoneCursor = cres.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                        arrayOf(id.toString()),
                        null
                    )

                    while (phoneCursor != null && phoneCursor.moveToNext()) {

                        var phoneNumber =
                            phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        phoneNumber = phoneNumber.replace("(", "")
                        phoneNumber = phoneNumber.replace(")", "")
                        phoneNumber = phoneNumber.replace(" ", "")
                        phoneNumber = phoneNumber.replace("-", "")
                        ls.add(phoneNumber)
                    }

                }


                val emailCursor = cres.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null,
                    "${ContactsContract.CommonDataKinds.Email.CONTACT_ID} = ?",
                    arrayOf(id.toString()),
                    null
                )

                var email = ""
                if(emailCursor?.moveToNext() == true){
                    email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS))
                    Log.i(TAG, email.toString())
                }

                Log.i(TAG, "PhoneNumbers: $ls")
                addContact(name, email, ls)
            }
        }

    }

    private fun addContact(id: Long, name: String, email: String, phoneNumbers: List<String>){
        val contactPhoneNumbers = ArrayList<ContactPhoneNumber>()

        for(i in phoneNumbers){
            contactPhoneNumbers.add(ContactPhoneNumber(phoneNumber = i))
        }

        val newContact = ContactWithPhone(ContactDetails(contactId = id, name = name, email = email, user_image = ""), contactPhoneNumbers)

        CoroutineScope(Dispatchers.Main).launch {
            val id = datasource.insert(newContact)
        }
    }

}