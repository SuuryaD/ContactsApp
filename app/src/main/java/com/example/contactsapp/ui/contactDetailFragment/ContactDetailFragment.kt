package com.example.contactsapp.ui.contactDetailFragment

import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.contactsapp.R
import com.example.contactsapp.data.database.ContactDatabase
import com.example.contactsapp.databinding.FragmentContactDetailBinding
import com.example.contactsapp.databinding.PhoneRowBinding
import com.example.contactsapp.di.ServiceLocator
import com.example.contactsapp.ui.addContactFragment.AddFragment
import com.example.contactsapp.ui.addContactFragment.AddFragmentViewModel
import com.example.contactsapp.util.EventObserver
import com.google.android.material.snackbar.Snackbar
import java.util.jar.Manifest


class ContactDetailFragment : Fragment() {

    private lateinit var binding: FragmentContactDetailBinding

    private val viewModel by viewModels<ContactDetailViewModel> {
        ContactDetailViewModelFactory(
            ServiceLocator.provideContactsDataSource(requireContext())
        )
    }
    private val args: ContactDetailFragmentArgs by navArgs()

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    Snackbar.make(binding.root, "Permission granted", Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(binding.root, "Permission Denied", Snackbar.LENGTH_SHORT).show()
                }
            }

//        (activity as AppCompatActivity).supportActionBar?.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_contact_detail, container, false)

        viewModel.start(args.contactId)
        activity?.title = "Contact Detail"

        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener {
            this.findNavController().navigate(
                ContactDetailFragmentDirections.actionContactDetailFragmentToAddFragment(args.contactId)
            )
        }

        viewModel.navigateToContactsListFragment.observe(this.viewLifecycleOwner, EventObserver {
            this.findNavController()
                    .navigate(ContactDetailFragmentDirections.actionContactDetailFragmentToContactsFragment())
        })

        viewModel.currentContact.observe(viewLifecycleOwner, Observer {
            it?.let {

                binding.parentLinearLayout.removeAllViews()
                it.phoneNumbers.forEach {
                    addView(it.phoneNumber)
                }
            }
        })
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit_contact -> {
                this.findNavController().navigate(
                    ContactDetailFragmentDirections.actionContactDetailFragmentToAddFragment(args.contactId)
                )
                true
            }
            R.id.delete_contact -> {
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


    private fun makeCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$phoneNumber")


        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startActivity(intent)
        } else {
            requestPermissionLauncher.launch(
                android.Manifest.permission.CALL_PHONE
            )
        }
    }

    private fun addView(phoneNumber: String) {
        val v = PhoneRowBinding.inflate(layoutInflater)
        v.textView2.text = phoneNumber
        v.root.setOnClickListener {
            makeCall(phoneNumber)
        }
        binding.parentLinearLayout.addView(v.root, binding.parentLinearLayout.childCount)
    }

}

@BindingAdapter("ImageUri")
fun setImageUri(imgView: ImageView, uri: String?) {

        val u = Uri.parse(uri)

        Glide.with(imgView.context)
            .load(u)
            .fitCenter()
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .error(R.drawable.ic_baseline_account_circle_24)
            .into(imgView)

}

@BindingAdapter("ImageUri2")
fun setImageUri2(imgView: ImageView, uri: String){

    val u = Uri.parse(uri)

    Glide.with(imgView.context)
        .load(u)
        .fitCenter()
        .circleCrop()
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true)
        .error(R.drawable.ic_baseline_add_a_photo_24)
        .into(imgView)
}
