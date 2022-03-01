package com.example.contactsapp.ui.callHistory

import android.telecom.Call
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.contactsapp.data.ContactsDataSource
import com.example.contactsapp.domain.model.CallHistory
import com.example.contactsapp.domain.model.CallHistoryApi
import kotlinx.coroutines.*

class CallHistoryViewModel(private val dataSource: ContactsDataSource) : ViewModel() {


    private val _data = MutableLiveData<List<CallHistory>>()
    val callHistory: LiveData<List<CallHistory>>
        get() = _data

    fun getCallHistory(ls: List<CallHistoryApi>){

        CoroutineScope(Dispatchers.Main).launch{
            _data.value = dataSource.getContactNames(ls)
        }

    }

}