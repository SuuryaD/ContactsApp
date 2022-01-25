package com.example.contactsapp.addContactFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.contactsapp.database.ContactDetails
import com.example.contactsapp.database.ContactDetailsDao
import com.example.contactsapp.database.ContactPhoneNumber
import com.example.contactsapp.database.ContactWithPhone
import com.example.contactsapp.model.ContactDetail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddFragmentViewModel(val dataSource: ContactDetailsDao, val contactId: Long) : ViewModel() {


    val currentContact = MutableLiveData<ContactWithPhone>()


    private fun getCurrentContact(contactId: Long){
        CoroutineScope(Dispatchers.Main).launch {
            currentContact.value = getCurrentContactFromDb(contactId)
        }
    }

    private suspend fun getCurrentContactFromDb(contactId: Long) : ContactWithPhone{
        return withContext(Dispatchers.IO){
            dataSource.getContactById(contactId)
        }
    }

    init{
        getCurrentContact(contactId)
    }

    private val _navigateToContacts = MutableLiveData<Boolean>()
    val navigateToContacts: LiveData<Boolean>
    get() = _navigateToContacts

    init {
        _navigateToContacts.value = false
    }


    fun onSave(name: String, email: String, phoneNumbers: List<String> ){


        if(contactId == 0L){
            val ls = ArrayList<ContactPhoneNumber>()

            for(i in phoneNumbers){
                ls.add(ContactPhoneNumber(phoneNumber = i))
            }

            val temp = ContactWithPhone(ContactDetails(name = name, email = email), ls)

            CoroutineScope(Dispatchers.IO).launch {
                dataSource.insert(temp)
            }

        }else{
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
                newPhoneNumbers.add(ContactPhoneNumber(contactId = contactId, phoneNumber = phoneNumbers[i]))
                i++
            }

            Log.i("AddFragmentViewModel", "Old: ${ls.toString()}, New: ${newPhoneNumbers.toString()}, Delete: ${deleteList.toString()}")

            val temp = ContactWithPhone(ContactDetails(contactId= contactId, name = name, email = email), ls)

            CoroutineScope(Dispatchers.IO).launch {
                dataSource.updateContact(temp)
                dataSource.insertPhoneNumbers(newPhoneNumbers)
                dataSource.deletePhoneNumber(deleteList)
            }
        }
        _navigateToContacts.value = true
//        val ls = ArrayList<ContactPhoneNumber>()
//
//
//        var i = 0
//
////        for(i in 0..currentContact.value?.phoneNumbers.orEmpty().size){
////            ls.add(ContactPhoneNumber(currentContact.value?.phoneNumbers?.get(i)?.phoneId!!, currentContact.value?.contactDetails?.contactId!!, ))
////        }
//
//        while(i < currentContact.value?.phoneNumbers.orEmpty().size && i < phoneNumbers.size ){
//            ls.add(ContactPhoneNumber(currentContact.value?.phoneNumbers?.get(i)?.phoneId!!, currentContact.value?.contactDetails?.contactId!!,phoneNumbers.get(i)))
//            i++
//        }
//        Log.i("AddFragmentViewModel", "$i - ${phoneNumbers.size}")
//
//        while(i < phoneNumbers.size){
//            ls.add(ContactPhoneNumber(phoneNumber = phoneNumbers[i]))
//            i++
//        }
//
//        Log.i("AddFragmentViewModel", ls.toString())
//
//        val temp = ContactWithPhone(ContactDetails(name = name, email = email), listOf())
//
//        CoroutineScope(Dispatchers.Main).launch{
////            save(temp)
//            _navigateToContacts.value = true
//        }
    }

    private suspend fun save(contactWithPhone: ContactWithPhone){
        withContext(Dispatchers.IO){
            dataSource.insert(contactWithPhone)
        }
    }

    fun doneNavigation() {
        _navigateToContacts.value = false
    }

}