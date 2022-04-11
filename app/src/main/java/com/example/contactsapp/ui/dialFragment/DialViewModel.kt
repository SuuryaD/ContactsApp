package com.example.contactsapp.ui.dialFragment

import android.util.Log
import androidx.lifecycle.*
import com.example.contactsapp.data.ContactsDataSource
import com.example.contactsapp.data.Result
import com.example.contactsapp.data.database.ContactDetails
import com.example.contactsapp.data.database.ContactWithPhone
import com.example.contactsapp.domain.model.ContactDetail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DialViewModel(val dataSource: ContactsDataSource) : ViewModel() {


    val _contacts = dataSource.observeAllContacts().map {
        if (it is Result.Success)
            it.data
        else
            null
    }

    val phoneNumber = MutableLiveData<String>()
//    val contacts: LiveData<ArrayList<ContactWithPhone>> = phoneNumber.switchMap {
//
////        Log.i("DialViewModel", it)
//        val ls = ArrayList<ContactWithPhone>()
//
//        if (it.isNullOrEmpty())
//            return@switchMap liveData {
//                emit(ls)
//            }
//
//        val contacts2 = _contacts.value
//            ?: return@switchMap liveData {
//                emit(ls)
//            }
//
//        for (i in contacts2) {
//
//            for (j in i.phoneNumbers) {
//
//                if (j.phoneNumber.contains(it)) {
//                    ls.add(i)
//                    break
//                }
//            }
//        }
//
//        return@switchMap liveData {
//            emit(ls)
//        }
//
//    }

//    val contacts2: LiveData<ArrayList<ContactDetail>> = phoneNumber.switchMap {
//
////        Log.i("DialViewModel", it)
//        val ls = ArrayList<ContactDetail>()
//
//        if (it.isNullOrEmpty())
//            return@switchMap liveData {
//                emit(ls)
//            }
//
//        val contacts2 = _contacts.value
//            ?: return@switchMap liveData {
//                emit(ls)
//            }
//
//        for (i in contacts2) {
//
//            for (j in i.phoneNumbers) {
//
//                if (j.phoneNumber.contains(it)) {
//                    ls.add(ContactDetail(
//                        i.contactDetails.contactId,
//                        i.contactDetails.name,
//                        i.contactDetails.email,
//                        i.contactDetails.user_image,
//                        j.phoneNumber
//                    ))
//                    break
//                }
//            }
//        }
//
//        return@switchMap liveData {
//            emit(ls)
//        }
//
//    }

    val contacts3 = MutableLiveData<ArrayList<ContactDetail>>()

    fun getContact(input: String) {

        CoroutineScope(Dispatchers.IO).launch {
            val ls = ArrayList<ContactDetail>()

            if (input.isNullOrEmpty()){
                return@launch withContext(Dispatchers.Main){
                    contacts3.value = ls
                    return@withContext
                }
            }


            val contacts2 = _contacts.value
                ?: return@launch withContext(Dispatchers.Main){
                    contacts3.value = ls
                    return@withContext
                }

            for (i in contacts2) {

                for (j in i.phoneNumbers) {

                    if (j.phoneNumber.contains(input)) {
                        ls.add(ContactDetail(
                            i.contactDetails.contactId,
                            i.contactDetails.name,
                            i.contactDetails.email,
                            i.contactDetails.user_image,
                            i.contactDetails.color_code,
                            j.phoneNumber
                        ))
                        break
                    }
                }
            }

            return@launch withContext(Dispatchers.Main){
                contacts3.value = ls
                return@withContext
            }
        }

//        phoneNumber.value = input
    }

//    fun getContactCorout(input: String){
//
//    }

}