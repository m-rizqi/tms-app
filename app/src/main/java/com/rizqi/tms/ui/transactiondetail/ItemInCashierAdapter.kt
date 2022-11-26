package com.rizqi.tms.ui.transactiondetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ItemItemInCashierBinding
import com.rizqi.tms.model.ItemInCashier
import com.rizqi.tms.model.PriceType
import com.rizqi.tms.utility.ThousandFormatter
import com.rizqi.tms.utility.getBitmapFromPath
import kotlin.math.ceil

class ItemInCashierAdapter : ListAdapter<ItemInCashier, ItemInCashierAdapter.ItemInCashierViewHolder>(DiffCallback){

    inner class ItemInCashierViewHolder(val binding : ItemItemInCashierBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(itemInCashier: ItemInCashier){
            val context = binding.root.context
            binding.apply {
                itemName = itemInCashier.itemName
                price = "${context.getString(R.string.rp_, ThousandFormatter.format(ceil(itemInCashier.pricePerItem).toLong()))}/${itemInCashier.unitName}"
                quantity = "${formatQuantity(itemInCashier.quantity)} ${itemInCashier.unitName}"
                total = context.getString(R.string.rp_, ThousandFormatter.format(itemInCashier.total))
                mcvTransactionDetailPriceAdjusted.visibility = if (itemInCashier.isTotalAdjusted) View.VISIBLE else View.GONE
            }
            itemInCashier.imagePath.also {
                Glide.with(context).load(context.getBitmapFromPath(it ?: "")).placeholder(R.drawable.image_placeholder).into(binding.ivTransactioinDetail)
            }
            when(itemInCashier.priceType){
                PriceType.Merchant -> {
                    binding.apply {
                        mcvTransactionDetailPriceType.visibility = View.VISIBLE
                        mcvTransactionDetailPriceType.setCardBackgroundColor(ResourcesCompat.getColor(context.resources, R.color.accent, null))
                        priceType = context.getString(R.string.merchant_price)
                    }
                }
                PriceType.Consumer -> {
                    binding.apply {
                        mcvTransactionDetailPriceType.visibility = View.VISIBLE
                        mcvTransactionDetailPriceType.setCardBackgroundColor(ResourcesCompat.getColor(context.resources, R.color.accent_3, null))
                        priceType = context.getString(R.string.consumer_price)
                    }
                }
                PriceType.None -> {
                    binding.mcvTransactionDetailPriceType.visibility = View.GONE
                }
            }
        }
    }
    companion object DiffCallback : DiffUtil.ItemCallback<ItemInCashier>(){
        override fun areItemsTheSame(oldItem: ItemInCashier, newItem: ItemInCashier): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ItemInCashier, newItem: ItemInCashier): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemInCashierViewHolder {
        return ItemInCashierViewHolder(
            ItemItemInCashierBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemInCashierViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun formatQuantity(value : Double): String {
        var valueString = value.toString()
        val dotIndex = valueString.indexOf('.')
        if (dotIndex >= 0 && dotIndex < valueString.lastIndex && valueString.substring(dotIndex+1).all { it == '0' }){
            valueString = valueString.substring(0, dotIndex)
        }
        return valueString
    }
}