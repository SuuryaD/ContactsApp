package com.example.contactsapp.contactsFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.contactsapp.database.ContactDetailsDao
import com.example.contactsapp.database.ContactWithPhone
import com.example.contactsapp.database.asModel
import com.example.contactsapp.model.ContactDetail

class ContactsListFragmentViewModel(datasource : ContactDetailsDao) : ViewModel() {

    private val _contacts: LiveData<List<ContactWithPhone>> = datasource.getAll()

    val contacts = Transformations.map(_contacts) {
        val ls = ArrayList<ContactDetail>()
        for(i in it){
            ls.add(ContactDetail(i.contactDetails.contactId, i.contactDetails.name, i.contactDetails.email, i.phoneNumbers))
//            ls.add(i.asModel())
        }
        return@map ls
    }

    private val _navigateToAddContact = MutableLiveData<Boolean>()
    val navigateToAddContact: LiveData<Boolean>
    get() = _navigateToAddContact


    init {
        _navigateToAddContact.value = false
    }



    fun doneNavigateToAddContact(){
        _navigateToAddContact.value = false
    }

    fun onAddButtonClick(){
        _navigateToAddContact.value = true
    }


}