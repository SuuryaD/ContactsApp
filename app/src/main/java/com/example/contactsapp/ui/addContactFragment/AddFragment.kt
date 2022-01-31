package com.example.contactsapp.ui.addContactFragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.contactsapp.R
import com.example.contactsapp.data.database.ContactDatabase
import com.example.contactsapp.databinding.AddContactBinding
import com.example.contactsapp.databinding.EditPhoneRowBinding
import com.example.contactsapp.di.ServiceLocator
import com.example.contactsapp.util.EventObserver
import com.google.android.material.snackbar.Snackbar

class AddFragment : Fragment() {

    private lateinit var binding: AddContactBinding
    private val args: AddFragmentArgs by navArgs()
    private val viewModel: AddFragmentViewModel by viewModels { AddFragmentViewModelFactory(
        ServiceLocator.provideContactsDataSource(requireContext())) }

    private lateinit var getImageLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){
            binding.userImage.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.add_contact, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.start(args.contactId)
        activity?.title = "Add Contact"

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        viewModel.currentContact.observe(viewLifecycleOwner, Observer {
            it?.let {
                it.phoneNumbers.forEach {
                    addView(it.phoneNumber)
                }
            }
            addView()
        })

        viewModel.navigateToContactDetail.observe(viewLifecycleOwner, EventObserver{
            if(it){
                this.findNavController().navigate(AddFragmentDirections.actionAddFragmentToContactDetailFragment(args.contactId))
            }
        })

        viewModel.navigateToContacts.observe(viewLifecycleOwner, EventObserver{
            this.findNavController().navigate(AddFragmentDirections.actionAddFragmentToContactsFragment())
        })

        binding.button3.setOnClickListener {
            viewModel.onSave(onSave())
        }

        binding.userImage.setOnClickListener {
            getImageLauncher.launch("image/*")
        }

        viewModel.snackBarEvent.observe(viewLifecycleOwner, EventObserver{
            Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
        })

    }

    private fun addView(phoneNumber: String? = null){
        val v = EditPhoneRowBinding.inflate(layoutInflater)
        if(phoneNumber != null)
            v.editTextPhone.text = Editable.Factory.getInstance().newEditable(phoneNumber)
        val cnt = binding.linearLayout.childCount
        v.editTextPhone.doAfterTextChanged {
                if(it.toString().isNotEmpty() && binding.linearLayout.childCount == cnt + 1){
                    addView()
                }
                if(binding.linearLayout.childCount > 1 && it.toString().isEmpty() && binding.linearLayout.childCount == cnt + 2){
                    binding.linearLayout.removeViewAt(binding.linearLayout.childCount - 1)
                }
        }
        binding.linearLayout.addView(v.root, binding.linearLayout.childCount)
    }

    private fun onSave() : List<String>{
        val ls = ArrayList<String>()

        val count = binding.linearLayout.childCount

        for(i in 0 until count){

            val v = binding.linearLayout.getChildAt(i)
            val phone = v.findViewById<EditText>(R.id.editTextPhone).text.toString()

            if(phone.isNotEmpty()){
                ls.add(phone)
            }
        }
        return ls
    }
}