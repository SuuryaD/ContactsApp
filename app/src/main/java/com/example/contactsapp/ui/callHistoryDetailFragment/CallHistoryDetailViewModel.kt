package com.example.contactsapp.ui.callHistoryDetailFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactsapp.data.ContactsDataSource
import com.example.contactsapp.domain.model.CallHistoryData
import com.example.contactsapp.util.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CallHistoryDetailViewModel(val dataSource: ContactsDataSource, val callHistoryData: CallHistoryData) :
    ViewModel() {

    //    private val _callHistory = MutableLiveData<CallHistory>()
//    val callHistory: LiveData<CallHistory>
//        get() = _callHistory
//
    val callHistoryLiv = dataSource.getContactFromPhone(callHistoryData)

    private val _sendMessage = MutableLiveData<Event<String>>()
    val sendMessage: LiveData<Event<String>>
        get() = _sendMessage

    private val _makeCall = MutableLiveData<Event<Unit>>()
    val makeCall: LiveData<Event<Unit>>
        get() = _makeCall

    private val _createNewContact = MutableLiveData<Event<Unit>>()
    val createNewContact: LiveData<Event<Unit>>
        get() = _createNewContact

    private val _addToContact = MutableLiveData<Event<Unit>>()
    val addToContact: LiveData<Event<Unit>>
        get() = _addToContact

    private val _deleteCallHistory = MutableLiveData<Event<Unit>>()
    val deleteCallHistory: LiveData<Event<Unit>>
        get() = _deleteCallHistory


    fun sendMessage(phoneNumber: String) {
        _sendMessage.value = Event(phoneNumber)
    }

    fun makeCall() {
        _makeCall.value = Event(Unit)
    }

    fun createNewContact() {
        _createNewContact.value = Event(Unit)
    }

    fun addToContact() {
        _addToContact.value = Event(Unit)
    }

    fun deleteCallHistory() {
        _deleteCallHistory.value = Event(Unit)
    }

    fun deleteCallHistory(id: Long){

        CoroutineScope(Dispatchers.Main).launch {
            dataSource.deleteCallHistory(id)
        }

    }

}