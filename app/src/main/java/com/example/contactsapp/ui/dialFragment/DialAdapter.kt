package com.example.contactsapp.ui.dialFragment

import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.bumptech.glide.Glide
import com.example.contactsapp.data.database.ContactWithPhone
import com.example.contactsapp.databinding.AlphabetHeaderBinding
import com.example.contactsapp.databinding.BottomButtonBinding
import com.example.contactsapp.databinding.RowItemBinding
import com.example.contactsapp.ui.contactsFragment.ContactListener
import com.example.contactsapp.util.getRandomMaterialColour

class DialAdapter(private val clickListener: ContactListener, val listener: DialClickListener) :
    ListAdapter<ContactWithPhone, RecyclerView.ViewHolder>(ContactPhoneCallBack()) {

    private val VIEW_TYPE_ONE = 1
    private val VIEW_TYPE_TWO = 2

    class ViewHolder1(val binding: RowItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ContactWithPhone, clickListener: ContactListener) {
            binding.contactWithPhone = item
            binding.clickListener = clickListener

            val v = TextDrawable.builder()
                .buildRound(
                    item.contactDetails.name[0].toString().uppercase(),
                    Color.parseColor(item.contactDetails.color_code)
                )


            Glide.with(binding.root.context)
                .load(Uri.parse(item.contactDetails.user_image))
                .fitCenter()
                .circleCrop()
                .error(v)
                .into(binding.imageView8)

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder1 {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RowItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder1(binding)
            }
        }
    }

    class ViewHolder2(val binding: AlphabetHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ContactWithPhone) {
            binding.textView3.text = item.contactDetails.name
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder2 {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AlphabetHeaderBinding.inflate(layoutInflater, parent, false)
                return ViewHolder2(binding)
            }
        }
    }

    class ViewHolder3(val binding: BottomButtonBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(list: DialClickListener, position: Int, size: Int) {
//            if(itemId == 0L)
            if(size == 0 && layoutPosition == 0){
                binding.createContactBtn.visibility = View.GONE
            }
            else{
                binding.createContactBtn.visibility = View.VISIBLE
            }

            binding.createContactBtn.setOnClickListener {
                list.listener()
                Log.i("ContactsAdapter", "Create contact Button called")
            }

            binding.addToBtn.setOnClickListener {
                list.addToContactListener()
            }

            binding.sendMessageBtn.setOnClickListener {
                list.sendMsgListener()
            }

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder3 {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = BottomButtonBinding.inflate(layoutInflater, parent, false)
                return ViewHolder3(binding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == currentList.size)
            return 3
        return when (getItem(position).contactDetails.contactId) {
            0L -> VIEW_TYPE_ONE
            else -> VIEW_TYPE_TWO
        }
    }

    override fun getItemCount(): Int {
        return currentList.size + 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ONE) {
            ViewHolder2.from(parent)
        } else if (viewType == 3) {
            ViewHolder3.from(parent)
        } else {
            ViewHolder1.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (position == currentList.size)
            (holder as ViewHolder3).bind(listener, position, currentList.size)
        else {
            if (getItemViewType(position) == VIEW_TYPE_ONE) {
                (holder as ViewHolder2).bind(getItem(position))
            } else {
                (holder as ViewHolder1).bind(getItem(position), clickListener)
            }
        }
    }
}

class ContactPhoneCallBack : DiffUtil.ItemCallback<ContactWithPhone>() {
    override fun areItemsTheSame(oldItem: ContactWithPhone, newItem: ContactWithPhone): Boolean {
        return oldItem.contactDetails.contactId == newItem.contactDetails.contactId
    }

    override fun areContentsTheSame(oldItem: ContactWithPhone, newItem: ContactWithPhone): Boolean {
        return oldItem == newItem
    }
}

class DialClickListener(
    val listener: () -> Unit,
    val addToContactListener: () -> Unit,
    val sendMsgListener: () -> Unit
)

