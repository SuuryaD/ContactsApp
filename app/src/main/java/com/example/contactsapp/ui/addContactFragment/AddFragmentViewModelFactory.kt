package com.example.contactsapp.ui.addContactFragment

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.contactsapp.data.ContactsDataSource

class AddFragmentViewModelFactory(
    val datasource: ContactsDataSource,
    val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddFragmentViewModel::class.java))
            return AddFragmentViewModel(datasource, context) as T
        else
            throw IllegalArgumentException("Unknown ViewModel class")
    }
}