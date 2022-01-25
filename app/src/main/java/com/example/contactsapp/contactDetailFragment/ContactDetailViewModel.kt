package com.example.contactsapp.contactDetailFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.contactsapp.database.ContactDetailsDao
import com.example.contactsapp.database.ContactWithPhone
import com.example.contactsapp.model.ContactDetail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactDetailViewModel(val dataSource: ContactDetailsDao, val contactId: Long) : ViewModel() {

    var currentContact = MutableLiveData<ContactWithPhone>()

    val navigateToContactsListFragment = MutableLiveData<Boolean>(false)

    private fun getCurrentContact(contactId: Long){
        CoroutineScope(Dispatchers.Main).launch {
            currentContact.value = getCurrentContactFromDb(contactId)
        }
    }

    private suspend fun getCurrentContactFromDb(contactId: Long) : ContactWithPhone{
        return withContext(Dispatchers.IO){
            dataSource.getContactById(contactId)
        }
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

    init {
        getCurrentContact(contactId)
    }

}