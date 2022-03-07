package com.example.contactsapp.ui.callHistory

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.contactsapp.R
import com.example.contactsapp.databinding.FragmentCallHistoryBinding
import com.example.contactsapp.di.ServiceLocator
import com.example.contactsapp.domain.model.CallHistoryApi
import com.example.contactsapp.util.CallLogPermissionRequester
import com.example.contactsapp.util.EventObserver
import com.example.contactsapp.util.PhonePermissionRequester
import com.google.android.material.snackbar.Snackbar


class CallHistoryFragment : Fragment() {


    private val viewModel by viewModels<CallHistoryViewModel> {
        CallHistoryViewModelFactory(
            ServiceLocator.provideContactsDataSource(requireContext())
        )
    }

    private lateinit var binding: FragmentCallHistoryBinding
    
    private lateinit var requestPermission: ActivityResultLauncher<String>
//    private lateinit var callRequestPermission: ActivityResultLauncher<String>

    private val phonePermissionRequester = PhonePermissionRequester(this, { onGranted() }, {
        Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_LONG).show()
    })
    var onGranted = {
        Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
    }


    private val callLogPermissionRequester = CallLogPermissionRequester(this, { onGrantedCallLog() }, { onDeniedCallLog()})
    var onGrantedCallLog = {Toast.makeText(requireContext(), "Call log permission Granted", Toast.LENGTH_SHORT).show()}
    var onDeniedCallLog = {Toast.makeText(requireContext(), "Call log permission denied", Toast.LENGTH_SHORT).show()}


    private var callLogPermission = false


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

//        requestPermission =
//            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
//                if (isGranted) {
//                    Snackbar.make(binding.root, "Permission granted", Snackbar.LENGTH_SHORT).show()
//                    callLogPermission = true
//                    showCallHistory()
//                } else {
//                    callLogPermission = false
//                    binding.noCallHistory.text = "Needs call logs permission!"
////
//                    binding.noCallHistory.visibility = View.VISIBLE
//                    binding.callHistoryList.visibility = View.GONE
//                    Snackbar.make(binding.root, "Permission Denied", Snackbar.LENGTH_SHORT).show()
//                }
//            }

//        callRequestPermission =
//            registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted: Boolean ->
//                if (isGranted) {
//                    Snackbar.make(binding.root, "Permission granted", Snackbar.LENGTH_SHORT).show()
//                } else {
//                    Snackbar.make(binding.root, "Permission Denied", Snackbar.LENGTH_SHORT).show()
//                }
//            }

        super.onCreate(savedInstanceState)
    }

    @SuppressLint("Range")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_call_history, container, false)

        onGrantedCallLog = {
            Toast.makeText(requireContext(), "Call log permission Granted", Toast.LENGTH_SHORT).show()
            callLogPermission = true
            binding.noCallHistory.visibility = View.GONE
            binding.callHistoryList.visibility = View.VISIBLE
            showCallHistory()
        }

        onDeniedCallLog = {
            Toast.makeText(requireContext(), "Call log permission denied", Toast.LENGTH_SHORT).show()
            callLogPermission = false
            binding.noCallHistory.text = "Needs call logs permission!"
//
            binding.noCallHistory.visibility = View.VISIBLE
            binding.callHistoryList.visibility = View.GONE
        }

        callLogPermissionRequester.checkPermissions(requireContext())

//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                android.Manifest.permission.READ_CALL_LOG
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            callLogPermission = true
//            showCallHistory()
//        }
//        else {
//            requestPermission.launch(
//                android.Manifest.permission.READ_CALL_LOG
//            )
//        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        if (callLogPermission) {

            val cres = this.context?.contentResolver
            cres?.registerContentObserver(
                CallLog.Calls.CONTENT_URI,
                false,
                object : ContentObserver(null) {
                    override fun onChange(selfChange: Boolean) {
                        Log.i("CallHistoryFragment", "On change called")
                        super.onChange(selfChange)
                        showCallHistory()
                    }

                })
        }

        binding.lifecycleOwner = viewLifecycleOwner
        val adapter = CallHistoryAdapter(viewModel, CallHistoryClickListener {
            makeCall(it.number)
        })

        binding.callHistoryList.adapter = adapter

        viewModel.callHistory.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.navigateToCallHistoryDetail.observe(viewLifecycleOwner, EventObserver{
            this.findNavController().navigate(CallHistoryFragmentDirections.actionCallHistoryFragmentToCallHistoryDetailFragment(it))
        })


    }

    @SuppressLint("Range")
    private fun showCallHistory() {

        if(!callLogPermission){
            binding.noCallHistory.text = "Needs call logs permission!"
            binding.callHistoryList.visibility = View.GONE
            binding.noCallHistory.visibility = View.VISIBLE
            return
        }


        Log.i("CallHistoryFragment", "call history called")
        val cursor = this.context?.contentResolver?.query(CallLog.Calls.CONTENT_URI, null, null,null , CallLog.Calls.DATE + " DESC")

        val callHistoryList = ArrayList<CallHistoryApi>()

        while(cursor != null && cursor.moveToNext()){

            val number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))
            val date = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE))
            val duration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION))
            val type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE))
            val id = cursor.getString(cursor.getColumnIndex(CallLog.Calls._ID))
            callHistoryList.add(
                CallHistoryApi(
                    id, number, date, duration, type
            ))
        }

        Log.i("CallHistoryFragment", callHistoryList.toString())
        if(callHistoryList.size == 0){
            binding.noCallHistory.text = "No call history to display"
            binding.noCallHistory.visibility = View.VISIBLE
            binding.callHistoryList.visibility = View.GONE
        }else{
            binding.noCallHistory.visibility = View.GONE
            binding.callHistoryList.visibility = View.VISIBLE
        }
        viewModel.getCallHistory(callHistoryList)
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