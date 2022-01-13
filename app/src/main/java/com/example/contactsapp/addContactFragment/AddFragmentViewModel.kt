package com.example.contactsapp.addContactFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.contactsapp.database.ContactDetails
import com.example.contactsapp.database.ContactDetailsDao
import com.example.contactsapp.database.ContactPhoneNumber
import com.example.contactsapp.database.ContactWithPhone
import com.example.contactsapp.model.ContactDetail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddFragmentViewModel(val dataSource: ContactDetailsDao) : ViewModel() {

    private val _navigateToContacts = MutableLiveData<Boolean>()
    val navigateToContacts: LiveData<Boolean>
    get() = _navigateToContacts


    init {
        _navigateToContacts.value = false
    }


    fun onSave(name: String, phoneNumber1: String, phoneNumber2: String, email: String ){
        val temp = ContactDetail(0L, name, email, listOf(phoneNumber1, phoneNumber2))
        CoroutineScope(Dispatchers.Main).launch{
            save(temp)
            _navigateToContacts.value = true
        }
    }

    private suspend fun save(contactDetail: ContactDetail){
        withContext(Dispatchers.IO){
            dataSource.insert(contactDetail)
        }
    }

    fun doneNavigation() {
        _navigateToContacts.value = false
    }

}