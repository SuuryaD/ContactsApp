package com.example.contactsapp.ui.addContactFragment

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.*
import com.example.contactsapp.data.ContactsDataSource
import com.example.contactsapp.data.database.*
import com.example.contactsapp.util.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Toast

import android.content.ContentProviderOperation
import com.example.contactsapp.util.getRandomMaterialColourCode
import java.lang.Exception
import android.content.OperationApplicationException
import android.database.Cursor
import android.os.RemoteException

import android.provider.ContactsContract.CommonDataKinds.Phone





class AddFragmentViewModel(private val dataSource: ContactsDataSource, val context: Context) :
    ViewModel() {

    private val _contactId: MutableLiveData<Long> = MutableLiveData<Long>()

    val currentContact = _contactId.switchMap { contactId ->
        dataSource.observeContactById(contactId)
    }

    fun setImageUri(uri: Uri) {
        userImage.value = uri.toString()
    }

    val userImage = currentContact.map {
        it?.contactDetails?.user_image ?: ""
    } as MutableLiveData<String>

    val name = currentContact.map {
        it?.contactDetails?.name ?: ""
    } as MutableLiveData<String>

    val email = currentContact.map {
        it?.contactDetails?.email ?: ""
    } as MutableLiveData<String>


    fun start(contactId: Long) {
        _contactId.value = contactId
    }

    private val _snackBarEvent = MutableLiveData<Event<String>>()
    val snackBarEvent: LiveData<Event<String>>
        get() = _snackBarEvent

    private val _navigateToContactDetail = MutableLiveData<Event<Pair<Long, String>>>()
    val navigateToContactDetail: LiveData<Event<Pair<Long, String>>>
        get() = _navigateToContactDetail


    fun isValuesChanged(phoneNumbers: List<String>): Boolean {

        if (currentContact.value != null) {

            if (name.value != currentContact.value?.contactDetails?.name) {
                return true
            }

            if (email.value != currentContact.value?.contactDetails?.email) {
                return true
            }

            if (phoneNumbers.size != currentContact.value?.phoneNumbers?.size)
                return true

            for (i in 0 until currentContact.value?.phoneNumbers?.size!!) {

                if (phoneNumbers.get(i) != currentContact.value?.phoneNumbers?.get(i)?.phoneNumber)
                    return true

            }
        } else {

            if (name.value?.isNotEmpty() == true || email.value?.isNotEmpty() == true || phoneNumbers.isNotEmpty())
                return true
        }
        return false
    }


    fun onSave(phoneNumbers: List<String>) {

        val name = name.value
        val email = email.value

        if (name == null || name.isEmpty()) {
            _snackBarEvent.value = Event("Name cannot be empty")
            return
        }

        if (email != null && email.isNotEmpty() && !email.matches(Patterns.EMAIL_ADDRESS.toRegex())) {
            _snackBarEvent.value = Event("Enter a valid Email")
            return
        }

        if (_contactId.value == 0L) {
            addNewContact2(phoneNumbers)
        } else {
//            for(i in phoneNumbers){
//                updateContact(currentContact.value?.contactDetails?.contactId.toString(), phoneNumbers)
//            }
            updateContact(phoneNumbers)
        }
    }

    private fun addContact(phoneNumbers: List<String>, id: Long) {

        val contactPhoneNumbers = ArrayList<ContactPhoneNumber>()

        for (i in phoneNumbers) {
            contactPhoneNumbers.add(ContactPhoneNumber(phoneNumber = i))
        }

        val newContact = ContactWithPhone(
            ContactDetails(
                contactId = id,
                name = name.value!!,
                email = email.value ?: "",
                user_image = userImage.value ?: "",
                color_code = getRandomMaterialColourCode(context)
            ), contactPhoneNumbers
        )

        CoroutineScope(Dispatchers.Main).launch {
            val id = dataSource.insert(newContact)
            _navigateToContactDetail.value = Event(Pair(id, "Contact Added Successfully"))
        }

    }

    private fun updateContact(phoneNumbers: List<String>) {

        val contactPhoneNumbers = ArrayList<ContactPhoneNumber>()
        var i = 0

        while (i < currentContact.value?.phoneNumbers.orEmpty().size && i < phoneNumbers.size) {
            contactPhoneNumbers.add(
                ContactPhoneNumber(
                    currentContact.value?.phoneNumbers?.get(i)?.phoneId!!,
                    currentContact.value?.contactDetails?.contactId!!,
                    phoneNumbers.get(i)
                )
            )
            i++
        }

        val deleteList = ArrayList<ContactPhoneNumber>()

        while (i < currentContact.value?.phoneNumbers.orEmpty().size) {
            deleteList.add(currentContact.value?.phoneNumbers?.get(i)!!)
            i++
        }

        val newPhoneNumbers = ArrayList<ContactPhoneNumber>()

        while (i < phoneNumbers.size) {
            newPhoneNumbers.add(
                ContactPhoneNumber(
                    contactId = _contactId.value!!,
                    phoneNumber = phoneNumbers[i]
                )
            )
            i++
        }

        val updatedContact = ContactWithPhone(
            ContactDetails(
                contactId = _contactId.value!!,
                name = name.value!!, email = email.value!!,
                user_image = userImage.value ?: "",
                favorite = currentContact.value?.contactDetails?.favorite!!,
                color_code = currentContact.value?.contactDetails?.color_code
                    ?: getRandomMaterialColourCode(context)
            ),
            contactPhoneNumbers
        )

        CoroutineScope(Dispatchers.Main).launch {

//            updateSystemContacts(phoneNumbers)
            dataSource.updateContact(updatedContact)
            dataSource.insertPhoneNumbers(newPhoneNumbers)
            dataSource.deletePhoneNumbers(deleteList)
            _navigateToContactDetail.value =
                Event(Pair(_contactId.value!!, "Contact Updated Successfully"))
        }
    }
//
//    private fun updateSystemContacts(phoneNumbers: List<String>) {
//
//        val contentValues = ContentValues()
//
//        for (i in phoneNumbers) {
//
//
//            // Put new phone number value.
//
//            // Put new phone number value.
//            contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, i)
//            contentValues.put(
//                ContactsContract.CommonDataKinds.Phone.TYPE,
//                ContactsContract.CommonDataKinds.Phone.TYPE_OTHER
//            )
//
//            Log.i("AddFragmentViewModel", contentValues.toString())
//
//
//            // Create query condition, query with the raw contact id.
//
//            // Create query condition, query with the raw contact id.
//            val whereClauseBuf = StringBuffer()
//
//            // Specify the update contact id.
//
//            // Specify the update contact id.
//            whereClauseBuf.append(ContactsContract.Data.CONTACT_ID)
//            whereClauseBuf.append("=")
//            whereClauseBuf.append(currentContact.value?.contactDetails?.contactId)
//
//            // Specify the row data mimetype to phone mimetype( vnd.android.cursor.item/phone_v2 )
//
//            // Specify the row data mimetype to phone mimetype( vnd.android.cursor.item/phone_v2 )
//            whereClauseBuf.append(" and ")
//            whereClauseBuf.append(ContactsContract.Data.MIMETYPE)
//            whereClauseBuf.append(" = '")
//            val mimetype = ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
//            whereClauseBuf.append(mimetype)
//            whereClauseBuf.append("'")
//
//            // Specify phone type.
//
//            // Specify phone type.
//            whereClauseBuf.append(" and ")
//            whereClauseBuf.append(ContactsContract.CommonDataKinds.Phone.TYPE)
//            whereClauseBuf.append(" = ")
//            whereClauseBuf.append(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
//
//            // Update phone info through Data uri.Otherwise it may throw java.lang.UnsupportedOperationException.
//
//            // Update phone info through Data uri.Otherwise it may throw java.lang.UnsupportedOperationException.
//            val dataUri = ContactsContract.Data.CONTENT_URI
//
//            val updateCount: Int =
//                context.contentResolver.update(
//                    dataUri,
//                    contentValues,
//                    whereClauseBuf.toString(),
//                    null
//                )
//
//            Log.i("AddFragmentViewModel", updateCount.toString())
//        }
////        updateContact(phoneNumbers)
//
//    }
//
//    fun updateContact(contactId: String, phoneNumbers: List<String>) {
//
//        val ops = ArrayList<ContentProviderOperation>()
//        val selectPhone: String = ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Contacts.Data.MIMETYPE + "='" +
//                Phone.CONTENT_ITEM_TYPE + "'"
//        val phoneArgs = arrayOf(contactId)
////        val contentops = ContentProviderOperation.
////        for(i in phoneNumbers) {
//            ops.add(
//                ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
//                    .withSelection(selectPhone, phoneArgs)
//                    .withValue(Phone.NUMBER, phoneNumbers[1])
//                    .withValue(Phone.TYPE, Phone.TYPE_OTHER)
//                    .withValue(Phone.NUMBER, phoneNumbers[0])
//                    .withValue(Phone.TYPE, Phone.TYPE_OTHER)
//                    .build()
//            )
////        }
//        try {
//            context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
//        } catch (e: RemoteException) {
//            // TODO Auto-generated catch block
//            e.printStackTrace()
//        } catch (e: OperationApplicationException) {
//            // TODO Auto-generated catch block
//            e.printStackTrace()
//        }
//    }


//    private fun addNewContact(phoneNumbers: List<String>) {
//
//        CoroutineScope(Dispatchers.IO).launch {
//
//            val contentValues = ContentValues()
//            contentValues.put(ContactsContract.RawContacts.ACCOUNT_TYPE,"")
//            contentValues.put(ContactsContract.RawContacts.ACCOUNT_NAME, "")
//
//
//            Log.i("AddFragmentViewModel", "addNewContact called")
//
//            val rawContactUri = context.contentResolver.insert(
//                ContactsContract.RawContacts.CONTENT_URI,
//                contentValues
//            )
//
//            if (rawContactUri != null) {
//                val id = ContentUris.parseId(rawContactUri)
//
//                insertContactDisplayName(
//                    ContactsContract.Data.CONTENT_URI,
//                    id,
//                    name.value!!
//                )
//
//                insertPhoneNumbers(
//                    ContactsContract.Data.CONTENT_URI,
//                    id,
//                    phoneNumbers
//                )
//                Log.i("AddFragmentViewModel", "id: $id")
//                addContact(phoneNumbers, id)
//
////                withContext(Dispatchers.Main){
////                    _navigateToContactDetail.value = Event(Pair(id, "Contact Added Successfully"))
////                }
//            }
//            Log.i("AddFragmentViewModel", "id: null")
//
//        }
//
//    }

    private fun addNewContact2(phoneNumbers: List<String>) {

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
        if (name.value.isNullOrEmpty())
            return

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
                    name.value
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
        if (email != null) {
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
                    )
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, email.value)
                    .withValue(
                        ContactsContract.CommonDataKinds.Email.TYPE,
                        ContactsContract.CommonDataKinds.Email.TYPE_WORK
                    )
                    .build()
            )
        }

        //------------------------------------------------------ Organization

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
            addContact(phoneNumbers, contactId)
            Log.i("AddFragmentViewModel", "AddNewContact2: id: $id")
            Log.i("AddFragmentViewModel", "AddNewContact2: contactId: $contactId")
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Exception: " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

