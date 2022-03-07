package com.example.contactsapp.ui.favoritesFragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.contactsapp.R
import com.example.contactsapp.data.database.ContactWithPhone
import com.example.contactsapp.databinding.FavoritesFragmentBinding
import com.example.contactsapp.di.ServiceLocator
import com.example.contactsapp.util.EventObserver
import com.example.contactsapp.util.PhonePermissionRequester
import com.google.android.material.snackbar.Snackbar

class FavoritesFragment : Fragment() {


    private val viewModel by viewModels<FavoritesViewModel> {
        FavoritesViewModelFactory(
            ServiceLocator.provideContactsDataSource(requireContext())
        )
    }
    private lateinit var binding: FavoritesFragmentBinding


    private val phonePermissionRequester = PhonePermissionRequester(this, { onGranted() }, {
        Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_LONG).show()
    })

    var onGranted = { Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()}


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.favorites_fragment, container, false)

        val adapter = FavoritesAdapter(viewModel, FavoritesListener {
            showPhoneNumberChooser(it)
        })

        binding.favoritesRecyclerView.adapter = adapter
        viewModel.favoriteContact.observe(viewLifecycleOwner){
            it?.let{
                if(it.isNotEmpty()){
                    binding.noFavorites.visibility = View.GONE
                    binding.favoritesRecyclerView.visibility = View.VISIBLE
                }else{
                    binding.favoritesRecyclerView.visibility = View.GONE
                    binding.noFavorites.visibility = View.VISIBLE
                }
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

    private fun showPhoneNumberChooser(contactWithPhone: ContactWithPhone) {

        if(contactWithPhone.phoneNumbers.size == 1){
            makeCall(contactWithPhone.phoneNumbers[0].phoneNumber)
            return
        }

        val alertBuilder = AlertDialog.Builder(this.context)
        alertBuilder.setTitle("Choose a number")

        val ls = contactWithPhone.phoneNumbers.map {
            it.phoneNumber
        }

        alertBuilder.setItems(ls.toTypedArray(), DialogInterface.OnClickListener{ dialog, which ->
            makeCall(ls[which])
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

    private fun makeCall(phoneNumber: String){

        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$phoneNumber")

        onGranted = {
            Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
        phonePermissionRequester.checkPermissions(requireContext())

    }


}