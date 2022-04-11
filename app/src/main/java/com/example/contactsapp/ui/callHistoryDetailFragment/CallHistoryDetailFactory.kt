package com.example.contactsapp.ui.callHistoryDetailFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.contactsapp.data.ContactsDataSource
import com.example.contactsapp.domain.model.CallHistoryData
import java.lang.IllegalArgumentException

class CallHistoryDetailFactory(val dataSource: ContactsDataSource, val callHistoryData: CallHistoryData) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CallHistoryDetailViewModel::class.java))
            return CallHistoryDetailViewModel(dataSource, callHistoryData) as T
        else
            throw IllegalArgumentException("Unknown class")
    }
}