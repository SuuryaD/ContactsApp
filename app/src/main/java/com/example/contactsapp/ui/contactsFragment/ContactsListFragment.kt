package com.example.contactsapp.ui.contactsFragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.contactsapp.R
import com.example.contactsapp.data.database.ContactDetails
import com.example.contactsapp.data.database.ContactPhoneNumber
import com.example.contactsapp.data.database.ContactWithPhone
import com.example.contactsapp.databinding.FragmentContactsListBinding
import com.example.contactsapp.di.ServiceLocator
import com.example.contactsapp.util.EventObserver
import com.google.android.material.tabs.TabLayout
import ezvcard.Ezvcard
import ezvcard.VCard
import ezvcard.io.text.VCardReader
import java.io.File

class ContactsListFragment : Fragment() {



    private lateinit var binding: FragmentContactsListBinding
    private val viewModel: ContactsListFragmentViewModel by viewModels { ContactsListFragmentViewModelFactory(ServiceLocator.provideContactsDataSource(requireContext())) }
    private lateinit var adapter: ContactsAdapter2

    private lateinit var getFileLauncher: ActivityResultLauncher<Intent>

    //    private val args: ContactsListFragmentArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getFileLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
//                Log.d("ContactListFragment", it.data?.data.toString())
                it?.let {
                    if(it.resultCode == Activity.RESULT_OK){
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.import_contact -> {
                val i = Intent(Intent.ACTION_OPEN_DOCUMENT)
                i.type = "text/x-vcard"
                i.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                i.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                i.flags = Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                getFileLauncher.launch(i)
                true
            }
            R.id.actionSearch -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        viewModel.navigateToAddContact.observe(this.viewLifecycleOwner, EventObserver {
                this.findNavController().navigate(ContactsListFragmentDirections.actionContactsFragmentToAddFragment(0L, null))
        })

    }

    fun getVcardFromfile(uri: Uri) {
        val inputStream = activity?.contentResolver?.openInputStream(uri)
        val noOfContactsAdded = viewModel.importContacts(inputStream)

        Toast.makeText(requireContext(), "$noOfContactsAdded Contacts added successfully", Toast.LENGTH_LONG).show()

//        val v = Ezvcard.parse(inputStream)
//        val y = v.all()
//
//        for(i in y){
//
//            val name = i.formattedName.value
//            val number = i.telephoneNumbers.map {
//                it.text.replace("\\s".toRegex(), "")
//            }
//            val email = i.emails.first().value
//
//            val temp = ContactWithPhone(ContactDetails(name = name, email = email), listOf(
//                ContactPhoneNumber(phoneNumber = number.first())
//            ))
//
//            Log.i("CallList", i.formattedName.value)
//            Log.i("CallList", i.telephoneNumbers.first().text.toString())
//            Log.i("CallList", i.emails.first().value)
//        }
//        inputStream?.close()


//        while(x != null){
//
//            Log.i("CallList", x.formattedName.toString())
//            Log.i("CallList", x.telephoneNumbers.first().text)
//            Log.i("CallList", x.emails.first().toString())
//        }
//        Log.i("CallList", v.formattedName.toString())
//        Log.i("CallList", v.telephoneNumbers.first().text.trim {
//            it == ' '
//        })
//        Log.i("CallList", v.emails.first().value)
    }

}