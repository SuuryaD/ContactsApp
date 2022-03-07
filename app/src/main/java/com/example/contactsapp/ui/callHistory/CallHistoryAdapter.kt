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
import com.example.contactsapp.databinding.AlphabetHeaderBinding
import com.example.contactsapp.databinding.CallHistoryHeaderBinding
import com.example.contactsapp.databinding.CallHistoryRowItemBinding
import com.example.contactsapp.domain.model.AlphabetHeaderType
import com.example.contactsapp.domain.model.CallHistory
import com.example.contactsapp.domain.model.RecyclerViewViewType
import com.example.contactsapp.util.getTimeAgo

class CallHistoryAdapter(
    val viewModel: CallHistoryViewModel,
    val clickListener: CallHistoryClickListener
) :
    ListAdapter<RecyclerViewViewType, RecyclerView.ViewHolder>(CallHistoryDiffUtil()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CallHistory -> 1
            else -> 2
        }
    }

    class ViewHolder1(val binding: CallHistoryRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: CallHistory,
            clickListener: CallHistoryClickListener,
            viewModel: CallHistoryViewModel
        ) {

            binding.textView4.text = item.name
            binding.textView6.text = getTimeAgo(item.callHistoryApi.first().date)

            binding.imageView11.setImageResource(
                when (item.callHistoryApi.first().type) {
                    1 -> R.drawable.ic_baseline_call_received_24
                    2 -> R.drawable.ic_baseline_call_made_24
                    3 -> R.drawable.ic_baseline_call_missed_24
                    else -> R.drawable.ic_baseline_block_24
                }
            )

            binding.root.setOnClickListener {
                clickListener.onClick(item)
            }

            binding.imageView12.setOnClickListener {
                viewModel.navigateToCallHistory(item)
            }

            val v = TextDrawable.builder()
                .buildRound(item.name.get(0).uppercase(), R.color.purple_200)

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
            fun from(parent: ViewGroup): ViewHolder1 {
                val inflater = LayoutInflater.from(parent.context)
                val binding = CallHistoryRowItemBinding.inflate(inflater, parent, false)
                return ViewHolder1(binding)
            }
        }
    }


    class ViewHolder2(val binding: CallHistoryHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AlphabetHeaderType) {
            binding.textView5.text = item.title
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder2 {
                val inflater = LayoutInflater.from(parent.context)
                val binding = CallHistoryHeaderBinding.inflate(inflater, parent, false)
                return ViewHolder2(binding)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == 1)
            ViewHolder1.from(parent)
        else
            ViewHolder2.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 1) {
            (holder as ViewHolder1).bind(getItem(position) as CallHistory, clickListener, viewModel)
        } else {
            (holder as ViewHolder2).bind(getItem(position) as AlphabetHeaderType)
        }
    }

}


class CallHistoryDiffUtil : DiffUtil.ItemCallback<RecyclerViewViewType>() {
    override fun areItemsTheSame(
        oldItem: RecyclerViewViewType,
        newItem: RecyclerViewViewType
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: RecyclerViewViewType,
        newItem: RecyclerViewViewType
    ): Boolean {
        return oldItem.equals(newItem)
    }


}

class CallHistoryClickListener(val onClickListener: (callHistory: CallHistory) -> Unit) {
    fun onClick(callHistory: CallHistory) = onClickListener(callHistory)
}