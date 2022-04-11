package com.example.contactsapp.ui.favoritesFragment

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.contactsapp.R
import com.example.contactsapp.data.database.ContactWithPhone
import com.example.contactsapp.databinding.FavoritesRowItemBinding
import com.example.contactsapp.util.getRandomMaterialColour


class FavoritesAdapter(
    val viewModel: FavoritesViewModel,
    val clickListener: FavoritesListener
    ) :
    ListAdapter<ContactWithPhone, FavoritesAdapter.ViewHolder>(FavoritesDiffUtil()) {


    class ViewHolder(val binding: FavoritesRowItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: ContactWithPhone,
            viewModel: FavoritesViewModel,
            clickListener: FavoritesListener
        ) {
            binding.contactWithPhone = item
            binding.clickListener = clickListener
//            binding.btn.setOnClickListener {
//                viewModel.navigateToContactDetail(item.contactDetails.contactId)
//            }

            binding.btn.setOnClickListener{

                Log.i("FavoritesAdapter", "onCLickCalled")
                val popMenu = PopupMenu(binding.root.context,binding.btn)
                popMenu.menuInflater.inflate(R.menu.favorite_popup_menu,popMenu.menu)
//                popMenu.inflate(R.menu.favorite_popup_menu)
                popMenu.show()
                popMenu.setOnMenuItemClickListener {
                    Log.i("FavoritesAdapter", "popMenuOnClickListener")
                    when(it.itemId){
                        R.id.remove_fav -> {
                            viewModel.removeFavorite(item.contactDetails.contactId)
                            true
                        }
                        R.id.fav_details -> {
                            viewModel.navigateToContactDetail(item.contactDetails.contactId)
                            true
                        }
                        else -> false
                    }
                }
            }


            val v = TextDrawable.builder()
                .buildRect(
                    item.contactDetails.name[0].toString().uppercase(),
                    Color.parseColor(item.contactDetails.color_code)
                )

            Glide.with(binding.root.context)
                .load(Uri.parse(item.contactDetails.user_image))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(v)
                .into(binding.img)
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FavoritesRowItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), viewModel, clickListener)
    }

}

class FavoritesDiffUtil : DiffUtil.ItemCallback<ContactWithPhone>() {
    override fun areItemsTheSame(oldItem: ContactWithPhone, newItem: ContactWithPhone): Boolean {
        return oldItem.contactDetails.contactId == newItem.contactDetails.contactId
    }

    override fun areContentsTheSame(oldItem: ContactWithPhone, newItem: ContactWithPhone): Boolean {
        return oldItem == newItem
    }

}

class FavoritesListener(val clickListner: (contactWithPhone: ContactWithPhone) -> Unit) {
    fun onClick(contactWithPhone: ContactWithPhone) = clickListner(contactWithPhone)
}

fun interface OnClickContactMenu{
    fun onClickMenu()
}