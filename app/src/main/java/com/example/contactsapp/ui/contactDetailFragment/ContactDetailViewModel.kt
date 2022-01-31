package com.example.contactsapp.ui.contactDetailFragment

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.example.contactsapp.data.database.ContactWithPhone
import com.example.contactsapp.data.ContactsDataSource
import com.example.contactsapp.data.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactDetailViewModel(val dataSource: ContactsDataSource) : ViewModel() {

    private val _contactId = MutableLiveData<Long>()

    private val _currentContact = _contactId.switchMap {
        dataSource.observeContactById(it).map { computeResult(it) }
    }

    private fun computeResult(it: Result<ContactWithPhone>) : ContactWithPhone?{
        return if(it is Result.Success){
            it.data
        }else{
            null
        }
    }

    val currentContact: LiveData<ContactWithPhone?> = _currentContact


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