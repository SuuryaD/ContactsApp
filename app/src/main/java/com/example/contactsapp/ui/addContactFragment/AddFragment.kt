package com.example.contactsapp.ui.addContactFragment

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.contactsapp.R
import com.example.contactsapp.data.database.ContactDatabase
import com.example.contactsapp.databinding.AddContactBinding
import com.example.contactsapp.databinding.EditPhoneRowBinding
import com.example.contactsapp.di.ServiceLocator

class AddFragment : Fragment() {

    private lateinit var binding: AddContactBinding
    private val args: AddFragmentArgs by navArgs()
    private val viewModel: AddFragmentViewModel by viewModels { AddFragmentViewModelFactory(
        ServiceLocator.provideContactsDataSource(requireContext())) }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.add_contact, container, false)

        viewModel.start(args.contactId)
        activity?.title = "Add/Edit Contacts"

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

        binding.button3.setOnClickListener {
            viewModel.onSave(binding.editTextTextPersonName5.text.toString(), binding.editTextTextEmailAddress.text.toString(), onSave())
//            this.findNavController().navigate(AddFragmentDirections.actionAddFragmentToContactDetailFragment(args.contactId))
        }


        viewModel.navigateToContacts.observe(this.viewLifecycleOwner, Observer {
            if(it){
                this.findNavController().navigate(AddFragmentDirections.actionAddFragmentToContactsFragment())
                viewModel.doneNavigation()
            }
        })

        viewModel.navigateToContactDetail.observe(this.viewLifecycleOwner){
            if(it){
                this.findNavController().navigate(AddFragmentDirections.actionAddFragmentToContactDetailFragment(args.contactId))
                viewModel.doneNavigationToDetail()
            }
        }

        return binding.root
    }

    private fun addView(phoneNumber: String? = null){
        val v = EditPhoneRowBinding.inflate(layoutInflater)
        if(phoneNumber != null)
            v.editTextPhone.text = Editable.Factory.getInstance().newEditable(phoneNumber)
        val cnt = binding.linearLayout.childCount
        v.editTextPhone.doAfterTextChanged {
                if(it.toString().isNotEmpty() && binding.linearLayout.childCount == cnt + 1){
                    addView()
                }
                if(binding.linearLayout.childCount > 1 && it.toString().isEmpty() && binding.linearLayout.childCount == cnt + 2){
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