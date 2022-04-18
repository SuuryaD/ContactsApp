package com.example.contactsapp.ui.addToFragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.contactsapp.R
import com.example.contactsapp.data.database.ContactWithPhone
import com.example.contactsapp.databinding.FragmentAddToContactBinding
import com.example.contactsapp.di.ServiceLocator
import com.example.contactsapp.ui.contactsFragment.*
import com.example.contactsapp.util.EventObserver

class AddToFragment : Fragment() {

    private lateinit var binding: FragmentAddToContactBinding
    private val viewModel: ContactsListFragmentViewModel by viewModels {
        ContactsListFragmentViewModelFactory(
            ServiceLocator.provideContactsDataSource(requireContext()), requireContext()
        )
    }
    private lateinit var adapter: ContactsAdapter2

    private val args: AddToFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_to_contact, container, false)

        binding.contactsListViewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner


        adapter = ContactsAdapter2(ContactListener {
            this.findNavController().navigate(
                AddToFragmentDirections.actionAddToFragmentToAddFragment(
                    it.contactDetails.contactId,
                    args.phoneNumber
                )
            )
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

        inflater.inflate(R.menu.add_to_fragment_menu, menu)

        val searchItem: MenuItem = menu.findItem(R.id.actionSearch)

        val searchView: SearchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                newText?.let {

                    if (newText.isNotEmpty()) {
                        val ls = ArrayList<ContactWithPhone>()

                        viewModel.contacts.value?.forEach {
                            if (it.contactDetails.contactId != 0L && it.contactDetails.name.startsWith(
                                    newText,
                                    true
                                )
                            )
                                ls.add(it)
                        }
                        adapter.submitList(ls)
                    } else {
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
            this.findNavController().navigate(
                AddToFragmentDirections.actionAddToFragmentToAddFragment(
                    0L,
                    args.phoneNumber
                )
            )
        })

    }

}
