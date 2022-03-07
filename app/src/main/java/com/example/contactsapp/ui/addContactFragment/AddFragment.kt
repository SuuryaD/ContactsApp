package com.example.contactsapp.ui.addContactFragment

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.contactsapp.R
import com.example.contactsapp.databinding.AddContactBinding
import com.example.contactsapp.databinding.EditPhoneRowBinding
import com.example.contactsapp.di.ServiceLocator
import com.example.contactsapp.util.EventObserver
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddFragment : Fragment() {

    private lateinit var binding: AddContactBinding
    private val args: AddFragmentArgs by navArgs()
    private val viewModel: AddFragmentViewModel by viewModels {
        AddFragmentViewModelFactory(
            ServiceLocator.provideContactsDataSource(requireContext())
        )
    }

    private lateinit var getImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>

    private lateinit var photoUri: Uri
    private lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                it?.let {
                    if (it.resultCode == Activity.RESULT_OK) {

                        if (it.data?.data != null) {

                            val flags =
                                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            activity?.contentResolver?.takePersistableUriPermission(
                                it.data?.data!!,
                                flags
                            )
                            viewModel.setImageUri(it.data?.data!!)
                        }
                    }
                }
            }

        takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

                if (it.resultCode == Activity.RESULT_OK) {
                    viewModel.setImageUri(photoUri)
                }
            }

        //Overriding back button press for showing dialog
        activity?.onBackPressedDispatcher?.addCallback(this) {

            if(viewModel.isValuesChanged(onSave())){
                askUser {
                    isEnabled = false
                    activity?.onBackPressed()
                }
            }
            else{
                isEnabled = false
                activity?.onBackPressed()
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.add_contact, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.start(args.contactId)

        (activity as AppCompatActivity).supportActionBar?.let {
            if(args.contactId == 0L)
                it.title = "Add Contact"
            else
                it.title = "Edit Contact"

            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_baseline_close_24)
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        viewModel.currentContact.observe(viewLifecycleOwner,{
            it?.let {
                it.phoneNumbers.forEach {
                    addView(it.phoneNumber)
                }
            }
            if(args.phoneNumber != null)
                addView(args.phoneNumber)
            addView()
        })

        viewModel.navigateToContactDetail.observe(viewLifecycleOwner, EventObserver{
            Toast.makeText(this.context, it.second, Toast.LENGTH_SHORT).show()
            if(args.phoneNumber != null){
                this.findNavController().navigateUp()
            }
            else{
                this.findNavController()
                    .navigate(AddFragmentDirections.actionAddFragmentToContactDetailFragment(it.first))
            }
        })

        binding.userImage.setOnClickListener {

            val builder = AlertDialog.Builder(this.context)
            builder.setTitle("Change Photo")
            if (viewModel.userImage.value.isNullOrEmpty()) {
                builder.setItems(R.array.image_option2) { _: DialogInterface, i: Int ->
                    when (i) {
                        0 -> getImageFromGallery()
                        1 -> takePicture()
                    }
                }
            } else {
                builder.setItems(R.array.image_option) { _: DialogInterface, i: Int ->
                    when (i) {
                        0 -> viewModel.setImageUri(Uri.parse(""))
                        1 -> getImageFromGallery()
                        2 -> takePicture()
                    }
                }
            }

            val alertDialog = builder.create()
            alertDialog.setButton(
                DialogInterface.BUTTON_NEGATIVE,
                "Cancel"
            ) { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
            alertDialog.show()

        }

        viewModel.snackBarEvent.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
        })

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_contact_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        return when (item.itemId) {
            R.id.save_menu -> {
                viewModel.onSave(onSave())
                true
            }
            android.R.id.home -> {
                if(viewModel.isValuesChanged(onSave())){
                    askUser {
                        this.findNavController().navigateUp()
                    }
                }else{
                    this.findNavController().navigateUp()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun getImageFromGallery() {

        val i = Intent(Intent.ACTION_OPEN_DOCUMENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.type = "image/*"
        i.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        i.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        i.flags = Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        getImageLauncher.launch(Intent.createChooser(i, "Select a photo"))

    }

    private fun takePicture() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                null
            }

            // Continue only if the File was successfully created
            photoFile?.also {
                photoUri = FileProvider.getUriForFile(
                    context!!,
                    "com.example.android.fileprovider",
                    it
                )

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                takePictureIntent.flags = Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                takePictureIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                takePictureIntent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                takePictureLauncher.launch(takePictureIntent)
            }
        }
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }


    private fun askUser(callback: () -> Unit) {

        val builder = AlertDialog.Builder(context)

        builder.setTitle("Discard Changes")
        builder.setNegativeButton("Discard") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
            callback()
        }
        builder.setPositiveButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }
        builder.create().show()
    }

    private fun addView(phoneNumber: String? = null) {

        val editPhoneRowBinding = EditPhoneRowBinding.inflate(layoutInflater)
        if (phoneNumber != null)
            editPhoneRowBinding.editTextPhone.text = Editable.Factory.getInstance().newEditable(phoneNumber)

        val cnt = binding.linearLayout.childCount

        editPhoneRowBinding.editTextPhone.doAfterTextChanged {
            if (it.toString().isNotEmpty() && binding.linearLayout.childCount == cnt + 1) {
                addView()
            }
            if (binding.linearLayout.childCount > 1 && it.toString()
                    .isEmpty() && binding.linearLayout.childCount == cnt + 2
            ) {
                binding.linearLayout.removeViewAt(binding.linearLayout.childCount - 1)
            }
        }
        binding.linearLayout.addView(editPhoneRowBinding.root, binding.linearLayout.childCount)
    }

    private fun onSave(): List<String> {

        val phoneNumbers = ArrayList<String>()
        val count = binding.linearLayout.childCount

        for (i in 0 until count) {

            val phoneRowItem = binding.linearLayout.getChildAt(i)
            val number = phoneRowItem.findViewById<EditText>(R.id.editTextPhone).text.toString()

            if (number.isNotEmpty()) {
                phoneNumbers.add(number)
            }
        }
        return phoneNumbers
    }
}

