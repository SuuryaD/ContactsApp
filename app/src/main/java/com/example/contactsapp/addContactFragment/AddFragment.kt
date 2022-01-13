package com.example.contactsapp.addContactFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.contactsapp.R
import com.example.contactsapp.database.ContactDatabase
import com.example.contactsapp.databinding.AddContactBinding

class AddFragment : Fragment() {

    private lateinit var binding: AddContactBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.add_contact, container, false)

        val dataSource = ContactDatabase.getInstance(this.requireContext()).contactDetailsDao
        val viewModel = ViewModelProvider(this,AddFragmentViewModelFactory(dataSource)).get(AddFragmentViewModel::class.java)

        binding.saveButton.setOnClickListener {
            viewModel.onSave(binding.nameText.text.toString(), binding.phoneNumber1.text.toString(), binding.phoneNumber2.text.toString(), binding.emailText.text.toString())
        }

        viewModel.navigateToContacts.observe(this.viewLifecycleOwner, Observer {
            if(it){
                this.findNavController().navigate(R.id.action_addFragment_to_contactsFragment)
                viewModel.doneNavigation()
            }
        })

        return binding.root
    }
}