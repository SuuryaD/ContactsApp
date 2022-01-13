package com.example.contactsapp.contactDetailFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.contactsapp.database.ContactDetailsDao
import com.example.contactsapp.database.asModel
import com.example.contactsapp.model.ContactDetail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactDetailViewModel(val dataSource: ContactDetailsDao, val contactId: Long) : ViewModel() {

    var currentContact = MutableLiveData<ContactDetail>()


    private fun getCurrentContact(contactId: Long){
        CoroutineScope(Dispatchers.Main).launch {
            currentContact.value = getCurrentContactFromDb(contactId)
        }
    }

    private suspend fun getCurrentContactFromDb(contactId: Long) : ContactDetail{
        return withContext(Dispatchers.IO){
            dataSource.getContactById(contactId).asModel()
        }
    }

    init {
        getCurrentContact(contactId)
    }

}