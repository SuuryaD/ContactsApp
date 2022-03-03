package com.example.contactsapp.ui.callHistoryDetailFragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import android.text.format.DateUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.contactsapp.R
import com.example.contactsapp.databinding.CallHistoryDetailRowItemBinding
import com.example.contactsapp.databinding.FragmentCallHistoryDetailBinding
import com.example.contactsapp.di.ServiceLocator
import com.example.contactsapp.domain.model.CallHistoryApi
import com.example.contactsapp.util.EventObserver
import java.text.SimpleDateFormat
import java.util.*

class CallHistoryDetailFragment : Fragment() {


    private val viewModel by viewModels<CallHistoryDetailViewModel> {
        CallHistoryDetailFactory(
            ServiceLocator.provideContactsDataSource(requireContext()),
            args.callHistory
        )
    }
    private val args: CallHistoryDetailFragmentArgs by navArgs()

    private lateinit var binding: FragmentCallHistoryDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.i("CallHistoryDetail", args.callHistory.toString())

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_call_history_detail,
            container,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.callHistoryLiv.observe(viewLifecycleOwner, {
            it?.let {
                binding.callHistory = it
                addView(it.callHistoryApi)
            }
        })

        viewModel.sendMessage.observe(viewLifecycleOwner, EventObserver{
            Log.i("CallHistoryDetail", "Send message observed")
            val i = Intent(Intent.ACTION_SENDTO)
            i.data = Uri.parse("smsto:$it")
            startActivity(i)
        })

        viewModel.makeCall.observe(viewLifecycleOwner, EventObserver{
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:${args.callHistory.number}")
            startActivity(intent)
        })

        viewModel.createNewContact.observe(viewLifecycleOwner, EventObserver{
            this.findNavController().navigate(CallHistoryDetailFragmentDirections.actionCallHistoryDetailFragmentToAddFragment(0L, args.callHistory.number))
        })

        viewModel.addToContact.observe(viewLifecycleOwner, EventObserver{
            this.findNavController().navigate(CallHistoryDetailFragmentDirections.actionCallHistoryDetailFragmentToAddToFragment(args.callHistory.number))
        })

        viewModel.deleteCallHistory.observe(viewLifecycleOwner, EventObserver{
            val cres = activity?.contentResolver
            val callHistory = args.callHistory

            for(i in callHistory.callHistoryApi){

                Log.i("CallHistoryDetail", "id: ${i.id}")
                cres?.delete(CallLog.Calls.CONTENT_URI, "${CallLog.Calls._ID} = ?" , arrayOf(i.id))
            }

            this.findNavController().navigateUp()
        })

        return binding.root
    }


    private fun addView(ls: List<CallHistoryApi>) {

        binding.callHistoryLayout.removeAllViews()

        for (i in ls) {
            val v = CallHistoryDetailRowItemBinding.inflate(layoutInflater)
            v.imageView15.setImageResource(
                when (i.type) {
                    1 -> R.drawable.ic_baseline_call_received_24
                    2 -> R.drawable.ic_baseline_call_made_24
                    3 -> R.drawable.ic_baseline_call_missed_24
                    else -> R.drawable.ic_baseline_block_24
                }
            )
            v.textView9.text = when(i.type){
                1 -> "Incoming Call"
                2 -> "Outgoing Call"
                3 -> "Missed Call"
                else -> "Cancelled"
            }
            v.textView10.text = SimpleDateFormat("EEE, d MMM yyyy hh:mm a").format(Date(i.date))
            v.textView11.text = DateUtils.formatElapsedTime(i.duration.toLong())


            binding.callHistoryLayout.addView(v.root, binding.callHistoryLayout.childCount)

        }

    }

}