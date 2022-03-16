package com.example.contactsapp.ui.favoritesFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.contactsapp.data.ContactsDataSource
import com.example.contactsapp.data.database.ContactWithPhone
import com.example.contactsapp.util.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoritesViewModel(private val dataSource: ContactsDataSource) : ViewModel() {


    private val _navigateToContactDetail = MutableLiveData<Event<Long>>()
    val navigateToContactDetail: LiveData<Event<Long>>
        get() = _navigateToContactDetail


    fun navigateToContactDetail(contactId: Long) {
        _navigateToContactDetail.value = Event(contactId)
    }


    val favoriteContact = dataSource.getAllFavoriteContacts().map {
        it.filter { contactWithPhone ->
            if (contactWithPhone.phoneNumbers.isNullOrEmpty())
                return@filter false
            true
        }
    }


}