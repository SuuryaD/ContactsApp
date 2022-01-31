package com.example.contactsapp.ui.contactsFragment

import androidx.lifecycle.*
import com.example.contactsapp.data.ContactsDataSource
import com.example.contactsapp.data.Result
import com.example.contactsapp.data.database.*
import com.example.contactsapp.util.Event
import kotlinx.coroutines.*

class ContactsListFragmentViewModel(datasource: ContactsDataSource) : ViewModel() {

    private val _contacts = datasource.observeAllContacts().map{
        if(it is Result.Success)
            it.data
        else
            null
    }

    val contacts = Transformations.map(_contacts){
        val ls = ArrayList<ContactWithPhone>()
        it?.let {
            if(it.isEmpty())
                return@map ls
            ls.add(
                ContactWithPhone(ContactDetails(name = it[0].contactDetails.name.substring(0,1).uppercase(), email = ""),
                listOf(ContactPhoneNumber(phoneNumber = ""))))
            ls.add(it[0])
            for(i in 1 until it.size){
                if(it[i -1].contactDetails.name.substring(0,1) != it[i].contactDetails.name.substring(0,1)){
                    ls.add(ContactWithPhone(ContactDetails(name = it[i].contactDetails.name.substring(0,1).uppercase(), email = ""),
                        listOf(ContactPhoneNumber(phoneNumber = ""))))
                    ls.add(it[i])
                }
                else{
                    ls.add(it[i])
                }
            }
            return@map ls
        }
    }

    private val _navigateToAddContact = MutableLiveData<Event<Unit>>()
    val navigateToAddContact: LiveData<Event<Unit>>
        get() = _navigateToAddContact

    fun onAddButtonClick() {
        _navigateToAddContact.value = Event(Unit)
    }


}