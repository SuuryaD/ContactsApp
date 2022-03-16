package com.example.contactsapp.ui.favoritesFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.contactsapp.data.ContactsDataSource

class FavoritesViewModelFactory(val dataSource: ContactsDataSource) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java))
            return FavoritesViewModel(dataSource) as T
        else
            throw IllegalArgumentException("Unknown ViewModel class")
    }
}