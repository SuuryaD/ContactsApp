package com.example.contactsapp.ui.contactDetailFragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.contactsapp.R
import com.example.contactsapp.data.database.ContactDatabase
import com.example.contactsapp.databinding.FragmentContactDetailBinding
import com.example.contactsapp.databinding.PhoneRowBinding
import com.example.contactsapp.di.ServiceLocator


class ContactDetailFragment : Fragment() {

    private lateinit var binding: FragmentContactDetailBinding

    private val viewModel by viewModels<ContactDetailViewModel> { ContactDetailViewModelFactory(ServiceLocator.provideContactsDataSource(requireContext()))  }
    private val args: ContactDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_contact_detail, container, false)

        val contactId = args.contactId

        viewModel.start(args.contactId)
        activity?.title = "Contact Detail"

        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        viewModel.currentContact.observe(viewLifecycleOwner, Observer {
            it?.let {

                binding.parentLinearLayout.removeAllViews()

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
                this.findNavController().navigate(ContactDetailFragmentDirections.actionContactDetailFragmentToAddFragment(args.contactId))
                true
            }
            R.id.delete_contact ->  {
                viewModel.deleteCurrentContact()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.contact_detail_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun addView(phoneNumber: String){
        val v = PhoneRowBinding.inflate(layoutInflater)
        v.textView2.text = phoneNumber
        binding.parentLinearLayout.addView(v.root, binding.parentLinearLayout.childCount)
    }




    // Testing


    override fun onPause() {
        super.onPause()
        Log.i("ContactDetailFragment", "On Pause called")
    }

    override fun onStop() {
        super.onStop()
        Log.i("ContactDetailFragment", "On Stop called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("ContactDetailFragment", "On DestroyView called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("ContactDetailFragment", "On Destroy called")
    }

    override fun onDetach() {
        super.onDetach()
//        viewModel.destroyContact()
        Log.i("ContactDetailFragment", "On Detach called")
    }
}