package com.example.contactsapp.ui.contactsFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.contactsapp.data.database.ContactDetailsDao
import com.example.contactsapp.data.database.ContactsDataSource

class ContactsListFragmentViewModelFactory(val datasource: ContactsDataSource) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ContactsListFragmentViewModel::class.java))
            return ContactsListFragmentViewModel(datasource) as T
        else
            throw IllegalArgumentException("Unknown ViewModel class")
    }
}