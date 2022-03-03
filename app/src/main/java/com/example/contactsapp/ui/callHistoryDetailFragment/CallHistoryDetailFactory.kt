package com.example.contactsapp.ui.callHistoryDetailFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.contactsapp.data.ContactsDataSource
import com.example.contactsapp.domain.model.CallHistory
import java.lang.IllegalArgumentException

class CallHistoryDetailFactory(val dataSource: ContactsDataSource, val callHistory: CallHistory): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(CallHistoryDetailViewModel::class.java))
            return CallHistoryDetailViewModel(dataSource, callHistory) as T
        else
            throw IllegalArgumentException("Unknown class")
    }
}