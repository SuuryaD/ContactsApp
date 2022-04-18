package com.example.contactsapp.ui.contactsFragment

import android.annotation.SuppressLint
import android.content.*
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.example.contactsapp.data.ContactsDataSource
import com.example.contactsapp.data.Result
import com.example.contactsapp.data.database.*
import com.example.contactsapp.util.Event
import com.example.contactsapp.util.getRandomMaterialColourCode
import ezvcard.Ezvcard
import kotlinx.coroutines.*
import java.io.InputStream
import java.lang.Exception

class ContactsListFragmentViewModel(val datasource: ContactsDataSource, private val context: Context) :
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

            Log.i("ContactListView", it.toString())

            if (it[0].contactDetails.name.first().isDigit()) {
                ls.add(
                    ContactWithPhone(
                        ContactDetails(
                            name = "#",
                            email = "",
                            color_code = getRandomMaterialColourCode(context)
                        ),
                        listOf(ContactPhoneNumber(phoneNumber = ""))
                    )
                )
            } else {
                ls.add(
                    ContactWithPhone(
                        ContactDetails(
                            name = it[0].contactDetails.name[0].uppercase(),
                            email = "",
                            color_code = getRandomMaterialColourCode(context)
                        ),
                        listOf(ContactPhoneNumber(phoneNumber = ""))
                    )
                )
            }

            ls.add(it[0])
            for (i in 1 until it.size) {
                if (it[i - 1].contactDetails.name.substring(0, 1)
                        .lowercase() != it[i].contactDetails.name.substring(0, 1).lowercase()
                ) {
                    ls.add(
                        ContactWithPhone(
                            ContactDetails(
                                name = it[i].contactDetails.name.substring(0, 1).uppercase(),
                                email = "",
                                color_code = getRandomMaterialColourCode(context)
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

        val temp = ContactWithPhone(
            ContactDetails(
                name = name,
                email = email,
                color_code = getRandomMaterialColourCode(context)
            ), ls
        )

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
            var email = ""
            if (!i.emails.isNullOrEmpty()) {
                email = i.emails.get(0).value
            }
            if (name.isNullOrEmpty())
                continue
            cnt++

            addNewContact(name, email, number)
        }
        return 2
    }

//    private fun addNewContact(name: String, email: String, phoneNumbers: List<String>) {
//
//        CoroutineScope(Dispatchers.IO).launch {
//
//            val contentValues = ContentValues()
//
//            val rawContactUri = context.contentResolver.insert(
//                ContactsContract.RawContacts.CONTENT_URI,
//                contentValues
//            )
//
//            val id = ContentUris.parseId(rawContactUri!!)
//
//            insertContactDisplayName(
//                ContactsContract.Data.CONTENT_URI,
//                id,
//                name
//            )
//
//            insertPhoneNumbers(
//                ContactsContract.Data.CONTENT_URI,
//                id,
//                phoneNumbers
//            )
//            addContact(id, name, email, "", "",phoneNumbers, null)
//        }
//
//    }

    private fun addNewContact(name: String, email: String, phoneNumbers: List<String>){
        val ops = ArrayList<ContentProviderOperation>()

        ops.add(
            ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI
            )
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, "com.surya")
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, "surya@zoho.com")
                .build()
        )

        //------------------------------------------------------ Names

        //------------------------------------------------------ Names
//        if (name.isNullOrEmpty())
//            return

        ops.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI.buildUpon()
                .appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true")
                .build())
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/vnd.surya.display")
                .withValue(ContactsContract.Data.DATA1, phoneNumbers.toString())
                .withValue(ContactsContract.Data.DATA2, "surya@zoho.com")
                .withValue(ContactsContract.Data.DATA3, "View Contact")
                .build()
        )

        ops.add(

            ContentProviderOperation.newInsert(
                ContactsContract.Data.CONTENT_URI
            )
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                )
                .withValue(
                    ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                    name
                ).build()
        )


        //------------------------------------------------------ Mobile Number

        //------------------------------------------------------ Mobile Number

        for (i in phoneNumbers) {

            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                    )
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, i)
                    .withValue(
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                    )
                    .build()
            )
        }


        //------------------------------------------------------ Email

        //------------------------------------------------------ Email
//        if (email != null) {
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
                    )
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
                    .withValue(
                        ContactsContract.CommonDataKinds.Email.TYPE,
                        ContactsContract.CommonDataKinds.Email.TYPE_WORK
                    )
                    .build()
            )
//        }

        //------------------------------------------------------ Organization

        //------------------------------------------------------ Organization
