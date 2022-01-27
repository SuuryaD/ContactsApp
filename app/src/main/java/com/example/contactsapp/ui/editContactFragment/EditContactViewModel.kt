package com.example.contactsapp.ui.editContactFragment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.contactsapp.data.database.*
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

    fun onSaveButtonClicked(name: String, email: String, phoneNumbers: List<String>){


        val ls = ArrayList<ContactPhoneNumber>()

       phoneNumbers.forEach {
           ls.add(ContactPhoneNumber(phoneNumber = it))
       }

        var i = 0

//        for(i in 0..currentContact.value?.phoneNumbers.orEmpty().size){
//            ls.add(ContactPhoneNumber(currentContact.value?.phoneNumbers?.get(i)?.phoneId!!, currentContact.value?.contactDetails?.contactId!!, ))
//        }

        while(i < currentContact.value?.phoneNumbers.orEmpty().size && i < phoneNumbers.size ){
            ls.add(ContactPhoneNumber(currentContact.value?.phoneNumbers?.get(i)?.phoneId!!, currentContact.value?.contactDetails?.contactId!!,phoneNumbers.get(i)))
            i++
        }
        Log.i("EditContactViewModel", "$i - ${phoneNumbers.size}")

        while(i < phoneNumbers.size){
            ls.add(ContactPhoneNumber(phoneNumber = phoneNumbers[i]))
            i++
        }

        Log.i("OnSaveButtonClicked", ls.toString())

        val temp = ContactWithPhone(ContactDetails(contactId = currentContact.value?.contactDetails?.contactId ?: 0L, name = name, email = email), listOf())

        CoroutineScope(Dispatchers.IO).launch {
            dataSource.updateContact2(temp)
        }

    }

}