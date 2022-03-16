package com.example.contactsapp.ui.addContactFragment

import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.*
import com.example.contactsapp.data.ContactsDataSource
import com.example.contactsapp.data.database.*
import com.example.contactsapp.util.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddFragmentViewModel(private val dataSource: ContactsDataSource) : ViewModel() {

    private val _contactId: MutableLiveData<Long> = MutableLiveData<Long>()

    val currentContact = _contactId.switchMap { contactId ->
        dataSource.observeContactById(contactId)
    }

    fun setImageUri(uri: Uri) {
        userImage.value = uri.toString()
    }

    val userImage = currentContact.map {
        it?.contactDetails?.user_image ?: ""
    } as MutableLiveData<String>

    val name = currentContact.map {
        it?.contactDetails?.name ?: ""
    } as MutableLiveData<String>

    val email = currentContact.map {
        it?.contactDetails?.email ?: ""
    } as MutableLiveData<String>


    fun start(contactId: Long) {
        _contactId.value = contactId
    }

    private val _snackBarEvent = MutableLiveData<Event<String>>()
    val snackBarEvent: LiveData<Event<String>>
        get() = _snackBarEvent

    private val _navigateToContactDetail = MutableLiveData<Event<Pair<Long, String>>>()
    val navigateToContactDetail: LiveData<Event<Pair<Long, String>>>
        get() = _navigateToContactDetail


    fun isValuesChanged(phoneNumbers: List<String>): Boolean {

        if (currentContact.value != null) {

            if (name.value != currentContact.value?.contactDetails?.name) {
                return true
            }

            if (email.value != currentContact.value?.contactDetails?.email) {
                return true
            }

            if (phoneNumbers.size != currentContact.value?.phoneNumbers?.size)
                return true

            for (i in 0 until currentContact.value?.phoneNumbers?.size!!) {

                if (phoneNumbers.get(i) != currentContact.value?.phoneNumbers?.get(i)?.phoneNumber)
                    return true

            }
        } else {

            if (name.value?.isNotEmpty() == true || email.value?.isNotEmpty() == true || phoneNumbers.isNotEmpty())
                return true
        }
        return false
    }


    fun onSave(phoneNumbers: List<String>) {

        val name = name.value
        val email = email.value

        if (name == null || name.isEmpty()) {
            _snackBarEvent.value = Event("Name cannot be empty")
            return
        }

        if (email != null && email.isNotEmpty() && !email.matches(Patterns.EMAIL_ADDRESS.toRegex())) {
            _snackBarEvent.value = Event("Enter a valid Email")
            return
        }

        if (_contactId.value == 0L) {
            addContact(phoneNumbers)
        } else {
            updateContact(phoneNumbers)
        }

    }

    private fun addContact(phoneNumbers: List<String>) {

        val contactPhoneNumbers = ArrayList<ContactPhoneNumber>()

        for (i in phoneNumbers) {
            contactPhoneNumbers.add(ContactPhoneNumber(phoneNumber = i))
        }

        val newContact = ContactWithPhone(
            ContactDetails(
                name = name.value!!,
                email = email.value ?: "",
                user_image = userImage.value ?: ""
            ), contactPhoneNumbers
        )

        CoroutineScope(Dispatchers.Main).launch {

            val id = dataSource.insert(newContact)
            _navigateToContactDetail.value = Event(Pair(id, "Contact Added Successfully"))

        }

    }

    private fun updateContact(phoneNumbers: List<String>) {

        val contactPhoneNumbers = ArrayList<ContactPhoneNumber>()
        var i = 0

        while (i < currentContact.value?.phoneNumbers.orEmpty().size && i < phoneNumbers.size) {
            contactPhoneNumbers.add(
                ContactPhoneNumber(
                    currentContact.value?.phoneNumbers?.get(i)?.phoneId!!,
                    currentContact.value?.contactDetails?.contactId!!,
                    phoneNumbers.get(i)
                )
            )
            i++
        }

        val deleteList = ArrayList<ContactPhoneNumber>()

        while (i < currentContact.value?.phoneNumbers.orEmpty().size) {
            deleteList.add(currentContact.value?.phoneNumbers?.get(i)!!)
            i++
        }

        val newPhoneNumbers = ArrayList<ContactPhoneNumber>()

        while (i < phoneNumbers.size) {
            newPhoneNumbers.add(
                ContactPhoneNumber(
                    contactId = _contactId.value!!,
                    phoneNumber = phoneNumbers[i]
                )
            )
            i++
        }

        val updatedContact = ContactWithPhone(
            ContactDetails(
                contactId = _contactId.value!!,
                name = name.value!!, email = email.value!!,
                user_image = userImage.value ?: "",
                favorite = currentContact.value?.contactDetails?.favorite!!
            ),
            contactPhoneNumbers
        )

        CoroutineScope(Dispatchers.Main).launch {

            dataSource.updateContact(updatedContact)
            dataSource.insertPhoneNumbers(newPhoneNumbers)
            dataSource.deletePhoneNumbers(deleteList)
            _navigateToContactDetail.value =
                Event(Pair(_contactId.value!!, "Contact Updated Successfully"))
        }
    }

}

//
//@BindingAdapter("text2")
//fun customText(editText: EditText, text: String){
//    editText.text = Editable.Factory.getInstance().newEditable(text)
//}
//
//@InverseBindingAdapter(attribute = "text2")
//fun getText(editText: EditText) : String{
//    return editText.text.toString()
//}
//
//@BindingAdapter("dynamicList")
//fun dynamicList(linearLayout: LinearLayout, ls : MutableLiveData<ArrayList<MutableLiveData<String>>>){
//
//    Log.i("AddFragmentViewModel", "dynamicList: ${ls.value?.size}")
//    ls.value?.let {
//        for(i in it){
//            addView(linearLayout,i)
//        }
//        addView(linearLayout)
//    }
//
//}
//
//fun addView(linearLayout: LinearLayout, phoneNumber: MutableLiveData<String>? = null){
//    val v = EditPhoneRowBinding.inflate(LayoutInflater.from(linearLayout.context))
//    if(phoneNumber != null)
//        v.editTextPhone.text = Editable.Factory.getInstance().newEditable(phoneNumber.value.toString())
//    val cnt = linearLayout.childCount
//    v.editTextPhone.doAfterTextChanged {
//        if(it.toString().isNotEmpty() && linearLayout.childCount == cnt + 1){
//            addView(linearLayout)
//        }
//        if(linearLayout.childCount > 1 && it.toString().isEmpty() && linearLayout.childCount == cnt + 2){
//            linearLayout.removeViewAt(linearLayout.childCount - 1)
//        }
//    }
//    linearLayout.addView(v.root, linearLayout.childCount)
//}