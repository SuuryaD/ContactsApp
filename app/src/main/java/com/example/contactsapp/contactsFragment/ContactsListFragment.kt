package com.example.contactsapp.contactsFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.contactsapp.R
import com.example.contactsapp.database.ContactDatabase
import com.example.contactsapp.databinding.FragmentContactsListBinding

class ContactsListFragment : Fragment() {

    private lateinit var binding: FragmentContactsListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contacts_list, container, false)

        val dataSource = ContactDatabase.getInstance(this.requireContext()).contactDetailsDao

        val viewModelFactory = ContactsListFragmentViewModelFactory(dataSource)

        val viewModel = ViewModelProvider(this,viewModelFactory).get(ContactsListFragmentViewModel::class.java)

        binding.contactsListViewModel = viewModel
        binding.setLifecycleOwner(this.viewLifecycleOwner)

        val res = dataSource.getAll()
        Log.i("MainActivity", "test: $res")
        res.observe(this.viewLifecycleOwner, Observer {
            Log.i("MainActivity", it.toString())
        })
        
        val adapter = ContactsAdapter2(ContactListener {
            contactWithPhone -> this.findNavController().navigate(ContactsListFragmentDirections.actionContactsFragmentToContactDetailFragment(contactWithPhone.contactDetails.contactId))
        })

        binding.contactList.adapter = adapter


        viewModel.contacts.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.navigateToAddContact.observe(this.viewLifecycleOwner, Observer { 
            if(it){
                this.findNavController().navigate(R.id.action_contactsFragment_to_addFragment)
                viewModel.doneNavigateToAddContact()
            }
        })
        
        return binding.root
    }



}