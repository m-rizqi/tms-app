package com.rizqi.tms.ui.search

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rizqi.tms.databinding.ItemSearchHistoryBinding
import com.rizqi.tms.model.ItemWithPrices
import com.rizqi.tms.model.SearchHistoryAndItem
import com.rizqi.tms.ui.itemdetail.ItemDetailActivity
import com.rizqi.tms.utility.ITEM_ID
import com.rizqi.tms.utility.ThousandFormatter

class SearchHistoryAdapter : ListAdapter<SearchHistoryAndItem, SearchHistoryAdapter.SearchHistoryViewHolder>(DiffCallback){

    var onClickListener : ((ItemWithPrices) -> Unit)? = null

    class SearchHistoryViewHolder(val binding : ItemSearchHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    companion object DiffCallback : DiffUtil.ItemCallback<SearchHistoryAndItem>(){
        override fun areItemsTheSame(
            oldItem: SearchHistoryAndItem,
            newItem: SearchHistoryAndItem
        ): Boolean {
            return oldItem.searchHistory.itemId == newItem.searchHistory.itemId
        }

        override fun areContentsTheSame(
            oldItem: SearchHistoryAndItem,
            newItem: SearchHistoryAndItem
        ): Boolean {
            val old = oldItem.itemWithPrices.item
            val new = newItem.itemWithPrices.item
            return old.name == new.name && old.imagePath == new.imagePath && old.lastUpdate == new.lastUpdate
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryViewHolder {
        return SearchHistoryViewHolder(
            ItemSearchHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: SearchHistoryViewHolder, position: Int) {
        val binding = holder.binding
        val itemWithPrices = getItem(position).itemWithPrices
        binding.itemName = itemWithPrices.item.name
        itemWithPrices.prices.firstOrNull { it.price.isMainPrice }?.let { mainPrice ->
            val price = ThousandFormatter.format(mainPrice.merchantSubPrice.subPrice.price)+"/"+ThousandFormatter.format(mainPrice.consumerSubPrice.subPrice.price)+ " (per " + mainPrice.price.unitName + ")"
            binding.priceString = price
        }
        binding.root.setOnClickListener {
            onClickListener?.invoke(itemWithPrices)
            val intent = Intent(it.context, ItemDetailActivity::class.java)
            intent.putExtra(ITEM_ID, itemWithPrices.item.id)
            it.context.startActivity(intent)
        }
    }
}