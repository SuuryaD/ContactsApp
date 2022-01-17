package com.example.contactsapp.editContactFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        binding.button.setOnClickListener {
            viewModel.onSaveButtonClicked(binding.editTextTextPersonName.text.toString(),
                binding.editTextTextPersonName2.text.toString(),
                binding.editTextTextPersonName3.text.toString(),
            binding.editTextTextPersonName4.text.toString())

            this.findNavController().navigate(EditContactFragmentDirections.actionEditContactFragmentToContactDetailFragment(contactId))
        }


//        viewModel.currentContact.observe(this.viewLifecycleOwner, Observer {
            Log.i("EditContactFragment", viewModel.currentContact.value.toString())
//        })

        return binding.root
    }

}