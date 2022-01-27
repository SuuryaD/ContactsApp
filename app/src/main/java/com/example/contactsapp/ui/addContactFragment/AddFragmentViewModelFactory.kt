package com.example.contactsapp.ui.addContactFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.contactsapp.data.database.ContactDetailsDao
import com.example.contactsapp.data.database.ContactsDataSource

class AddFragmentViewModelFactory(val datasource: ContactsDataSource) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AddFragmentViewModel::class.java))
            return AddFragmentViewModel(datasource) as T
        else
            throw IllegalArgumentException("Unknown ViewModel class")
    }
}