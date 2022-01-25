package com.example.contactsapp.editContactFragment

import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.contactsapp.R
import com.example.contactsapp.contactDetailFragment.ContactDetailFragmentArgs
import com.example.contactsapp.contactDetailFragment.ContactDetailViewModel
import com.example.contactsapp.contactDetailFragment.ContactDetailViewModelFactory
import com.example.contactsapp.database.ContactDatabase
import com.example.contactsapp.databinding.EditPhoneRowBinding
import com.example.contactsapp.databinding.FragmentEditContactBinding

class EditContactFragment : Fragment() {

    private lateinit var binding: FragmentEditContactBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_contact, container, false)

        val dataSource = ContactDatabase.getInstance(this.requireContext()).contactDetailsDao
        val args : ContactDetailFragmentArgs by navArgs()
        val contactId = args.contactId

        val viewModel = ViewModelProvider(this,
            EditContactViewModelFactory(dataSource,contactId)
        ).get(EditContactViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        viewModel.currentContact.observe(viewLifecycleOwner, Observer {


            for(i in it.phoneNumbers){
                addView(i.phoneNumber)
            }
        })

        binding.editSaveButton.setOnClickListener {
            viewModel.onSaveButtonClicked(binding.editTextTextPersonName.text.toString(), binding.editTextTextEmailAddress2.text.toString(), onSave())
            this.findNavController().navigate(EditContactFragmentDirections.actionEditContactFragmentToContactDetailFragment(viewModel.contactId))
        }


//        viewModel.currentContact.observe(this.viewLifecycleOwner, Observer {
            Log.i("EditContactFragment", viewModel.currentContact.value.toString())
//        })

        return binding.root
    }


    private fun addView(phoneNumber: String){
        val v = EditPhoneRowBinding.inflate(layoutInflater)
        v.editTextPhone.text = Editable.Factory.getInstance().newEditable(phoneNumber)
        binding.linearLayout2.addView(v.root, binding.linearLayout2.childCount)
    }

    private fun onSave() : List<String>{
        val ls = ArrayList<String>()

        val count = binding.linearLayout2.childCount

        for(i in 0 until count){

            val v = binding.linearLayout2.getChildAt(i)
            val phone = v.findViewById<EditText>(R.id.editTextPhone).text.toString()

            if(phone.isNotEmpty()){
                ls.add(phone)
            }
        }
        return ls

    }

}