package com.example.contactsapp.ui.callHistory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.contactsapp.data.ContactsDataSource

class CallHistoryViewModelFactory(val dataSource: ContactsDataSource, val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CallHistoryViewModel::class.java))
            return CallHistoryViewModel(dataSource, context) as T
        else
            throw IllegalArgumentException("Unknown ViewModel class")
    }
}