package com.example.contactsapp.addContactFragment

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.contactsapp.MainActivity
import com.example.contactsapp.R
import com.example.contactsapp.database.ContactDatabase
import com.example.contactsapp.databinding.AddContactBinding
import com.example.contactsapp.databinding.EditPhoneRowBinding

class AddFragment : Fragment() {

    private lateinit var binding: AddContactBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.add_contact, container, false)

        val args: AddFragmentArgs by navArgs()
        val contactId = args.contactId

        val dataSource = ContactDatabase.getInstance(this.requireContext()).contactDetailsDao
        val viewModel = ViewModelProvider(this,AddFragmentViewModelFactory(dataSource, contactId)).get(AddFragmentViewModel::class.java)

        activity?.title = "Add/Edit Contacts"
//        binding.saveButton.setOnClickListener {
//            viewModel.onSave(binding.nameText.text.toString(), binding.phoneNumber1.text.toString(), binding.phoneNumber2.text.toString(), binding.emailText.text.toString())
//        }

        viewModel.currentContact.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.editTextTextPersonName5.text = Editable.Factory.getInstance().newEditable(it.contactDetails.name)
                binding.editTextTextEmailAddress.text = Editable.Factory.getInstance().newEditable(it.contactDetails.email)
                it.phoneNumbers.forEach {
                    addView(it.phoneNumber)
                }
            }
            addView()
        })


//        addView()

        binding.button3.setOnClickListener {
            viewModel.onSave(binding.editTextTextPersonName5.text.toString(), binding.editTextTextEmailAddress.text.toString(), onSave())
        }



        viewModel.navigateToContacts.observe(this.viewLifecycleOwner, Observer {
            if(it){
                this.findNavController().navigate(R.id.action_addFragment_to_contactsFragment)
                viewModel.doneNavigation()
            }
        })

        return binding.root
    }



    private fun addView(){
        val v = EditPhoneRowBinding.inflate(layoutInflater)
        val cnt = binding.linearLayout.childCount
        v.editTextPhone.doAfterTextChanged {

            if(it.toString().isNotEmpty() && binding.linearLayout.childCount == cnt + 1){
                addView()
            }
            if(binding.linearLayout.childCount > 1 && it.toString().isEmpty()){
                binding.linearLayout.removeViewAt(binding.linearLayout.childCount - 1)
            }
        }
        binding.linearLayout.addView(v.root, binding.linearLayout.childCount)
    }

    private fun addView(phoneNumber: String){
        val v = EditPhoneRowBinding.inflate(layoutInflater)
        v.editTextPhone.text = Editable.Factory.getInstance().newEditable(phoneNumber)
        val cnt = binding.linearLayout.childCount
        v.editTextPhone.doAfterTextChanged {

            if(it.toString().isNotEmpty() && binding.linearLayout.childCount == cnt + 1){
                addView()
            }
            if(binding.linearLayout.childCount > 1 && it.toString().isEmpty()){
                binding.linearLayout.removeViewAt(binding.linearLayout.childCount - 1)
            }
        }
        binding.linearLayout.addView(v.root, binding.linearLayout.childCount)
    }

    private fun onSave() : List<String>{
        val ls = ArrayList<String>()

        val count = binding.linearLayout.childCount

        for(i in 0 until count){

            val v = binding.linearLayout.getChildAt(i)
            val phone = v.findViewById<EditText>(R.id.editTextPhone).text.toString()

            if(phone.isNotEmpty()){
                ls.add(phone)
            }
        }
        return ls
    }
}