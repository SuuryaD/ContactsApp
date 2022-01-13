package com.example.contactsapp.contactDetailFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.contactsapp.database.ContactDetailsDao

class ContactDetailViewModelFactory(val dataSource: ContactDetailsDao, val contactId: Long): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ContactDetailViewModel::class.java))
            return ContactDetailViewModel(dataSource, contactId) as T
        else
            throw IllegalArgumentException("Unknown ViewModel class")
    }

}