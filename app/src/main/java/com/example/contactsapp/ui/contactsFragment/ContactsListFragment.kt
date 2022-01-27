package com.example.contactsapp.ui.contactsFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.contactsapp.R
import com.example.contactsapp.databinding.FragmentContactsListBinding
import com.example.contactsapp.di.ServiceLocator

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

        activity?.title = "Contacts App"

        val adapter = ContactsAdapter2(ContactListener {
            contactWithPhone -> this.findNavController().navigate(ContactsListFragmentDirections.actionContactsFragmentToContactDetailFragment(contactWithPhone.contactDetails.contactId))
        })

        binding.contactList.adapter = adapter

        viewModel.contacts.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
                Log.i("ContactsListFragment", " Transformed contacts $it.toString()")
            }
            Log.i("ContactsListFragment", " Transformed contacts  null $it.toString()")
        })

        viewModel.navigateToAddContact.observe(this.viewLifecycleOwner, Observer { 
            if(it){
                this.findNavController().navigate(ContactsListFragmentDirections.actionContactsFragmentToAddFragment(0L))
                viewModel.doneNavigateToAddContact()
            }
        })
        return binding.root
    }

}