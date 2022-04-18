package com.example.contactsapp.ui.contactsFragment

import android.Manifest
import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.provider.ContactsContract
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.contactsapp.R
import com.example.contactsapp.data.database.ContactWithPhone
import com.example.contactsapp.databinding.FragmentContactsListBinding
import com.example.contactsapp.di.ServiceLocator
import com.example.contactsapp.util.ContactsPermissionRequester
import com.example.contactsapp.util.EventObserver
import com.example.contactsapp.util.createVcfFile


class ContactsListFragment : Fragment() {

    private lateinit var binding: FragmentContactsListBinding
    private val viewModel: ContactsListFragmentViewModel by viewModels {
        ContactsListFragmentViewModelFactory(
            ServiceLocator.provideContactsDataSource(requireContext()),
            requireContext()
        )
    }
    private lateinit var adapter: ContactsAdapter2

    private lateinit var getFileLauncher: ActivityResultLauncher<Intent>

    private val contactsPermissionRequester = ContactsPermissionRequester(this, { onGranted() }, {
        Toast.makeText(requireContext(), "Contacts Permission Denied", Toast.LENGTH_SHORT).show()
    })
    var onGranted = {
        viewModel.import()
        observeContactChange()
//        Toast.makeText(requireContext(), "Contacts permission granted", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getFileLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

                it?.let {
                    if (it.resultCode == Activity.RESULT_OK) {
                        if (it.data?.data != null) {

                            Log.d("ContactList", it.data?.data.toString())

                            val flags =
                                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            activity?.contentResolver?.takePersistableUriPermission(
                                it.data?.data!!,
                                flags
                            )
                            getVcardFromfile(it.data?.data!!)

                        }
                    }
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_contacts_list, container, false)

        val isSync =
            activity?.getSharedPreferences("abcd", Context.MODE_PRIVATE)?.getBoolean("sync", true)

        onGranted = {
            viewModel.import()
            observeContactChange()
//            ContentResolver.setSyncAutomatically(Account("surya@zoho.com", "com.surya"), ContactsContract.AUTHORITY, true)
        }
//        contactsPermissionRequester.checkPermissions(requireContext())

        val accountManager = AccountManager.get(context)

        Account("surya@zoho.com", "com.surya").also {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val added =  accountManager.addAccountExplicitly(it, "12345", null)
                Log.i("MainActivity", added.toString())
            }
        }

//        val client: ContentProviderClient =
//            context?.contentResolver?.acquireContentProviderClient(ContactsContract.AUTHORITY_URI)!!
//        val values = ContentValues()
//        values.put(ContactsContract.Groups.ACCOUNT_NAME, "surya@zoho.com")
//        values.put(ContactsContract.Groups.ACCOUNT_TYPE, "com.surya")
//        values.put(ContactsContract.Settings.UNGROUPED_VISIBLE, true)
//        try {
//            client.insert(
//                ContactsContract.Settings.CONTENT_URI.buildUpon()
//                    .appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true").build(),
//                values
//            )
//        } catch (e: RemoteException) {
//            e.printStackTrace()
//        }

        val accounts = accountManager.accounts

        for(i in accounts){
            Log.i("ContactListFragment", "Name: ${i.name}, Type: ${i.type}")
        }

        Log.i("ContactsListFragment", isSync.toString())

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val cres = this.context?.contentResolver
            cres?.registerContentObserver(
                ContactsContract.Contacts.CONTENT_URI,
                false,
                object : ContentObserver(null) {
                    override fun onChange(selfChange: Boolean) {
                        super.onChange(selfChange)
//                        viewModel.import()
                    }
                })

            ContentResolver.setSyncAutomatically(Account("surya@zoho.com", "com.surya"), ContactsContract.AUTHORITY, true)
        } else {
            contactsPermissionRequester.checkPermissions(requireContext())
        }

        binding.contactsListViewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        adapter = ContactsAdapter2(ContactListener { contactWithPhone ->
            this.findNavController().navigate(
                ContactsListFragmentDirections.actionContactsFragmentToContactDetailFragment(
                    contactWithPhone.contactDetails.contactId
                )
            )
        })

        viewModel.contacts.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.isNotEmpty()) {
                    binding.noContacts.visibility = View.GONE
                    binding.contactList.visibility = View.VISIBLE
                    Log.i(tag, it.toString())
                    adapter.submitList(it)
                } else {
                    binding.contactList.visibility = View.GONE
                    binding.noContacts.visibility = View.VISIBLE
                }
