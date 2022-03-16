package com.example.contactsapp.ui.contactsFragment

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.contactsapp.data.ContactsDataSource

class ContactsListFragmentViewModelFactory(
    val datasource: ContactsDataSource,
    val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactsListFragmentViewModel::class.java))
            return ContactsListFragmentViewModel(datasource, context) as T
        else
            throw IllegalArgumentException("Unknown ViewModel class")
    }
}