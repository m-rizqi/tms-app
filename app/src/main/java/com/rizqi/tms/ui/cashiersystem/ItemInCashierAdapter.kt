package com.rizqi.tms.ui.cashiersystem

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ItemCashierBinding
import com.rizqi.tms.model.ItemInCashier
import com.rizqi.tms.model.SubPrice
import com.rizqi.tms.model.SubPriceWithSpecialPrice
import com.rizqi.tms.utility.ThousandFormatter
import kotlin.math.ceil

class ItemInCashierAdapter : ListAdapter<ItemInCashier, ItemInCashierAdapter.ItemInCashierViewHolder>(DiffCallback){

    companion object DiffCallback : DiffUtil.ItemCallback<ItemInCashier>(){
        override fun areItemsTheSame(oldItem: ItemInCashier, newItem: ItemInCashier): Boolean {
            return oldItem.itemId == newItem.itemId && oldItem.priceId == newItem.priceId && oldItem.subPriceId == newItem.subPriceId
        }

        override fun areContentsTheSame(oldItem: ItemInCashier, newItem: ItemInCashier): Boolean {
            return oldItem == newItem
        }

    }

    inner class ItemInCashierViewHolder(val binding : ItemCashierBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(itemInCashier: ItemInCashier){
            val context = binding.root.context
            binding.apply {
                itemName = itemInCashier.itemWithPrices.item.name
                quantity = formatQuantity(itemInCashier.quantity)
                total = ThousandFormatter.format(itemInCashier.total)
            }
            val possibleSubPrice = mutableListOf<SubPriceWithSpecialPrice>()
            itemInCashier.itemWithPrices.prices.forEach { priceAndSubPrice ->
                possibleSubPrice.add(priceAndSubPrice.merchantSubPrice)
                possibleSubPrice.add(priceAndSubPrice.consumerSubPrice)
            }
            val priceArrayAdapter = ArrayAdapter<String>(
                context, R.layout.item_auto_complete, R.id.tv_item_auto_complete,
                possibleSubPrice.map { subPriceWithSpecialPrice ->
                    val subPrice = subPriceWithSpecialPrice.getSubPrice()
                    "${context.getString(R.string.rp_, ThousandFormatter.format(ceil(subPrice.price).toLong()))}/${itemInCashier.itemWithPrices.prices.find { priceAndSubPrice -> priceAndSubPrice.price.id == subPrice.priceId }?.price?.unitName} (${
                        when(subPrice){
                            is SubPrice.ConsumerSubPrice -> context.getString(R.string.consumer)
                            is SubPrice.MerchantSubPrice -> context.getString(R.string.merchant)
                        }
                    })"
                }
            )

            binding.actvItemCashierPrice.apply {
                setAdapter(priceArrayAdapter)
                hint = "${context.getString(R.string.rp_, ThousandFormatter.format(ceil(itemInCashier.usedSubPrice.getSubPrice().price).toLong()))}/${itemInCashier.itemWithPrices.prices.find { priceAndSubPrice -> priceAndSubPrice.price.id == itemInCashier.usedSubPrice.getSubPrice().priceId }?.price?.unitName} (${
                    when(itemInCashier.usedSubPrice.getSubPrice()){
                        is SubPrice.ConsumerSubPrice -> context.getString(R.string.consumer)
                        is SubPrice.MerchantSubPrice -> context.getString(R.string.merchant)
                    }
                })"
                setHintTextColor(ResourcesCompat.getColor(context.resources, R.color.black_100, null))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemInCashierViewHolder {
        return ItemInCashierViewHolder(ItemCashierBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemInCashierViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun formatQuantity(value : Double): String {
        var valueString = value.toString()
        val dotIndex = valueString.indexOf(".")
        if (dotIndex >= 0 && dotIndex <= valueString.lastIndex && valueString.substring(dotIndex).all { it == '0' }){
            valueString = valueString.substring(0, dotIndex)
        }
        return valueString
    }

}