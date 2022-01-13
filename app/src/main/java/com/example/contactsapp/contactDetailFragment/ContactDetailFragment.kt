package com.example.contactsapp.contactDetailFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.contactsapp.R
import com.example.contactsapp.database.ContactDatabase
import com.example.contactsapp.databinding.FragmentContactDetailBinding


class ContactDetailFragment : Fragment() {

    private lateinit var binding: FragmentContactDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_contact_detail, container, false)

        val dataSource = ContactDatabase.getInstance(this.requireContext()).contactDetailsDao
        val args :ContactDetailFragmentArgs by navArgs()
        val contactId = args.contactId

        val viewModel = ViewModelProvider(this,ContactDetailViewModelFactory(dataSource,contactId)).get(ContactDetailViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner


        return binding.root
    }

}