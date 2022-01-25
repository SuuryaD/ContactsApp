package com.example.contactsapp.contactsFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.contactsapp.database.ContactDetails
import com.example.contactsapp.database.ContactDetailsDao
import com.example.contactsapp.database.ContactPhoneNumber
import com.example.contactsapp.database.ContactWithPhone
import com.example.contactsapp.model.ContactDetail

class ContactsListFragmentViewModel(datasource: ContactDetailsDao) : ViewModel() {

    val contacts: LiveData<List<ContactWithPhone>> = datasource.getAll()

    val _contacts = MutableLiveData(datasource.getAll().value)

    val contacts2 = Transformations.map(contacts){
        val ls = ArrayList<ContactWithPhone>()
        it?.let {
            if(it.isEmpty())
                return@map ls
            ls.add(
                ContactWithPhone(ContactDetails(name = it[0].contactDetails.name.substring(0,1), email = ""),
                listOf(ContactPhoneNumber(phoneNumber = ""))))
            ls.add(it[0])
            for(i in 1 until it.size){
                if(it[i -1].contactDetails.name.substring(0,1) != it[i].contactDetails.name.substring(0,1)){
                    ls.add(ContactWithPhone(ContactDetails(name = it[i].contactDetails.name.substring(0,1), email = ""),
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

//    val contacts = Transformations.map(_contacts) {
//        val ls = ArrayList<ContactDetail>()
//        for(i in it){
//            ls.add(ContactDetail(i.contactDetails.contactId, i.contactDetails.name, i.contactDetails.email, i.phoneNumbers))
////            ls.add(i.asModel())
//        }
//        return@map ls
//    }

    private val _navigateToAddContact = MutableLiveData<Boolean>()
    val navigateToAddContact: LiveData<Boolean>
        get() = _navigateToAddContact


    init {
        _navigateToAddContact.value = false
    }


    fun doneNavigateToAddContact() {
        _navigateToAddContact.value = false
    }

    fun onAddButtonClick() {
        _navigateToAddContact.value = true
    }


}