package com.example.contactsapp.ui.callHistory

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.contactsapp.R
import com.example.contactsapp.databinding.CallHistoryRowItemBinding
import com.example.contactsapp.domain.model.CallHistory
import com.example.contactsapp.util.getTimeAgo

class CallHistoryAdapter(val clickListener: CallHistoryClickListener) :
    ListAdapter<CallHistory, CallHistoryAdapter.ViewHolder>(CallHistoryDiffUtil()) {


    class ViewHolder(val binding: CallHistoryRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CallHistory, clickListener: CallHistoryClickListener) {

            Log.i(
                "CallHistoryAdapter",
                "Name: ${item.name} Time: ${getTimeAgo(item.callHistoryApi.date)} "
            )

            binding.textView4.text = item.name
            binding.textView6.text = getTimeAgo(item.callHistoryApi.date)

            binding.imageView11.setImageResource(when(item.callHistoryApi.type){
                1 -> R.drawable.ic_baseline_call_received_24
                2 -> R.drawable.ic_baseline_call_made_24
                3 -> R.drawable.ic_baseline_call_missed_24
                else -> R.drawable.ic_baseline_block_24
            })

            binding.root.setOnClickListener {
                clickListener.onClick(item)
            }

            val v = TextDrawable.builder()
                .buildRound(item.name[0].uppercase(), R.color.purple_200)

            Glide.with(binding.root.context)
                .load(Uri.parse(item.userImage))
                .fitCenter()
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(v)
                .into(binding.imageView10)

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = CallHistoryRowItemBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.i("CallHistoryAdapter", "On create called")
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i("CallHistoryAdapter", "On bind called")
        holder.bind(getItem(position), clickListener)
    }

}


class CallHistoryDiffUtil : DiffUtil.ItemCallback<CallHistory>() {

    override fun areItemsTheSame(oldItem: CallHistory, newItem: CallHistory): Boolean {
        return oldItem.contactId == newItem.contactId
    }

    override fun areContentsTheSame(oldItem: CallHistory, newItem: CallHistory): Boolean {
        return oldItem == newItem
    }

}

class CallHistoryClickListener(val onClickListener: (callHistory: CallHistory) -> Unit) {
    fun onClick(callHistory: CallHistory) = onClickListener(callHistory)
}