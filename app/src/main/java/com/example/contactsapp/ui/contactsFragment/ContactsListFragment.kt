package com.example.contactsapp.ui.contactsFragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.contactsapp.R
import com.example.contactsapp.data.database.ContactWithPhone
import com.example.contactsapp.databinding.FragmentContactsListBinding
import com.example.contactsapp.di.ServiceLocator
import com.example.contactsapp.util.EventObserver

class ContactsListFragment : Fragment() {

    private lateinit var binding: FragmentContactsListBinding
    private val viewModel: ContactsListFragmentViewModel by viewModels { ContactsListFragmentViewModelFactory(ServiceLocator.provideContactsDataSource(requireContext())) }
    private lateinit var adapter: ContactsAdapter2
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contacts_list, container, false)

        binding.contactsListViewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        adapter = ContactsAdapter2(ContactListener {
            contactWithPhone -> this.findNavController().navigate(ContactsListFragmentDirections.actionContactsFragmentToContactDetailFragment(contactWithPhone.contactDetails.contactId))
        })


        viewModel.contacts.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        binding.contactList.adapter = adapter

        setHasOptionsMenu(true);

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.contact_list_menu, menu)

        val searchItem : MenuItem = menu.findItem(R.id.actionSearch)

        val searchView : SearchView = searchItem.actionView as SearchView

//        searchView.setOnCloseListener(object: SearchView.OnCloseListener{
//            override fun onClose(): Boolean {
//
//                Log.i("ContactsListFragment", "close called")
//                Log.i("ContactListFragment", viewModel.contacts.value.toString())
//                adapter.submitList(viewModel.contacts.value)
//                return false
//            }
//
//        })


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                newText?.let {

                    if(newText.isNotEmpty()){
                        val ls = ArrayList<ContactWithPhone>()

                        viewModel.contacts.value?.forEach {
                            if(it.contactDetails.contactId != 0L && it.contactDetails.name.startsWith(newText, true))
                                ls.add(it)
                        }
                        adapter.submitList(ls)
                    }
                    else{
                        adapter.submitList(viewModel.contacts.value)
                    }
                }
                return true
            }

        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        viewModel.navigateToAddContact.observe(this.viewLifecycleOwner, EventObserver {
                this.findNavController().navigate(ContactsListFragmentDirections.actionContactsFragmentToAddFragment(0L))
        })

    }

}