package com.example.contactsapp.editContactFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.contactsapp.database.ContactDetailsDao
import java.lang.IllegalArgumentException

class EditContactViewModelFactory(val dataSource: ContactDetailsDao,
                                  val contactId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(EditContactViewModel::class.java))
            return EditContactViewModel(dataSource, contactId) as T
        else
            throw IllegalArgumentException("Unknow view model class")
    }
}