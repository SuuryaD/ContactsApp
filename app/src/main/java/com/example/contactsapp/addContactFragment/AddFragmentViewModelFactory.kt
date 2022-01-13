package com.example.contactsapp.addContactFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.contactsapp.database.ContactDetailsDao

class AddFragmentViewModelFactory(val datasource: ContactDetailsDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AddFragmentViewModel::class.java))
            return AddFragmentViewModel(datasource) as T
        else
            throw IllegalArgumentException("Unknown ViewModel class")
    }
}