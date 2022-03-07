package com.example.contactsapp.ui.contactDetailFragment

import android.util.Log
import androidx.lifecycle.*
import com.example.contactsapp.data.database.ContactWithPhone
import com.example.contactsapp.data.ContactsDataSource
import com.example.contactsapp.util.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactDetailViewModel(private val dataSource: ContactsDataSource) : ViewModel() {

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

    fun start(contactId: Long){

        if(_contactId.value != contactId){
            Log.i("ContactDetailViewModel", "inside If ")
            _contactId.value = contactId
        }
    }

    fun deleteCurrentContact() {
        CoroutineScope(Dispatchers.IO).launch {
            dataSource.deleteContact(_currentContact.value?.contactDetails?.contactId!!)
        }
        _navigateBack.value = Event(Unit)
    }

    fun changeFavourite() {

        currentContact.value?.contactDetails?.favorite?.let {

            CoroutineScope(Dispatchers.Main).launch {
                val before = it
                val v = dataSource.updateFavourite(!it, _contactId.value!!)
                if(v == 1){
                    if(before)
                        _displayFavouriteChangeToast.value = Event("Removed from favourites")
                    else
                        _displayFavouriteChangeToast.value = Event("Added to Favourites Successfully")
                }
                else
                    _displayFavouriteChangeToast.value = Event("Something Went Wrong, Try again!")
            }
        }


    }




}