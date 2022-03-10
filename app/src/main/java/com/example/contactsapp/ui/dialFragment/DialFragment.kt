package com.example.contactsapp.ui.dialFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.contactsapp.databinding.DialFragmentBinding
import android.content.Intent


import android.annotation.SuppressLint
import android.net.Uri
import android.text.Editable
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionManager
import android.view.Gravity
import android.widget.Toast

import com.example.contactsapp.R
import com.example.contactsapp.util.PhonePermissionRequester
import java.lang.StringBuilder


class DialFragment : Fragment() {

    companion object {
        fun newInstance() = DialFragment()
    }

    private lateinit var viewModel: DialViewModel
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

        binding = DataBindingUtil.inflate(inflater, R.layout.dial_fragment, container, false)

        binding.btn1.setOnClickListener {
            binding.editText.setText(binding.editText.text.toString() + "1")
        }

        binding.btn2.setOnClickListener {
            binding.editText.setText(binding.editText.text.toString() + "2")
        }

        binding.btn3.setOnClickListener {
            binding.editText.setText(binding.editText.text.toString() + "3")
        }

        binding.btn4.setOnClickListener {
            binding.editText.setText(binding.editText.text.toString() + "4")
        }

        binding.btn5.setOnClickListener {
            binding.editText.setText(binding.editText.text.toString() + "5")
        }

        binding.btn6.setOnClickListener {
            binding.editText.setText(binding.editText.text.toString() + "6")
        }

        binding.btn7.setOnClickListener {
            binding.editText.setText(binding.editText.text.toString() + "7")
        }

        binding.btn8.setOnClickListener {
            binding.editText.setText(binding.editText.text.toString() + "8")
        }

        binding.btn9.setOnClickListener {
            binding.editText.setText(binding.editText.text.toString() + "9")
        }

        binding.btn0.setOnClickListener {
            binding.editText.setText(binding.editText.text.toString() + "0")
        }

        binding.btnStr.setOnClickListener {
            binding.editText.setText(binding.editText.text.toString() + "*")
        }

        binding.btnHash.setOnClickListener {
            binding.editText.setText(binding.editText.text.toString() + "#")
        }

        binding.btn0.setOnLongClickListener {
            binding.editText.setText(binding.editText.text.toString() + "+")
            true
        }

        binding.imageButton.setOnClickListener {
            var text = binding.editText.text.toString()
            if(text.isNotEmpty()){
                text = text.dropLast(1)
               binding.editText.setText(text)
            }
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

    fun slideDown(){

        val transition: Transition = Slide(Gravity.BOTTOM)
        transition.duration = 600
        transition.addTarget(binding.dialerCardView)
        TransitionManager.beginDelayedTransition(binding.parentConstrain, transition!!)
        binding.dialerCardView.visibility = if (binding.dialerCardView.isShown) View.GONE else View.VISIBLE

    }

    fun slideUp(){

        val transition: Transition = Slide(Gravity.BOTTOM)
        transition.duration = 600
        transition.addTarget(binding.dialerCardView)
        TransitionManager.beginDelayedTransition(binding.parentConstrain, transition!!)
        binding.dialerCardView.visibility = View.VISIBLE

    }

    fun toggle(show: Boolean){
        val transition: Transition = Slide(Gravity.BOTTOM)
        transition.duration = 600
        transition.addTarget(binding.dialerCardView)
        TransitionManager.beginDelayedTransition(binding.parentConstrain, transition!!)
        binding.dialerCardView.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun makeCall(phoneNumber: String){

        if(phoneNumber.isEmpty())
            return

        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$phoneNumber")

        onGranted = {
            startActivity(intent)
        }

        phonePermissionRequester.checkPermissions(requireContext())
    }

}

