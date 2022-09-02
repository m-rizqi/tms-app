package com.rizqi.tms.ui.dashboard.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ItemCardItemBinding
import com.rizqi.tms.model.ItemWithPrices
import com.rizqi.tms.ui.itemdetail.ItemDetailActivity
import com.rizqi.tms.utility.ITEM_ID
import com.rizqi.tms.utility.ThousandFormatter
import com.rizqi.tms.utility.getBitmapFromPath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GridItemAdapter : ListAdapter<ItemWithPrices, GridItemAdapter.GridItemViewHolder>(DiffCallback){
    class GridItemViewHolder(val binding : ItemCardItemBinding) : RecyclerView.ViewHolder(binding.root)

    companion object DiffCallback : DiffUtil.ItemCallback<ItemWithPrices>(){
        override fun areItemsTheSame(oldItem: ItemWithPrices, newItem: ItemWithPrices): Boolean {
            return oldItem.item.id == newItem.item.id
        }

        override fun areContentsTheSame(oldItem: ItemWithPrices, newItem: ItemWithPrices): Boolean {
            return oldItem.item.imagePath == newItem.item.imagePath &&
                    oldItem.item.name == newItem.item.name
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridItemViewHolder {
        return GridItemViewHolder(
            ItemCardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: GridItemViewHolder, position: Int) {
        val itemWithPrices = getItem(position)
        val mainPrice = itemWithPrices.prices.firstOrNull {
            it.price.isMainPrice
        }
        val binding = holder.binding
        val context = binding.root.context
        itemWithPrices.item.imagePath?.let {
            CoroutineScope(Dispatchers.IO).launch {
                val bitmap = context.getBitmapFromPath(it)
                withContext(Dispatchers.Main){
                    Glide.with(binding.root.context).load(bitmap).placeholder(R.drawable.image_placeholder).into(binding.ivCardItemImage)
                    binding.ivCardItemImage.visibility = View.VISIBLE
                    binding.shimmerCardItemImage.visibility = View.GONE
                }
            }
        }
        mainPrice?.let {
            binding.merchantPrice = context.getString(R.string.rp_, ThousandFormatter.format(it.merchantSubPrice.subPrice.price)) + "/${it.price.unitName}"
            binding.consumerPrice = context.getString(R.string.rp_, ThousandFormatter.format(it.consumerSubPrice.subPrice.price)) + "/${it.price.unitName}"
        }
        binding.itemName = itemWithPrices.item.name
        binding.root.setOnClickListener {
            val intent = Intent(it.context, ItemDetailActivity::class.java)
            intent.putExtra(ITEM_ID, itemWithPrices.item.id)
            it.context.startActivity(intent)
        }
    }
}