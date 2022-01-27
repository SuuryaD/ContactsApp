package com.example.contactsapp.ui.contactsFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.contactsapp.data.database.ContactWithPhone
import com.example.contactsapp.databinding.AlphabetHeaderBinding
import com.example.contactsapp.databinding.RowItemBinding

class ContactsAdapter2(val clickListener: ContactListener) : ListAdapter<ContactWithPhone, RecyclerView.ViewHolder>(ContactPhoneCallBack()) {


    val VIEW_TYPE_ONE = 1
    val VIEW_TYPE_TWO = 2
    class ViewHolder1(val binding: RowItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: ContactWithPhone, clickListener: ContactListener){
            binding.contactWithPhone = item
            binding.clickListener = clickListener
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

    class ViewHolder2(val binding: AlphabetHeaderBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: ContactWithPhone){
            binding.textView3.text = item.contactDetails.name
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder2{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AlphabetHeaderBinding.inflate(layoutInflater, parent, false)
                return ViewHolder2(binding)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position).contactDetails.contactId){
            0L -> VIEW_TYPE_ONE
            else -> VIEW_TYPE_TWO
        }
    }

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Rec {
//        if(viewType == VIEW_TYPE_ONE){
//            return ViewHolder2.from(parent)
//        }
//        else
//            return ViewHolder.from(parent)
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == VIEW_TYPE_ONE){
            return ViewHolder2.from(parent)
        }
        else
            return ViewHolder1.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position) == VIEW_TYPE_ONE){
            (holder as ViewHolder2).bind(getItem(position))
        }
        else {
            (holder as ViewHolder1).bind(getItem(position), clickListener)
        }
    }
}

class ContactPhoneCallBack : DiffUtil.ItemCallback<ContactWithPhone>(){
    override fun areItemsTheSame(oldItem: ContactWithPhone, newItem: ContactWithPhone): Boolean {
        return oldItem.contactDetails.contactId == newItem.contactDetails.contactId
    }

    override fun areContentsTheSame(oldItem: ContactWithPhone, newItem: ContactWithPhone): Boolean {
        return oldItem == newItem
    }

}

class ContactListener(val onClickListener: (contactWithPhone: ContactWithPhone) -> Unit){
    fun onClick(contactWithPhone: ContactWithPhone) = onClickListener(contactWithPhone)
}