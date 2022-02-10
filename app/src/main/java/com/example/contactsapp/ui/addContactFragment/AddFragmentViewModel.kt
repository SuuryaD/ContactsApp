package com.example.contactsapp.ui.addContactFragment

import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.*
import com.example.contactsapp.data.ContactsDataSource
import com.example.contactsapp.data.Result
import com.example.contactsapp.data.database.*
import com.example.contactsapp.util.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddFragmentViewModel(private val dataSource: ContactsDataSource) : ViewModel() {

    private val _contactId: MutableLiveData<Long> = MutableLiveData<Long>()

//    var user_image: Uri? = null

    val currentContact = _contactId.switchMap { contactId ->
        dataSource.observeContactById(contactId).map{ computeResult(it) }
    }

    private fun computeResult(data: Result<ContactWithPhone>) : ContactWithPhone?{

        return if(data is Result.Success ) {
            data.data
        }else {
            null
        }
    }

    fun setImageUri(uri: Uri){
//        user_image = uri
        userImage.value = uri.toString()
    }

    val userImage = currentContact.map {
        it?.contactDetails?.user_image ?: ""
    } as MutableLiveData<String>

    val name = currentContact.map {
        it?.contactDetails?.name ?: ""
    } as MutableLiveData<String>

    val email = currentContact.map{
        it?.contactDetails?.email ?: ""
    } as MutableLiveData<String>


    fun start(contactId: Long){
        _contactId.value = contactId
    }

    private val _snackBarEvent = MutableLiveData<Event<String>>()
    val snackBarEvent: LiveData<Event<String>>
        get() = _snackBarEvent

    private val _invalidEmailSnackBar = MutableLiveData<Event<String>>()
    val invalidEmailSnackBar : LiveData<Event<String>>
        get() = _invalidEmailSnackBar


    private val _navigateToContactDetail = MutableLiveData<Event<Unit>>()
    val navigateToContactDetail : LiveData<Event<Unit>>
        get() = _navigateToContactDetail

    private val _navigateToContacts = MutableLiveData<Event<Unit>>()
    val navigateToContacts: LiveData<Event<Unit>>
        get() = _navigateToContacts


    fun onSave(phoneNumbers: List<String> ){

        val name = name.value
        val email = email.value

        if(name == null || name.isEmpty()){
            _snackBarEvent.value = Event("Name cannot be empty")
            return
        }

        if(email != null && !email.matches(Patterns.EMAIL_ADDRESS.toRegex())){
            _invalidEmailSnackBar.value = Event("Enter a valid Email")
            return
        }

        if(_contactId.value == 0L){
            addContact(phoneNumbers)
            _navigateToContacts.value = Event(Unit)

        }else{
            updateContact(phoneNumbers)
            _navigateToContactDetail.value = Event(Unit)
        }

    }

    private fun addContact( phoneNumbers: List<String>){

        val ls = ArrayList<ContactPhoneNumber>()

        for(i in phoneNumbers){
            ls.add(ContactPhoneNumber(phoneNumber = i))
        }

        val temp = ContactWithPhone(ContactDetails(name = name.value ?: "", email = email.value ?: "", user_image = userImage.value), ls)

        CoroutineScope(Dispatchers.IO).launch {
            dataSource.insert(temp)
        }

    }


    private fun updateContact(phoneNumbers: List<String>){

        val ls = ArrayList<ContactPhoneNumber>()
        var i = 0

        while(i < currentContact.value?.phoneNumbers.orEmpty().size && i < phoneNumbers.size ){
            ls.add(ContactPhoneNumber(currentContact.value?.phoneNumbers?.get(i)?.phoneId!!, currentContact.value?.contactDetails?.contactId!!,phoneNumbers.get(i)))
            i++
        }

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

        val temp = ContactWithPhone(
            ContactDetails(contactId= _contactId.value!!,
                name = name.value!!, email = email.value!!,
                user_image = userImage.value),
            ls)

        CoroutineScope(Dispatchers.IO).launch {
            dataSource.updateContact(temp)
            dataSource.insertPhoneNumbers(newPhoneNumbers)
            dataSource.deletePhoneNumbers(deleteList)
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