//                binding.contactList.layoutManager?.scrollToPosition(0)
            }
        })

        binding.contactList.adapter = adapter

        setHasOptionsMenu(true);

        return binding.root
    }

    private fun observeContactChange(){
        val cres = this.context?.contentResolver
        cres?.registerContentObserver(
            ContactsContract.Contacts.CONTENT_URI,
            false,
            object : ContentObserver(null) {
                override fun onChange(selfChange: Boolean) {
                    super.onChange(selfChange)
//                    viewModel.import()
                }
            })
        ContentResolver.setSyncAutomatically(Account("surya@zoho.com", "com.surya"), ContactsContract.AUTHORITY, true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.contact_list_menu, menu)

        val searchItem: MenuItem = menu.findItem(R.id.actionSearch)

//        val syncItem: MenuItem = menu.findItem(R.id.sync)

//        if(activity?.getSharedPreferences("abcd", Context.MODE_PRIVATE)?.getBoolean("sync", false) == true){
//            syncItem.title = "Disable sync"
//        }
//        else{
//            syncItem.title = "Enable sync"
//        }

        val searchView: SearchView = searchItem.actionView as SearchView

        searchView.setOnCloseListener {
            Log.i("ContactsListFragment", "search view on close called")
            adapter.submitList(viewModel.contacts.value)
            binding.contactList.scrollToPosition(0)
            true
        }


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                newText?.let {

                    if (newText.isNotEmpty()) {

                        val ls = ArrayList<ContactWithPhone>()
                        viewModel.contacts.value?.forEach {
                            if (it.contactDetails.contactId != 0L && it.contactDetails.name.contains(
                                    newText,
                                    true
                                )
                            )
                                ls.add(it)
                        }
                        adapter.submitList(ls)
                    } else {
                        Log.i("ContactsListFragment", "empty text triggered")
                        adapter.submitList(viewModel.contacts.value){
                            binding.contactList.scrollToPosition(0)
                        }
                    }
                }
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.import_contact_vcf -> {
                val i = Intent(Intent.ACTION_OPEN_DOCUMENT)
                i.type = "text/x-vcard"
                i.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                i.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                i.flags = Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                getFileLauncher.launch(i)
                true
            }
            R.id.import_contact -> {
                contactsPermissionRequester.checkPermissions(requireContext())
                true
            }
            R.id.actionSearch -> {
                true
            }
            R.id.export_contacts -> {
                shareContact()
                true
            }
//            R.id.sync -> {
//                Log.i("ContactsListFragment", "before toggle: ${activity?.getSharedPreferences("abcd", Context.MODE_PRIVATE)?.getBoolean("sync", false).toString()}" )
//                toggleSync()
//                Log.i("ContactsListFragment","before toggle: ${activity?.getSharedPreferences("abcd", Context.MODE_PRIVATE)?.getBoolean("sync", false).toString()}")
//                activity?.invalidateOptionsMenu()
//                true
//            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun shareContact() {

        val f = createVcfFile(viewModel.contacts.value ?: emptyList(), requireContext())
        val i = Intent()
        i.action = Intent.ACTION_SEND
        i.type = "text/x-vcard"
        i.putExtra(
            Intent.EXTRA_STREAM,
            FileProvider.getUriForFile(context!!, "com.example.android.fileprovider", f)
        )
        startActivity(Intent.createChooser(i, "Contact"))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        viewModel.navigateToAddContact.observe(this.viewLifecycleOwner, EventObserver {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                this.findNavController().navigate(
                    ContactsListFragmentDirections.actionContactsFragmentToAddFragment(
                        0L,
                        null
                    )
                )
            }else{
                contactsPermissionRequester.checkPermissions(this.context!!)
            }
        })
    }

    fun getVcardFromfile(uri: Uri) {
        val inputStream = activity?.contentResolver?.openInputStream(uri)
        val noOfContactsAdded = viewModel.importContacts(inputStream)

        Toast.makeText(
            requireContext(),
            "$noOfContactsAdded Contacts added successfully",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun toggleSync() {

        val sharedPreferences = activity?.getSharedPreferences("abcd", Context.MODE_PRIVATE)
        val isSync: Boolean =
            sharedPreferences?.getBoolean("sync", false)!!

        if(isSync == false){
            onGranted = {
                val sharedEditor = activity?.getSharedPreferences("abcd", Context.MODE_PRIVATE)?.edit()

                sharedEditor?.putBoolean("sync", !isSync)
                sharedEditor?.apply()
                viewModel.import()
//                viewModel.syncContacts()
            }

            contactsPermissionRequester.checkPermissions(requireContext())
        }
        else{
            val sharedEditor = activity?.getSharedPreferences("abcd", Context.MODE_PRIVATE)?.edit()
            sharedEditor?.putBoolean("sync", !isSync)
            sharedEditor?.apply()
        }
    }

}