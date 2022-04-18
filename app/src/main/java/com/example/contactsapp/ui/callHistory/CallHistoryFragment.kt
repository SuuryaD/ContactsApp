package com.example.contactsapp.ui.callHistory

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Intent
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.text.format.DateUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.contactsapp.R
import com.example.contactsapp.data.database.CallHistory
import com.example.contactsapp.databinding.FragmentCallHistoryBinding
import com.example.contactsapp.di.ServiceLocator
import com.example.contactsapp.domain.model.AlphabetHeaderType
import com.example.contactsapp.domain.model.CallHistoryData
import com.example.contactsapp.domain.model.RecyclerViewViewType
import com.example.contactsapp.util.CallLogPermissionRequester
import com.example.contactsapp.util.EventObserver
import com.example.contactsapp.util.PhonePermissionRequester
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.jar.Manifest


class CallHistoryFragment : Fragment() {


    private val viewModel by viewModels<CallHistoryViewModel> {
        CallHistoryViewModelFactory(
            ServiceLocator.provideContactsDataSource(requireContext()),
            requireContext()
        )
    }

    private lateinit var binding: FragmentCallHistoryBinding

    private lateinit var requestPermission: ActivityResultLauncher<String>

    private val phonePermissionRequester = PhonePermissionRequester(this, { onGranted() }, {
        Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_LONG).show()
    })
    var onGranted = {
        Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
    }

    private val callLogPermissionRequester =
        CallLogPermissionRequester(this, { onGrantedCallLog() }, { onDeniedCallLog() })
    var onGrantedCallLog = {
        Toast.makeText(requireContext(), "Call log permission Granted", Toast.LENGTH_SHORT).show()
    }
    var onDeniedCallLog = {
        Toast.makeText(requireContext(), "Call log permission denied", Toast.LENGTH_SHORT).show()
    }

    private var callLogPermission = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("Range")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


//        if (ContextCompat.checkSelfPermission(
//                requireContext(),
//                android.Manifest.permission.READ_CALL_LOG
//            ) == PackageManager.PERMISSION_GRANTED
//        ){
//            callLogPermission = true
//        }

            binding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_call_history, container, false)

        onGrantedCallLog = {
//            Toast.makeText(requireContext(), "Call log permission Granted", Toast.LENGTH_SHORT).show()
            callLogPermission = true
            binding.noCallHistory.visibility = View.GONE
            binding.grantCallLog.visibility = View.GONE
            binding.callHistoryList.visibility = View.VISIBLE
            showCallHistory()
            onCallLogChange()
        }

        onDeniedCallLog = {
            Toast.makeText(requireContext(), "Call log permission denied", Toast.LENGTH_SHORT)
                .show()
            callLogPermission = false
            binding.noCallHistory.text = "Needs call logs permission!"
            binding.noCallHistory.visibility = View.VISIBLE
            binding.grantCallLog.visibility = View.VISIBLE
            binding.callHistoryList.visibility = View.GONE
        }

        binding.grantCallLog.setOnClickListener {
            callLogPermissionRequester.checkPermissions(requireContext())
        }

        callLogPermissionRequester.checkPermissions(requireContext())

        if (callLogPermission) {

//            showCallHistory()
            binding.grantCallLog.visibility = View.GONE
            val cres = this.context?.contentResolver
            cres?.registerContentObserver(
                CallLog.Calls.CONTENT_URI,
                true,
                object : ContentObserver(null) {
                    override fun onChange(selfChange: Boolean) {
                        Log.i("CallHistoryFragment", "On change called")
                        super.onChange(selfChange)
                        showCallHistory()
                    }
                })
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        val adapter = CallHistoryAdapter(viewModel, CallHistoryClickListener {
            makeCall(it.number)
        })

        binding.callHistoryList.adapter = adapter

        viewModel.callHistory2.observe(viewLifecycleOwner, {

            it?.let {

                var ls3 = ArrayList<RecyclerViewViewType>()

                val temp = ArrayList<ArrayList<CallHistory>>()

                CoroutineScope(Dispatchers.IO).launch {

                    if (it.isNotEmpty()) {
                        temp.add(arrayListOf(it[0]))
                    }
                    for (i in 1 until it.size) {

                        if (it[i].number != it[i - 1].number) {
                            temp.add(arrayListOf(it[i]))
                        } else {
                            temp.last().add(it[i])
                        }
                    }

                    val ls4 = getContactNames(temp)
                    Log.i("CallHistoryViewModel", temp.toString())

                    if (ls4.isNotEmpty() && DateUtils.isToday(ls4[0].callHistoryApi.first().date)) {
                        ls3.add(AlphabetHeaderType("Today"))
                    }

                    var add = true
                    for (i in ls4) {

                        if (!DateUtils.isToday(i.callHistoryApi.first().date) && add) {
                            ls3.add(AlphabetHeaderType("Older"))
                            add = false
                        }
                        ls3.add(i)
                    }

                    Log.i("CallHistoryViewModel", ls3.toString())
                    Log.i("CallHistoryViewModel", ls4.toString())

                    withContext(Dispatchers.Main) {
                        adapter.submitList(ls3)
                    }
                }
            }
        })

        viewModel.navigateToCallHistoryDataDetail.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().navigate(
                CallHistoryFragmentDirections.actionCallHistoryFragmentToCallHistoryDetailFragment(
                    it
                )
            )
        })
    }

    @SuppressLint("Range")
    private fun showCallHistory() {

        if (!callLogPermission) {
            binding.noCallHistory.text = "Needs call logs permission!"
            binding.callHistoryList.visibility = View.GONE
            binding.noCallHistory.visibility = View.VISIBLE
            binding.grantCallLog.visibility = View.GONE
            return
        }
        viewModel.importCallLog()
//        viewModel.getCallHistory2()
    }


    private fun makeCall(phoneNumber: String) {

        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$phoneNumber")

        onGranted = {
            Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
        phonePermissionRequester.checkPermissions(requireContext())

    }

    private fun onCallLogChange() {

        val cres = this.context?.contentResolver
        cres?.registerContentObserver(
            CallLog.Calls.CONTENT_URI,
            true,
            object : ContentObserver(null) {
                override fun onChange(selfChange: Boolean) {
                    Log.i("CallHistoryFragment", "On change called")
                    super.onChange(selfChange)
                    showCallHistory()
                }
            })
    }

    fun getContactNames(ls: List<List<CallHistory>>): List<CallHistoryData> {

        val ls2 = ArrayList<CallHistoryData>()
        for (i in ls) {

            if (i.isEmpty()) {
                continue
            }

            val temp = CallHistoryData(
                i[0].contactId,
                i[0].name,
                i[0].user_image,
                i[0].number,
                i
            )
            ls2.add(temp)
        }
        Log.i("ContactDetailsDao", ls2.toString())
        return ls2

    }

}