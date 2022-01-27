package com.example.contactsapp.ui.contactDetailFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.contactsapp.data.database.ContactDetailsDao
import com.example.contactsapp.data.database.ContactsDataSource

class ContactDetailViewModelFactory(val dataSource: ContactsDataSource): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ContactDetailViewModel::class.java))
            return ContactDetailViewModel(dataSource) as T
        else
            throw IllegalArgumentException("Unknown ViewModel class")
    }

}