package com.example.contactsapp.contactDetailFragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.contactsapp.R
import com.example.contactsapp.database.ContactDatabase
import com.example.contactsapp.databinding.FragmentContactDetailBinding
import com.example.contactsapp.databinding.PhoneRowBinding


class ContactDetailFragment : Fragment() {

    private lateinit var binding: FragmentContactDetailBinding
    private lateinit var viewModel : ContactDetailViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_contact_detail, container, false)

        val dataSource = ContactDatabase.getInstance(this.requireContext()).contactDetailsDao
        val args :ContactDetailFragmentArgs by navArgs()
        val contactId = args.contactId

        activity?.title = "Contact Detail"
        viewModel = ViewModelProvider(this,ContactDetailViewModelFactory(dataSource,contactId)).get(ContactDetailViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        viewModel.currentContact.observe(this.viewLifecycleOwner, Observer {
            it?.let {
                Log.i("ContactDetailFragment", it.toString())
                it.phoneNumbers.forEach {
                    addView(it.phoneNumber)
                }
            }
        })

        setHasOptionsMenu(true)

        viewModel.navigateToContactsListFragment.observe(this.viewLifecycleOwner, Observer {
            if(it){
                this.findNavController().navigate(ContactDetailFragmentDirections.actionContactDetailFragmentToContactsFragment())
                viewModel.doneNavigateToContactsListFragment()
            }
        })

        binding.button.setOnClickListener {
            this.findNavController().navigate(ContactDetailFragmentDirections.actionContactDetailFragmentToAddFragment(contactId))
        }

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       return when(item.itemId){
            R.id.edit_contact -> {
                this.findNavController().navigate(ContactDetailFragmentDirections.actionContactDetailFragmentToAddFragment(viewModel.contactId))
                true
            }
            R.id.delete_contact ->  {
                viewModel.deleteCurrentContact()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

//        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.contact_detail_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun addView(phoneNumber: String){
        val v = PhoneRowBinding.inflate(layoutInflater)
//        val layoutInflater = layoutInflater.inflate(R.layout.phone_row, null)
        v.textView2.text = phoneNumber
        binding.parentLinearLayout.addView(v.root, binding.parentLinearLayout.childCount)
    }

}