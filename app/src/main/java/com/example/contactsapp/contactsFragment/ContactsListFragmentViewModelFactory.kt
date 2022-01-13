package com.example.contactsapp.contactsFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.contactsapp.database.ContactDetailsDao

class ContactsListFragmentViewModelFactory(val datasource: ContactDetailsDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ContactsListFragmentViewModel::class.java))
            return ContactsListFragmentViewModel(datasource) as T
        else
            throw IllegalArgumentException("Unknown ViewModel class")
    }
}