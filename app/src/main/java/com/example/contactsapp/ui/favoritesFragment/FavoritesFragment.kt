package com.example.contactsapp.ui.favoritesFragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.contactsapp.R
import com.example.contactsapp.data.database.ContactWithPhone
import com.example.contactsapp.databinding.FavoritesFragmentBinding
import com.example.contactsapp.di.ServiceLocator
import com.example.contactsapp.util.EventObserver
import com.google.android.material.snackbar.Snackbar

class FavoritesFragment : Fragment() {


    private val viewModel by viewModels<FavoritesViewModel> {
        FavoritesViewModelFactory(
            ServiceLocator.provideContactsDataSource(requireContext())
        )
    }
    private lateinit var binding: FavoritesFragmentBinding

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

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.favorites_fragment, container, false)

        val adapter = FavoritesAdapter(viewModel, FavoritesListener {
            makeCall(it)
        })

        binding.favoritesRecyclerView.adapter = adapter
        viewModel.favoriteContact.observe(viewLifecycleOwner){
            it?.let{
                adapter.submitList(it)
            }
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.navigateToContactDetail.observe(viewLifecycleOwner, EventObserver{
            this.findNavController().navigate(FavoritesFragmentDirections.actionFavoritesFragmentToContactDetailFragment(it))
        })

    }



    private fun makeCall(contactWithPhone: ContactWithPhone) {

        if(contactWithPhone.phoneNumbers.size == 1){
            makeCall2(contactWithPhone.phoneNumbers[0].phoneNumber)
            return
        }


        val alertBuilder = AlertDialog.Builder(this.context)
        alertBuilder.setTitle("Choose a number")

        val ls = contactWithPhone.phoneNumbers.map {
            it.phoneNumber
        }

        alertBuilder.setItems(ls.toTypedArray(), DialogInterface.OnClickListener{ dialog, which ->
            makeCall2(ls[which])
        })

        val alertDialog = alertBuilder.create()
        alertDialog.setButton(
            DialogInterface.BUTTON_NEGATIVE,
            "Cancel"
        ) { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }
        alertDialog.show()

    }

    private fun makeCall2(phoneNumber: String){

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


}