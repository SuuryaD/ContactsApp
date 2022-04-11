package com.example.contactsapp.ui.contactDetailFragment

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.contactsapp.data.ContactsDataSource

class ContactDetailViewModelFactory(val dataSource: ContactsDataSource, val context: Context) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactDetailViewModel::class.java))
            return ContactDetailViewModel(dataSource, context) as T
        else
            throw IllegalArgumentException("Unknown ViewModel class")
    }

}