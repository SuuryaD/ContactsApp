package com.example.contactsapp.ui.contactDetailFragment

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.*
import com.example.contactsapp.data.database.ContactWithPhone
import com.example.contactsapp.data.ContactsDataSource
import com.example.contactsapp.util.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactDetailViewModel(private val dataSource: ContactsDataSource,
private val context: Context
) : ViewModel() {

    private val _contactId = MutableLiveData<Long>()

    private val _currentContact = _contactId.switchMap {
        dataSource.observeContactById(it)
    }

    val currentContact: LiveData<ContactWithPhone?> = _currentContact

    private val _navigateBack = MutableLiveData<Event<Unit>>()
    val navigateBack: LiveData<Event<Unit>>
        get() = _navigateBack


    private val _displayFavouriteChangeToast = MutableLiveData<Event<String>>()
    val displayFavouriteChangeToast: LiveData<Event<String>>
        get() = _displayFavouriteChangeToast

    fun start(contactId: Long) {

        if (_contactId.value != contactId) {
            _contactId.value = contactId
        }
    }

    fun deleteCurrentContact() {
        CoroutineScope(Dispatchers.IO).launch {
            deleteContactById2(_currentContact.value?.contactDetails?.contactId!!)
            dataSource.deleteContact(_currentContact.value?.contactDetails?.contactId!!)
        }
        _navigateBack.value = Event(Unit)
    }

    @SuppressLint("Range")
    fun deleteContactById(id: Long) {
        val cr = context.contentResolver
        val cur = cr.query(
            ContactsContract.RawContacts.CONTENT_URI,
            null, null, null, null)
        cur?.let {
            try {
                if (it.moveToFirst()) {
                    do {
                        if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup._ID)) == id.toString()) {
                            val lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY))
                            val uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey)
                            cr.delete(uri, null, null)
                            break
                        }

                    } while (it.moveToNext())
                }

            } catch (e: Exception) {
                println(e.stackTrace)
            } finally {
                it.close()
            }
        }
    }

    fun deleteContactById2(id: Long){
        val uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, id.toString())
        context.contentResolver.delete(uri, null, null)
    }

    fun changeFavourite() {

        currentContact.value?.contactDetails?.favorite?.let {

            CoroutineScope(Dispatchers.Main).launch {
                val before = it
                val v = dataSource.updateFavourite(!it, _contactId.value!!)
//                if (v == 1) {
//                    if (before)
//                        _displayFavouriteChangeToast.value = Event("Removed from favourites")
//                    else
//                        _displayFavouriteChangeToast.value =
//                            Event("Added to Favourites Successfully")
//                } else
//                    _displayFavouriteChangeToast.value = Event("Something Went Wrong, Try again!")
            }
        }


    }


}