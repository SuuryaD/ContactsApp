package com.example.contactsapp.ui.dialFragment

import android.util.Log
import androidx.lifecycle.*
import com.example.contactsapp.data.ContactsDataSource
import com.example.contactsapp.data.Result
import com.example.contactsapp.data.database.ContactWithPhone

class DialViewModel(val dataSource: ContactsDataSource) : ViewModel() {


    val _contacts = dataSource.observeAllContacts().map {
        if (it is Result.Success)
            it.data
        else
            null
    }

//    val contacts: LiveData<List<ContactWithPhone>> = Transformations.map(_contacts){
//
//        val ls = ArrayList<ContactWithPhone>()
//
//        it?.let {
//
//            for(i in it){
//
//
//            }
//
//        }
//
//
//        return@map ls
//
//    }

    val phoneNumber = MutableLiveData<String>()
    val contacts: LiveData<ArrayList<ContactWithPhone>> = phoneNumber.switchMap {

//        Log.i("DialViewModel", it)
        val ls = ArrayList<ContactWithPhone>()

        if (it.isNullOrEmpty())
            return@switchMap liveData {
                emit(ls)
            }

//        Log.i("DialViewModel", _contacts.value.toString())

        val contacts2 = _contacts.value
            ?: return@switchMap liveData {
                emit(ls)
            }

        for (i in contacts2) {

            for (j in i.phoneNumbers) {

                if (j.phoneNumber.startsWith(it)) {
                    ls.add(i)
                    break
                }

            }

        }
//        Log.i("DialViewModel", ls.toString())

        return@switchMap liveData {
            emit(ls)
        }

    }

    fun getContact(input: String) {
        phoneNumber.value = input
    }

}