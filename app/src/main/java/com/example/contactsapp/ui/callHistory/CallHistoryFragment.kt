package com.example.contactsapp.ui.callHistory

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.telecom.Call
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.contactsapp.R
import com.example.contactsapp.databinding.FragmentCallHistoryBinding
import com.example.contactsapp.di.ServiceLocator
import com.example.contactsapp.domain.model.CallHistory
import com.example.contactsapp.domain.model.CallHistoryApi
import com.google.android.material.snackbar.Snackbar


class CallHistoryFragment : Fragment() {


    private val viewModel by viewModels<CallHistoryViewModel> {
        CallHistoryViewModelFactory(
            ServiceLocator.provideContactsDataSource(requireContext())
        )
    }

    private lateinit var binding: FragmentCallHistoryBinding
    
    private lateinit var requestPermission: ActivityResultLauncher<String>


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        requestPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
//                    Snackbar.make(binding.root, "Permission granted", Snackbar.LENGTH_SHORT).show()
                    Log.i("CallHistoryFragment", "permission Granted")
                    showCallHistory()
                } else {
//                    Snackbar.make(binding.root, "Permission Denied", Snackbar.LENGTH_SHORT).show()
                    Log.i("CallHistoryFragment", "permission Denied")
                }
            }

        super.onCreate(savedInstanceState)
    }

    @SuppressLint("Range")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_call_history, container, false)


        binding.lifecycleOwner = viewLifecycleOwner
        val adapter = CallHistoryAdapter(CallHistoryClickListener {
            makeCall(it.callHistoryApi.number)
        })
        binding.callHistoryList.adapter = adapter

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_CALL_LOG
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.WRITE_CALL_LOG
            ) == PackageManager.PERMISSION_GRANTED) {

            showCallHistory()
        } else {
            requestPermission.launch(
                android.Manifest.permission.READ_CALL_LOG
            )
            requestPermission.launch(
                android.Manifest.permission.WRITE_CALL_LOG
            )
        }

        viewModel.callHistory.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("Range")
    private fun showCallHistory() {

        Log.i("CallHistoryFragment", "call history called")
        val cursor = this.context?.contentResolver?.query(CallLog.Calls.CONTENT_URI, null, null,null , CallLog.Calls.DATE + " DESC")

        val ls = ArrayList<CallHistoryApi>()
        while(cursor?.moveToNext() == true){

            val number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))
            val date = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE))
            val duration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION))
            val type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE))
            ls.add(
                CallHistoryApi(
                    number, date, duration, type
            ))
        }
        viewModel.getCallHistory(ls)
    }


    private fun makeCall(phoneNumber: String){

        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            showCallHistory()
        }

    }

}