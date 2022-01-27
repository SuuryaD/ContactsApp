package com.example.contactsapp.ui.contactDetailFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.contactsapp.data.database.ContactDetailsDao
import com.example.contactsapp.data.database.ContactWithPhone
import com.example.contactsapp.data.database.ContactsDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactDetailViewModel(val dataSource: ContactsDataSource) : ViewModel() {

    private val _contactId = MutableLiveData<Long>()

    private val _currentContact = _contactId.switchMap {
        dataSource.observeContactById(it)
    }
    val currentContact: LiveData<ContactWithPhone> = _currentContact


    val navigateToContactsListFragment = MutableLiveData<Boolean>(false)


    fun start(contactId: Long){

        if(_contactId.value != contactId){
            Log.i("ContactDetailViewModel", "inside If ")
            _contactId.value = contactId
        }

        Log.i("ContactDetailViewModel", "View Model Start called")
    }

    fun deleteCurrentContact() {
        CoroutineScope(Dispatchers.IO).launch {
            dataSource.deleteContact(currentContact.value?.contactDetails?.contactId!!)
        }
        navigateToContactsListFragment.value = true
    }

    fun doneNavigateToContactsListFragment(){
        navigateToContactsListFragment.value = false
    }

}