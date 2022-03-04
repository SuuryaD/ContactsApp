package com.example.contactsapp.ui.contactsFragment

import androidx.lifecycle.*
import com.example.contactsapp.data.ContactsDataSource
import com.example.contactsapp.data.Result
import com.example.contactsapp.data.database.*
import com.example.contactsapp.util.Event
import ezvcard.Ezvcard
import kotlinx.coroutines.*
import java.io.InputStream

class ContactsListFragmentViewModel(val datasource: ContactsDataSource) : ViewModel() {

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
                if(it[i -1].contactDetails.name.substring(0,1).lowercase() != it[i].contactDetails.name.substring(0,1).lowercase()){
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

    fun addContact(name: String, email: String, phoneNumbers: List<String>){

        val ls = ArrayList<ContactPhoneNumber>()
        for(i in phoneNumbers){
            ls.add(ContactPhoneNumber(phoneNumber = i))
        }

        val temp = ContactWithPhone(ContactDetails(name = name, email = email), ls)

        CoroutineScope(Dispatchers.Main).launch {

            val id = withContext(Dispatchers.IO){
                datasource.insert(temp)
            }

        }
    }

    fun importContacts(inputStream: InputStream?): Int{

        val vCards = Ezvcard.parse(inputStream).all()
        var cnt = 0
        for(i in vCards){

            val name = i.formattedName.value
            val number = i.telephoneNumbers.map {
                if(it.text.startsWith('+')){
//                    it.text.replace("[\\D]".toRegex(), "")
                    it.text.replaceAfter('+', it.text.replace("[\\D]".toRegex(), ""))
                }else{
                    it.text.replace("[\\D]".toRegex(), "")
                }

//                it.text
            }
            val email = i.emails.first().value

            if(name.isNullOrEmpty())
                continue

            cnt++
            addContact(name, email, number)
        }

        return cnt
    }


}