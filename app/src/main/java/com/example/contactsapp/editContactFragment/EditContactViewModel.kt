package com.example.contactsapp.editContactFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.contactsapp.database.*
import com.example.contactsapp.model.ContactDetail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditContactViewModel(val dataSource: ContactDetailsDao, val contactId: Long) : ViewModel() {


    val currentContact = MutableLiveData<ContactWithPhone>()


    private fun getCurrentContact(contactId: Long){
        CoroutineScope(Dispatchers.Main).launch {
            currentContact.value = getCurrentContactFromDb(contactId)
        }
    }

    private suspend fun getCurrentContactFromDb(contactId: Long) : ContactWithPhone {
        return withContext(Dispatchers.IO){
            dataSource.getContactById(contactId)
        }
    }

    init {
        getCurrentContact(contactId)
    }

    fun onSaveButtonClicked(name: String, phoneNumber1: String, phoneNumber2: String, email: String){

        var temp = ContactWithPhone(ContactDetails(currentContact.value?.contactDetails?.contactId ?: 0L, name, email),
            listOf(
                ContactPhoneNumber(currentContact.value?.phoneNumbers?.get(0)?.phoneId ?: 0L, currentContact.value?.contactDetails?.contactId ?: 0L, phoneNumber1),
                ContactPhoneNumber(currentContact.value?.phoneNumbers?.get(1)?.phoneId ?: 0L, currentContact.value?.contactDetails?.contactId ?: 0L, phoneNumber2)
            ))

        CoroutineScope(Dispatchers.IO).launch {
            dataSource.updateContact(temp)
        }
    }

}