package com.example.contactsapp.contactDetailActivity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.contactsapp.databinding.FragmentContactDetailBinding
import android.provider.ContactsContract


import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.view.*
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.contactsapp.R
import com.example.contactsapp.data.database.ContactPhoneNumber
import com.example.contactsapp.data.database.ContactWithPhone
import com.example.contactsapp.databinding.PhoneRowBinding
import com.example.contactsapp.di.ServiceLocator
import com.example.contactsapp.ui.contactDetailFragment.ContactDetailFragmentDirections
import com.example.contactsapp.ui.contactDetailFragment.ContactDetailViewModel
import com.example.contactsapp.ui.contactDetailFragment.ContactDetailViewModelFactory
import com.example.contactsapp.util.PhonePermissionRequester
import com.example.contactsapp.util.createVcfFile


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ContactDetailActivityFragment : Fragment() {

    private var _binding: FragmentContactDetailBinding? = null

    private val binding get() = _binding!!

    private val viewModel by viewModels<ContactDetailViewModel> {
        ContactDetailViewModelFactory(
            ServiceLocator.provideContactsDataSource(requireContext()),
            requireContext()
        )
    }

    private val phonePermissionRequester = PhonePermissionRequester(this, { onGranted() }, {
        Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_LONG).show()
    })

    var onGranted =
        { Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show() }

    @SuppressLint("Range")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val args = arguments?.getString("data")
        _binding = FragmentContactDetailBinding.inflate(inflater, container, false)
        _binding?.lifecycleOwner = viewLifecycleOwner
        _binding?.viewModel = viewModel

        Log.i("FirstFragment", "$args")

        val uri: Uri = Uri.parse(args)
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        )

        val cursor = this.context?.contentResolver?.query(
            uri, projection,
            null, null, null
        )
        cursor?.moveToFirst()

        val id =
            cursor?.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))

        viewModel.start(id!!.toLong())

        Log.i("FirstFragment", "id: $id")

        cursor.close()

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.contact_detail_activity_menu, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.share_contact_2 -> {
                shareContact(viewModel.currentContact.value!!)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.currentContact.observe(viewLifecycleOwner, Observer {

            Log.i("ContactDetailFragment", it.toString())
            it?.let {
                if (it.contactDetails.user_image.isNullOrEmpty()) {
                    binding.imageView6.drawable.setTint(Color.parseColor(it.contactDetails.color_code))
                }
                binding.imageView7.setBackgroundColor(Color.parseColor(it.contactDetails.color_code))
                binding.parentLinearLayout.removeAllViews()

                if (it.phoneNumbers.isEmpty() && it.contactDetails.email.isEmpty()) {
                    addViewEmpty("Add phone number", it.contactDetails.user_image.isNullOrEmpty())
                }

                it.phoneNumbers.forEach { it2: ContactPhoneNumber ->
                    addView(it2.phoneNumber, it.contactDetails.user_image.isNullOrEmpty())
                }
            }
            activity?.invalidateOptionsMenu()

        })

        binding.emailLayout.setOnClickListener {

            Log.i("ContactDetailFragment", "Email layout click listener")
            val i = Intent(Intent.ACTION_SENDTO)
            i.data = Uri.parse("mailto:${binding.displayEmail.text}")
            startActivity(i)
        }

    }

    private fun addView(phoneNumber: String, addTint: Boolean) {
        val v = PhoneRowBinding.inflate(layoutInflater)

        if (addTint) {
            v.imageView9.drawable.setTint(
                Color.parseColor(viewModel.currentContact.value?.contactDetails?.color_code)
            )
            v.imageView2.drawable.setTint(
                Color.parseColor(viewModel.currentContact.value?.contactDetails?.color_code)
            )
        }

        v.textView2.text = phoneNumber
        v.root.setOnClickListener {
            makeCall(phoneNumber)
        }
        v.imageView9.setOnClickListener {
            sendMessage(phoneNumber)
        }
        binding.parentLinearLayout.addView(v.root, binding.parentLinearLayout.childCount)
    }

    private fun makeCall(phoneNumber: String) {

        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$phoneNumber")
        onGranted = {
            startActivity(intent)
        }
        phonePermissionRequester.checkPermissions(requireContext())

    }

    private fun sendMessage(phoneNumber: String) {
        val i = Intent(Intent.ACTION_SENDTO)
        i.data = Uri.parse("smsto:$phoneNumber")
        startActivity(i)
    }

    private fun addViewEmpty(phoneNumber: String, addTint: Boolean) {

        val v = PhoneRowBinding.inflate(layoutInflater)
        v.textView2.text = phoneNumber
        if (addTint) {
            v.imageView2.drawable.setTint(
                Color.parseColor(viewModel.currentContact.value?.contactDetails?.color_code)
            )
        }
        v.root.setOnClickListener {
            this.findNavController().navigate(
                ContactDetailFragmentDirections.actionContactDetailFragmentToAddFragment(
                    viewModel.currentContact.value?.contactDetails?.contactId!!,
                    null
                )
            )
        }
        v.imageView9.visibility = View.GONE
        binding.parentLinearLayout.addView(v.root, binding.parentLinearLayout.childCount)

    }

    private fun shareContact(contactWithPhone: ContactWithPhone) {

        val f = createVcfFile(contactWithPhone, requireContext())
        val i = Intent()
        i.action = Intent.ACTION_SEND
        i.type = "text/x-vcard"
        i.putExtra(
            Intent.EXTRA_STREAM,
            FileProvider.getUriForFile(context!!, "com.example.android.fileprovider", f)
        )
        startActivity(Intent.createChooser(i, "Contact"))

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}