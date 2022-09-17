package com.rizqi.tms.ui.search

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ItemSearchBinding
import com.rizqi.tms.model.ItemWithPrices
import com.rizqi.tms.ui.itemdetail.ItemDetailActivity
import com.rizqi.tms.utility.ITEM_ID
import com.rizqi.tms.utility.ThousandFormatter
import com.rizqi.tms.utility.dp
import com.rizqi.tms.utility.getBitmapFromPath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchResultAdapter : ListAdapter<ItemWithPrices, SearchResultAdapter.SearchResultViewHolder>(DiffCallback){

    var onClickListener : ((ItemWithPrices) -> Unit)? = null

    class SearchResultViewHolder(val binding : ItemSearchBinding) : RecyclerView.ViewHolder(binding.root)

    companion object DiffCallback : DiffUtil.ItemCallback<ItemWithPrices>(){
        override fun areItemsTheSame(oldItem: ItemWithPrices, newItem: ItemWithPrices): Boolean {
            return oldItem.item.id == newItem.item.id
        }

        override fun areContentsTheSame(oldItem: ItemWithPrices, newItem: ItemWithPrices): Boolean {
            return oldItem.item.name == newItem.item.name &&
                    oldItem.item.imagePath == newItem.item.imagePath &&
                    oldItem.item.lastUpdate == newItem.item.lastUpdate
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        return SearchResultViewHolder(
            ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
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
                    Glide.with(binding.root.context).load(bitmap).into(binding.ivSearchImage)
                    binding.ivSearchImage.visibility = View.VISIBLE
                    binding.shimmerItemSearchImage.visibility = View.GONE
                }
            }
        }
        mainPrice?.let {
            binding.merchantPrice = context.getString(R.string.rp_, ThousandFormatter.format(it.merchantSubPrice.subPrice.price)) + "/${it.price.unitName}"
            binding.consumerPrice = context.getString(R.string.rp_, ThousandFormatter.format(it.consumerSubPrice.subPrice.price)) + "/${it.price.unitName}"
        }
        binding.itemName = itemWithPrices.item.name
        binding.root.setOnClickListener {
            onClickListener?.invoke(itemWithPrices)
            val intent = Intent(it.context, ItemDetailActivity::class.java)
            intent.putExtra(ITEM_ID, itemWithPrices.item.id)
            it.context.startActivity(intent)
        }
        val rootParams = binding.root.layoutParams as RecyclerView.LayoutParams
        rootParams.topMargin = if (position == 0) 16.dp(context) else 0
        binding.root.apply {
            layoutParams = rootParams
            requestLayout()
        }
    }
}