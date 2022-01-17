package com.example.contactsapp.contactsFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.contactsapp.database.ContactWithPhone
import com.example.contactsapp.databinding.RowItemBinding

class ContactsAdapter2(val clickListener: ContactListener) : ListAdapter<ContactWithPhone, ContactsAdapter2.ViewHolder>(ContactPhoneCallBack()) {


    class ViewHolder(val binding: RowItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: ContactWithPhone, clickListener: ContactListener){
            binding.contactWithPhone = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RowItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

}

class ContactPhoneCallBack : DiffUtil.ItemCallback<ContactWithPhone>(){
    override fun areItemsTheSame(oldItem: ContactWithPhone, newItem: ContactWithPhone): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ContactWithPhone, newItem: ContactWithPhone): Boolean {
        return oldItem == newItem
    }

}

class ContactListener(val onClickListener: (contactWithPhone: ContactWithPhone) -> Unit){
    fun onClick(contactWithPhone: ContactWithPhone) = onClickListener(contactWithPhone)
}