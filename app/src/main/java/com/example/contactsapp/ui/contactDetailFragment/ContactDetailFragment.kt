package com.example.contactsapp.ui.contactDetailFragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider.getUriForFile
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.contactsapp.R
import com.example.contactsapp.data.database.ContactPhoneNumber
import com.example.contactsapp.data.database.ContactWithPhone
import com.example.contactsapp.databinding.FragmentContactDetailBinding
import com.example.contactsapp.databinding.PhoneRowBinding
import com.example.contactsapp.di.ServiceLocator
import com.example.contactsapp.util.EventObserver
import com.example.contactsapp.util.PhonePermissionRequester
import com.example.contactsapp.util.createVcfFile


class ContactDetailFragment : Fragment() {

    private lateinit var binding: FragmentContactDetailBinding

    private val viewModel by viewModels<ContactDetailViewModel> {
        ContactDetailViewModelFactory(
            ServiceLocator.provideContactsDataSource(requireContext()),
            requireContext()
        )
    }
    private val args: ContactDetailFragmentArgs by navArgs()

    private val phonePermissionRequester = PhonePermissionRequester(this, { onGranted() }, {
        Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_LONG).show()
    })

    var onGranted =
        { Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_contact_detail, container, false)

//        binding.displayName.isSelected = true
        binding.displayName2.isSelected = true

        viewModel.start(args.contactId)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner


        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.emailLayout.setOnClickListener {

            Log.i("ContactDetailFragment", "Email layout click listener")
            val i = Intent(Intent.ACTION_SENDTO)
            i.data = Uri.parse("mailto:${binding.displayEmail.text}")
            startActivity(i)
        }

        viewModel.navigateBack.observe(this.viewLifecycleOwner, EventObserver {
            this.findNavController().navigateUp()
        })

        viewModel.currentContact.observe(viewLifecycleOwner, Observer {
            Log.i("ContactDetailFragment", it.toString())
            it?.let {
                if(it.contactDetails.user_image.isNullOrEmpty())
                    binding.imageView6.drawable.setTint(Color.parseColor(it.contactDetails.color_code))
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

        viewModel.displayFavouriteChangeToast.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(this.context, it, Toast.LENGTH_SHORT).show()
        })

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit_contact -> {
                this.findNavController().navigate(
                    ContactDetailFragmentDirections.actionContactDetailFragmentToAddFragment(
                        args.contactId,
                        null
                    )
                )
                true
            }
            R.id.delete_contact -> {
                deleteContact()
                true
            }
            R.id.share_contact -> {
                shareContact(viewModel.currentContact.value!!)
                true
            }
            R.id.star_contact -> {
                viewModel.changeFavourite()
                activity?.invalidateOptionsMenu()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onPrepareOptionsMenu(menu: Menu) {

        if (viewModel.currentContact.value?.contactDetails?.favorite == false || viewModel.currentContact.value?.contactDetails?.favorite == null) {
            menu.findItem(R.id.star_contact).icon =
                ContextCompat.getDrawable(context!!, R.drawable.ic_baseline_favorite_border_24)
        } else {
            menu.findItem(R.id.star_contact).icon =
                ContextCompat.getDrawable(context!!, R.drawable.ic_baseline_favorite_24)
        }

        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.contact_detail_menu, menu)

        if (viewModel.currentContact.value?.contactDetails?.favorite == false || viewModel.currentContact.value?.contactDetails?.favorite == null) {
            menu.findItem(R.id.star_contact).icon =
                ContextCompat.getDrawable(context!!, R.drawable.ic_baseline_favorite_border_24)
        } else {
            menu.findItem(R.id.star_contact).icon =
                ContextCompat.getDrawable(context!!, R.drawable.ic_baseline_favorite_24)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun deleteContact() {
        val builder = AlertDialog.Builder(context)

        builder.setTitle("Delete Contact")
        builder.setNegativeButton("Delete") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
            viewModel.deleteCurrentContact()
        }
        builder.setPositiveButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }
        builder.create().show()
    }

    private fun makeCall(phoneNumber: String) {

        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$phoneNumber")

        onGranted = {
            startActivity(intent)
        }

        phonePermissionRequester.checkPermissions(requireContext())

    }


    private fun addView(phoneNumber: String, addTint: Boolean) {
        val v = PhoneRowBinding.inflate(layoutInflater)

        if(addTint){
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

    private fun addViewEmpty(phoneNumber: String, addTint: Boolean) {

        val v = PhoneRowBinding.inflate(layoutInflater)
        v.textView2.text = phoneNumber
        if(addTint){
            v.imageView2.drawable.setTint(
                Color.parseColor(viewModel.currentContact.value?.contactDetails?.color_code)
            )
        }
        v.root.setOnClickListener {
            this.findNavController().navigate(
                ContactDetailFragmentDirections.actionContactDetailFragmentToAddFragment(
                    args.contactId,
                    null
                )
            )
        }
        v.imageView9.visibility = View.GONE
        binding.parentLinearLayout.addView(v.root, binding.parentLinearLayout.childCount)

    }

    private fun sendMessage(phoneNumber: String) {
        val i = Intent(Intent.ACTION_SENDTO)
        i.data = Uri.parse("smsto:$phoneNumber")
        startActivity(i)
    }


    private fun shareContact(contactWithPhone: ContactWithPhone) {

        val f = createVcfFile(contactWithPhone, requireContext())
        val i = Intent()
        i.action = Intent.ACTION_SEND
//        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        i.type = "text/x-vcard"
        i.putExtra(
            Intent.EXTRA_STREAM,
            getUriForFile(context!!, "com.example.android.fileprovider", f)
        )
        startActivity(Intent.createChooser(i, "Contact"))

    }

}

@BindingAdapter("ImageUri")
fun setImageUri(imgView: ImageView, uri: String?) {

    val u = Uri.parse(uri)

    Glide.with(imgView.context)
        .load(u)
        .centerCrop()
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true)
        .error(R.drawable.ic_baseline_person_24)
        .into(imgView)

}

@BindingAdapter("ImageUri2")
fun setImageUri2(imgView: ImageView, uri: String) {

    val u = Uri.parse(uri)

    Glide.with(imgView.context)
        .load(u)
        .fitCenter()
        .circleCrop()
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true)
        .error(R.drawable.ic_baseline_person_24)
        .into(imgView)
}