//        if (!company.equals("") && !jobTitle.equals("")) {
//            ops.add(
//                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                    .withValue(
//                        ContactsContract.Data.MIMETYPE,
//                        ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE
//                    )
//                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
//                    .withValue(
//                        ContactsContract.CommonDataKinds.Organization.TYPE,
//                        ContactsContract.CommonDataKinds.Organization.TYPE_WORK
//                    )
//                    .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, jobTitle)
//                    .withValue(
//                        ContactsContract.CommonDataKinds.Organization.TYPE,
//                        ContactsContract.CommonDataKinds.Organization.TYPE_WORK
//                    )
//                    .build()
//            )
//        }

        // Asking the Contact provider to create a new contact

        // Asking the Contact provider to create a new contact
        try {
            val res = context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
            val projection = arrayOf(ContactsContract.RawContacts.CONTACT_ID)
            val cursor: Cursor? =
                context.contentResolver.query(res[0].uri!!, projection, null, null, null)
            cursor?.moveToNext()
            val contactId: Long = cursor?.getLong(0) ?: 0L
            cursor?.close()
            val id = ContentUris.parseId(res[0].uri!!)
//            addContact(phoneNumbers, contactId)
            addContact(contactId, name, email, "", "",phoneNumbers, null)
            Log.i("AddFragmentViewModel", "AddNewContact2: id: $id")
            Log.i("AddFragmentViewModel", "AddNewContact2: contactId: $contactId")
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Exception: " + e.message, Toast.LENGTH_SHORT).show()
        }
    }



    private suspend fun insertContactDisplayName(
        addContactsUri: Uri,
        rawContactId: Long,
        displayName: String
    ) {

        withContext(Dispatchers.IO) {

            val contentValues = ContentValues()
            contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
            // Each contact must has an mime type to avoid java.lang.IllegalArgumentException: mimetype is required error.
            contentValues.put(
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
            )
            // Put contact display name value.
            contentValues.put(
                ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                displayName
            )

            context.contentResolver.insert(addContactsUri, contentValues)
        }
    }

    private suspend fun insertPhoneNumbers(
        addContactsUri: Uri,
        rawContactId: Long,
        phoneNumbers: List<String>
    ) {

        withContext(Dispatchers.IO) {
            for (i in phoneNumbers) {
                val contentValues = ContentValues()
                contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                contentValues.put(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                )
                contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, i)
                contentValues.put(
                    ContactsContract.CommonDataKinds.Phone.TYPE,
                    ContactsContract.CommonDataKinds.Phone.TYPE_OTHER
                )
                context.contentResolver.insert(addContactsUri, contentValues)
            }
        }

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

//                val accountId = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.Data.RAW_CONTACT_ID))
//                val selection = { "${ContactsContract.RawContacts.CONTACT_ID} = ?" }
                val rawCursor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    cres.query(
                        ContactsContract.RawContacts.CONTENT_URI,
                        null,
                        "${ContactsContract.RawContacts.CONTACT_ID} = ?",
                        arrayOf("$id"),
                        null,
                        null
                    )
                } else {
                    null
                }

                var accountName = ""
                var accountType = ""
                if (rawCursor?.moveToNext() == true) {
                    accountName =
                        rawCursor.getString(rawCursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_NAME)) ?: ""
//                    ContactsContract.RawContacts.
                    accountType =
                        rawCursor.getString(rawCursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE)) ?: ""
                    Log.i(TAG, "account Name: $accountName, account Type: $accountType")
                }

//                Log.i("ContactListViewModel", id.toString())

                val name =
                    contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
//                Log.i(TAG, name ?: "null")

                val phoneNumbers = ArrayList<String>()

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
                        val phoneId =
                            phoneCursor.getLong(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID))
                        phoneNumber = phoneNumber.replace("(", "")
                        phoneNumber = phoneNumber.replace(")", "")
                        phoneNumber = phoneNumber.replace(" ", "")
                        phoneNumber = phoneNumber.replace("-", "")
                        Log.i(TAG, "Inside phone cursor: phoneNumber: $phoneNumber, phoneId: $phoneId")
                        if(!phoneNumbers.contains(phoneNumber))
                            phoneNumbers.add(phoneNumber)
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
                if (emailCursor?.moveToNext() == true) {
                    email =
                        emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS))
//                    Log.i(TAG, email)
                }

//                Log.i(TAG, "PhoneNumbers: $ls")
                val contact = datasource.getContactById2(id)

                Log.i(TAG, "id: $id, name: $name, ls: ${phoneNumbers}, contact: ${contact.toString()}")
                if (name != null)
                    addContact(id, name, email, accountName, accountType, phoneNumbers, contact)
            }
        }

    }

    private fun addContact(
        id: Long,
        name: String,
        email: String,
        accountName: String,
        accountType: String,
        phoneNumbers: List<String>,
        contactWithPhone: ContactWithPhone?
    ) {
        val contactPhoneNumbers = ArrayList<ContactPhoneNumber>()

        for (i in phoneNumbers) {
            contactPhoneNumbers.add(ContactPhoneNumber(phoneNumber = i))
        }

//        if(contactWithPhone != null &&
//            contactWithPhone.contactDetails.name == name &&
//                contactWithPhone.contactDetails.email == email
//                )
//                    return

        val newContact = ContactWithPhone(
            ContactDetails(
                contactId = id,
                name = name,
                email = email,
                user_image = contactWithPhone?.contactDetails?.user_image ?: "",
                favorite = contactWithPhone?.contactDetails?.favorite ?: false,
                color_code = contactWithPhone?.contactDetails?.color_code
                    ?: getRandomMaterialColourCode(context),
                accountName = accountName,
                accountType = accountType
            ), contactPhoneNumbers
        )

        CoroutineScope(Dispatchers.IO).launch {
//            if(contactWithPhone != newContact)
            Log.i("ContactsListFragment", newContact.toString())
            datasource.insert2(newContact)
        }
    }

}