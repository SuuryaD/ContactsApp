package com.example.contactsapp.ui.contactsFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.contactsapp.R
import com.example.contactsapp.databinding.FragmentContactsListBinding
import com.example.contactsapp.di.ServiceLocator
import com.example.contactsapp.util.EventObserver

class ContactsListFragment : Fragment() {

    private lateinit var binding: FragmentContactsListBinding
    private val viewModel: ContactsListFragmentViewModel by viewModels { ContactsListFragmentViewModelFactory(ServiceLocator.provideContactsDataSource(requireContext())) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contacts_list, container, false)

        binding.contactsListViewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        (activity as AppCompatActivity).supportActionBar?.title = "Contacts"

        val adapter = ContactsAdapter2(ContactListener {
            contactWithPhone -> this.findNavController().navigate(ContactsListFragmentDirections.actionContactsFragmentToContactDetailFragment(contactWithPhone.contactDetails.contactId))
        })

        viewModel.contacts.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        binding.contactList.adapter = adapter

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        viewModel.navigateToAddContact.observe(this.viewLifecycleOwner, EventObserver {
                this.findNavController().navigate(ContactsListFragmentDirections.actionContactsFragmentToAddFragment(0L))

        })

    }

}