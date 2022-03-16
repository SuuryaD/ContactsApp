package com.example.contactsapp.ui.dialFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.contactsapp.data.ContactsDataSource

class DialViewModelFactory(val dataSource: ContactsDataSource) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DialViewModel::class.java))
            return DialViewModel(dataSource) as T
        else
            throw IllegalArgumentException("Unknown ViewModel class")
    }
}