package com.example.contactsapp.ui.addContactFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.contactsapp.data.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddFragmentViewModel(private val dataSource: ContactsDataSource) : ViewModel() {

    private val _contactId: MutableLiveData<Long> = MutableLiveData<Long>()

    val currentContact = _contactId.switchMap {
       dataSource.observeContactById(it)
    }

    fun start(contactId: Long){
        _contactId.value = contactId
    }

    private val _navigateToContacts = MutableLiveData<Boolean>()
    val navigateToContacts: LiveData<Boolean>
    get() = _navigateToContacts

    private val _navigateToContactDetail = MutableLiveData<Boolean>()
    val navigateToContactDetail: LiveData<Boolean>
            get() = _navigateToContactDetail

    init {
        _navigateToContacts.value = false
    }


    fun onSave(name: String, email: String, phoneNumbers: List<String> ){


        if(_contactId.value == 0L){
            addContact(name,email, phoneNumbers)
            _navigateToContacts.value = true

        }else{

            updateContact(name, email, phoneNumbers)
            _navigateToContactDetail.value = true
        }
//        _navigateToContacts.value = true
    }

    private fun addContact(name: String, email: String, phoneNumbers: List<String>){
        val ls = ArrayList<ContactPhoneNumber>()

        for(i in phoneNumbers){
            ls.add(ContactPhoneNumber(phoneNumber = i))
        }

        val temp = ContactWithPhone(ContactDetails(name = name, email = email), ls)

        CoroutineScope(Dispatchers.IO).launch {
            dataSource.insert(temp)
        }

    }


    private fun updateContact(name: String, email: String, phoneNumbers: List<String>){

        val ls = ArrayList<ContactPhoneNumber>()
        var i = 0

        while(i < currentContact.value?.phoneNumbers.orEmpty().size && i < phoneNumbers.size ){
            ls.add(ContactPhoneNumber(currentContact.value?.phoneNumbers?.get(i)?.phoneId!!, currentContact.value?.contactDetails?.contactId!!,phoneNumbers.get(i)))
            i++
        }
        Log.i("AddFragmentViewModel", "$i - ${phoneNumbers.size}")

        val deleteList = ArrayList<ContactPhoneNumber>()

        while(i < currentContact.value?.phoneNumbers.orEmpty().size){
            deleteList.add(currentContact.value?.phoneNumbers?.get(i)!!)
            i++
        }

        val newPhoneNumbers = ArrayList<ContactPhoneNumber>()

        while(i < phoneNumbers.size){
            newPhoneNumbers.add(ContactPhoneNumber(contactId = _contactId.value!!, phoneNumber = phoneNumbers[i]))
            i++
        }

        Log.i("AddFragmentViewModel", "Old: ${ls.toString()}, New: ${newPhoneNumbers.toString()}, Delete: ${deleteList.toString()}")

        val temp = ContactWithPhone(ContactDetails(contactId= _contactId.value!!, name = name, email = email), ls)

        CoroutineScope(Dispatchers.IO).launch {
            dataSource.updateContact(temp)
            dataSource.insertPhoneNumbers(newPhoneNumbers)
            dataSource.deletePhoneNumbers(deleteList)
        }


    }

    private suspend fun save(contactWithPhone: ContactWithPhone){
        withContext(Dispatchers.IO){
            dataSource.insert(contactWithPhone)
        }
    }

    fun doneNavigation() {
        _navigateToContacts.value = false
    }

    fun doneNavigationToDetail() {
        _navigateToContactDetail.value = false
    }

}