package com.example.contactsapp.contactsFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.contactsapp.databinding.RowItemBinding
import com.example.contactsapp.model.ContactDetail


class ContactsAdapter(val clickListener: ContactListener) : ListAdapter<ContactDetail, ContactsAdapter.ViewHolder>(ContactCallBack()) {


    class ViewHolder(val binding: RowItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: ContactDetail,clickListener: ContactListener){
            binding.contactDetail = item
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

class ContactCallBack : DiffUtil.ItemCallback<ContactDetail>(){
    override fun areItemsTheSame(oldItem: ContactDetail, newItem: ContactDetail): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ContactDetail, newItem: ContactDetail): Boolean {
        return oldItem == newItem
    }

}

class ContactListener(val onClickListener: (contactDetail : ContactDetail) -> Unit){
    fun onClick(contactDetail: ContactDetail) = onClickListener(contactDetail)
}