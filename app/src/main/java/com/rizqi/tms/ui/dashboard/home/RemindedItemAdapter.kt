package com.rizqi.tms.ui.dashboard.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rizqi.tms.databinding.ItemNotificationBinding
import com.rizqi.tms.model.Item
import com.rizqi.tms.model.ItemWithPrices
import com.rizqi.tms.ui.itemdetail.ItemDetailActivity
import com.rizqi.tms.utility.ITEM_ID
import com.rizqi.tms.utility.getDateString

class RemindedItemAdapter : ListAdapter<ItemWithPrices, RemindedItemAdapter.RemindedItemViewHolder>(DiffCallback){

    class RemindedItemViewHolder(val binding : ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root)

    companion object DiffCallback : DiffUtil.ItemCallback<ItemWithPrices>() {
        override fun areItemsTheSame(oldItem: ItemWithPrices, newItem: ItemWithPrices): Boolean {
            return oldItem.item.id == newItem.item.id
        }

        override fun areContentsTheSame(oldItem: ItemWithPrices, newItem: ItemWithPrices): Boolean {
            return oldItem.item.name == newItem.item.name &&
                    oldItem.item.imagePath == newItem.item.imagePath &&
                    oldItem.item.clickCount == newItem.item.clickCount
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemindedItemViewHolder {
        return RemindedItemViewHolder(
            ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RemindedItemViewHolder, position: Int) {
        val itemWithPrices = getItem(position)
        val binding = holder.binding
        binding.itemName = itemWithPrices.item.name
        binding.dateString = binding.root.context.getDateString(itemWithPrices.item.lastUpdate)
        binding.root.setOnClickListener {
            val intent = Intent(it.context, ItemDetailActivity::class.java)
            intent.putExtra(ITEM_ID, itemWithPrices.item.id)
            it.context.startActivity(intent)
        }
    }
}