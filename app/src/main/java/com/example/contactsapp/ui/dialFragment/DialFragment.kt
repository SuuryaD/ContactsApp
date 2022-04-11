package com.example.contactsapp.ui.dialFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import android.content.Intent


import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.text.Editable
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.amulyakhare.textdrawable.TextDrawable
import com.bumptech.glide.Glide

import com.example.contactsapp.R
import com.example.contactsapp.data.database.ContactWithPhone
import com.example.contactsapp.databinding.*
import com.example.contactsapp.di.ServiceLocator
import com.example.contactsapp.domain.model.ContactDetail
import com.example.contactsapp.ui.contactsFragment.ContactListener
import com.example.contactsapp.ui.favoritesFragment.FavoritesViewModel
import com.example.contactsapp.ui.favoritesFragment.FavoritesViewModelFactory
import com.example.contactsapp.util.PhonePermissionRequester
import com.example.contactsapp.util.getRandomMaterialColour
import java.lang.StringBuilder


class DialFragment : Fragment() {

    companion object {
        fun newInstance() = DialFragment()
    }

    private val viewModel by viewModels<DialViewModel> {
        DialViewModelFactory(
            ServiceLocator.provideContactsDataSource(requireContext())
        )
    }

    private lateinit var binding: DialFragmentBinding

    private val phonePermissionRequester = PhonePermissionRequester(this, { onGranted() }, {
        Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_LONG).show()
    })
    var onGranted = {
        Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_LONG).show()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreateView(inflater, container, savedInstanceState)

        binding = DataBindingUtil.inflate(inflater, R.layout.dial_fragment, container, false)

//        viewModel.contacts.observe(viewLifecycleOwner, {
//            Log.i("DialFragment", it.toString())
//
//            binding.scrollViewLayout.removeAllViews()
//
//            if(!binding.editText.text.toString().isNullOrEmpty()){
//                for(i in it)
//                    addView(i)
//                addButtons()
//            }
//                addButtons()
//        })

        viewModel._contacts.observe(viewLifecycleOwner, {
            Log.i("DialFragment", it.toString())
        })

