package com.rizqi.tms.ui.dashboard.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rizqi.tms.databinding.ItemCardItemBinding
import com.rizqi.tms.ui.dashboard.home.HomeViewModel.Item

class GridItemAdapter : ListAdapter<Item, GridItemAdapter.GridItemViewHolder>(DiffCallback){
    class GridItemViewHolder(val binding : ItemCardItemBinding) : RecyclerView.ViewHolder(binding.root)

    companion object DiffCallback : DiffUtil.ItemCallback<Item>(){
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridItemViewHolder {
        return GridItemViewHolder(
            ItemCardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: GridItemViewHolder, position: Int) {
        val item = getItem(position)
        val binding = holder.binding
        binding.apply {
            itemName = item.name
            merchantPrice = item.merchantMainPrice
            consumerPrice = item.consumerMainPrice
        }
        item.image?.let { image ->
            Glide.with(binding.root.context).load(image).into(binding.ivCardItemImage)
            binding.ivCardItemImage.visibility = View.VISIBLE
            binding.shimmerCardItemImage.visibility = View.GONE
        }
        binding.root.setOnClickListener {
            item.onClickListener
        }
    }
}