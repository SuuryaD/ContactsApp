package com.example.contactsapp.ui.callHistory

import android.net.Uri
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
import com.example.contactsapp.databinding.CallHistoryRowItemBinding
import com.example.contactsapp.domain.model.AlphabetHeaderType
import com.example.contactsapp.domain.model.CallHistoryData
import com.example.contactsapp.domain.model.RecyclerViewViewType
import com.example.contactsapp.util.getRandomMaterialColour
import com.example.contactsapp.util.getTimeAgo

class CallHistoryAdapter(
    val viewModel: CallHistoryViewModel,
    val clickListener: CallHistoryClickListener
) :
    ListAdapter<RecyclerViewViewType, RecyclerView.ViewHolder>(CallHistoryDiffUtil()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CallHistoryData -> 1
            else -> 2
        }
    }

    class ViewHolder1(val binding: CallHistoryRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: CallHistoryData,
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

            var v = TextDrawable.builder().buildRound(
                item.name.get(0).uppercase(),
                getRandomMaterialColour(binding.root.context)
            )
            if (!item.name[0].isLetterOrDigit() || item.name[0].isDigit()) {
                v = TextDrawable.builder()
                    .buildRound("#", getRandomMaterialColour(binding.root.context))
            }

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


    class ViewHolder2(val binding: AlphabetHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AlphabetHeaderType) {
            binding.textView3.text = item.title
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder2 {
                val inflater = LayoutInflater.from(parent.context)
                val binding = AlphabetHeaderBinding.inflate(inflater, parent, false)
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
            (holder as ViewHolder1).bind(getItem(position) as CallHistoryData, clickListener, viewModel)
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
//        return false
    }

    override fun areContentsTheSame(
        oldItem: RecyclerViewViewType,
        newItem: RecyclerViewViewType
    ): Boolean {
        return oldItem.equals(newItem)
//        return false
    }
}

class CallHistoryClickListener(val onClickListener: (callHistoryData: CallHistoryData) -> Unit) {
    fun onClick(callHistoryData: CallHistoryData) = onClickListener(callHistoryData)
}