//    private suspend fun insertContactDisplayName(
//        addContactsUri: Uri,
//        rawContactId: Long,
//        displayName: String
//    ) {
//
//        withContext(Dispatchers.IO) {
//
//            val contentValues = ContentValues()
//            contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
//            // Each contact must has an mime type to avoid java.lang.IllegalArgumentException: mimetype is required error.
//            contentValues.put(
//                ContactsContract.Data.MIMETYPE,
//                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
//            )
//            // Put contact display name value.
//            contentValues.put(
//                ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
//                displayName
//            )
//            context.contentResolver.insert(addContactsUri, contentValues)
//        }
//    }
//
//    private suspend fun insertPhoneNumbers(
//        addContactsUri: Uri,
//        rawContactId: Long,
//        phoneNumbers: List<String>
//    ) {
//
//        withContext(Dispatchers.IO) {
//            for (i in phoneNumbers) {
//                val contentValues = ContentValues()
//                contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
//                contentValues.put(
//                    ContactsContract.Data.MIMETYPE,
//                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
//                )
//                contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, i)
//                contentValues.put(
//                    ContactsContract.CommonDataKinds.Phone.TYPE,
//                    ContactsContract.CommonDataKinds.Phone.TYPE_OTHER
//                )
//                context.contentResolver.insert(addContactsUri, contentValues)
//            }
//        }
//
//    }

}