//        viewModel.contacts2.observe(viewLifecycleOwner, {
//            binding.scrollViewLayout.removeAllViews()
//
//            if(!binding.editText.text.toString().isNullOrEmpty()){
//                for(i in it)
//                    addView(i)
//                addButtons()
//            }
//        })

        viewModel.contacts3.observe(viewLifecycleOwner, {
            binding.scrollViewLayout.removeAllViews()

            if(!binding.editText.text.toString().isNullOrEmpty()){
                for(i in it)
                    addView(i)
                addButtons()
            }
        })


        binding.btn1.setOnClickListener {
            binding.editText.setText(binding.editText.text.toString() + "1")
            viewModel.getContact(binding.editText.text.toString())
        }

        binding.btn2.setOnClickListener {
            binding.editText.setText(binding.editText.text.toString() + "2")
            viewModel.getContact(binding.editText.text.toString())
        }

        binding.btn3.setOnClickListener {
            binding.editText.setText(binding.editText.text.toString() + "3")
            viewModel.getContact(binding.editText.text.toString())
        }

        binding.btn4.setOnClickListener {
            binding.editText.setText(binding.editText.text.toString() + "4")
            viewModel.getContact(binding.editText.text.toString())
        }

        binding.btn5.setOnClickListener {
            binding.editText.setText(binding.editText.text.toString() + "5")
            viewModel.getContact(binding.editText.text.toString())
        }

        binding.btn6.setOnClickListener {
            binding.editText.setText(binding.editText.text.toString() + "6")
            viewModel.getContact(binding.editText.text.toString())
        }

        binding.btn7.setOnClickListener {
            binding.editText.setText(binding.editText.text.toString() + "7")
            viewModel.getContact(binding.editText.text.toString())
        }

        binding.btn8.setOnClickListener {
            binding.editText.setText(binding.editText.text.toString() + "8")
            viewModel.getContact(binding.editText.text.toString())
        }

        binding.btn9.setOnClickListener {
            binding.editText.setText(binding.editText.text.toString() + "9")
            viewModel.getContact(binding.editText.text.toString())
        }

        binding.btn0.setOnClickListener {
            binding.editText.setText(binding.editText.text.toString() + "0")
            viewModel.getContact(binding.editText.text.toString())
        }

        binding.btnStr.setOnClickListener {
            binding.editText.setText(binding.editText.text.toString() + "*")
            viewModel.getContact(binding.editText.text.toString())
        }

        binding.btnHash.setOnClickListener {
            binding.editText.setText(binding.editText.text.toString() + "#")
            viewModel.getContact(binding.editText.text.toString())
        }

        binding.btn0.setOnLongClickListener {
            binding.editText.setText(binding.editText.text.toString() + "+")
            viewModel.getContact(binding.editText.text.toString())
            true
        }

        binding.imageButton.setOnClickListener {
            var text = binding.editText.text.toString()
            if (text.isNotEmpty()) {
                text = text.dropLast(1)
                binding.editText.setText(text)
            }
            viewModel.getContact(binding.editText.text.toString())
        }

        binding.callBtn.setOnClickListener {
            makeCall(binding.editText.text.toString())
        }

        binding.dropDown.setOnClickListener {
//            slideDown()
            toggle(false)
        }

        binding.floatingActionButton2.setOnClickListener {
//            slideUp()
            toggle(true)
        }

        return binding.root
    }

    private fun sendMessage(phoneNumber: String) {

        val i = Intent(Intent.ACTION_SENDTO)
        i.data = Uri.parse("smsto:$phoneNumber")
        startActivity(i)

    }

    fun toggle(show: Boolean) {
        val transition: Transition = Slide(Gravity.BOTTOM)
        transition.duration = 600
        transition.addTarget(binding.dialerCardView)
        TransitionManager.beginDelayedTransition(binding.parentConstrain, transition)
        binding.dialerCardView.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun addView(contactWithPhone: ContactWithPhone) {

        val rowItemBinding = RowItemBinding.inflate(layoutInflater)

        rowItemBinding.contactWithPhone = contactWithPhone

        rowItemBinding.root.setOnClickListener {
            this.findNavController()
                .navigate(
                    DialFragmentDirections.actionDialFragmentToContactDetailFragment(
                        contactWithPhone.contactDetails.contactId
                    )
                )
        }

        val v = TextDrawable.builder()
            .buildRound(
                contactWithPhone.contactDetails.name[0].toString().uppercase(),
                Color.parseColor(contactWithPhone.contactDetails.color_code)
            )

        Glide.with(rowItemBinding.root.context)
            .load(Uri.parse(contactWithPhone.contactDetails.user_image))
            .fitCenter()
            .circleCrop()
            .error(v)
            .into(rowItemBinding.imageView8)

        binding.scrollViewLayout.addView(rowItemBinding.root, binding.scrollViewLayout.childCount)
    }


    private fun addView(contactDetail: ContactDetail) {

        val dialFragmentRowItem = DialFragmentRowItemBinding.inflate(layoutInflater)
        val cnt = binding.scrollViewLayout.childCount

        dialFragmentRowItem.contactDetail = contactDetail


        dialFragmentRowItem.root.setOnClickListener {
            this.findNavController()
                .navigate(
                    DialFragmentDirections.actionDialFragmentToContactDetailFragment(
                        contactDetail.contactId
                    )
                )
        }

        val v = TextDrawable.builder()
            .buildRound(
                contactDetail.name[0].toString().uppercase(),
                Color.parseColor(contactDetail.color_code)
            )

        Glide.with(dialFragmentRowItem.root.context)
            .load(Uri.parse(contactDetail.user_image))
            .fitCenter()
            .circleCrop()
            .error(v)
            .into(dialFragmentRowItem.imageView)

        binding.scrollViewLayout.addView(dialFragmentRowItem.root, binding.scrollViewLayout.childCount)
    }


    private fun addButtons(){

        val bottomButtonBinding = BottomButtonBinding.inflate(layoutInflater)

        bottomButtonBinding.createContactBtn.setOnClickListener {
            this.findNavController().navigate(
                DialFragmentDirections.actionDialFragmentToAddFragment(
                    0L,
                    binding.editText.text.toString()
                )
            )
        }

        bottomButtonBinding.addToBtn.setOnClickListener {
            this.findNavController()
                .navigate(DialFragmentDirections.actionDialFragmentToAddToFragment(binding.editText.text.toString()))
        }

        bottomButtonBinding.sendMessageBtn.setOnClickListener {
            sendMessage(binding.editText.text.toString())
        }

        binding.scrollViewLayout.addView(bottomButtonBinding.root, binding.scrollViewLayout.childCount)

    }

    private fun makeCall(phoneNumber: String) {

        if (phoneNumber.isEmpty())
            return

        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$phoneNumber")

        onGranted = {
            startActivity(intent)
        }
        phonePermissionRequester.checkPermissions(requireContext())
    }

}

