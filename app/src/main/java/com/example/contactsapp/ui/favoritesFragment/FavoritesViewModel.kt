package com.example.contactsapp.ui.favoritesFragment

import androidx.lifecycle.*
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

    fun removeFavorite(contactId: Long){
        viewModelScope.launch {
            dataSource.removeFavorite(contactId)
        }
    }


    val favoriteContact = dataSource.getAllFavoriteContacts().map {
        it.filter { contactWithPhone ->
            if (contactWithPhone.phoneNumbers.isNullOrEmpty())
                return@filter false
            true
        }
    }


}