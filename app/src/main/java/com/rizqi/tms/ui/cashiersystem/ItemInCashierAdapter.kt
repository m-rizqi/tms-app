package com.rizqi.tms.ui.cashiersystem

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ItemCashierBinding
import com.rizqi.tms.model.ItemInCashier
import com.rizqi.tms.model.SubPrice
import com.rizqi.tms.model.SubPriceWithSpecialPrice
import com.rizqi.tms.model.TotalPriceType
import com.rizqi.tms.utility.ThousandFormatter
import com.rizqi.tms.utility.hideKeyboard
import kotlin.math.ceil

class ItemInCashierAdapter : ListAdapter<ItemInCashier, ItemInCashierAdapter.ItemInCashierViewHolder>(DiffCallback){

    var onSubPriceChangedListener : ((ItemInCashier, SubPriceWithSpecialPrice, Int) -> ItemInCashier?)? = null
    var onQuantityChangedListener : ((ItemInCashier, Double, Int) -> ItemInCashier?)? = null
    var onIncrementQuantityListener : ((ItemInCashier, Int) -> ItemInCashier?)? = null
    var onDecrementQuantityListener : ((ItemInCashier, Int) -> ItemInCashier?)? = null
    var onRequestTotalAdjustmentListener : ((ItemInCashier, Int) -> Unit)? = null

    companion object DiffCallback : DiffUtil.ItemCallback<ItemInCashier>(){
        override fun areItemsTheSame(oldItem: ItemInCashier, newItem: ItemInCashier): Boolean {
            return oldItem.itemId == newItem.itemId && oldItem.priceId == newItem.priceId && oldItem.subPriceId == newItem.subPriceId
        }

        override fun areContentsTheSame(oldItem: ItemInCashier, newItem: ItemInCashier): Boolean {
            return oldItem == newItem
        }

    }

    inner class ItemInCashierViewHolder(val binding : ItemCashierBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(itemInCashier: ItemInCashier, position: Int){
            val context = binding.root.context
            binding.apply {
                itemName = itemInCashier.itemWithPrices?.item?.name
                quantity = formatQuantity(itemInCashier.quantity)
                total = ThousandFormatter.format(itemInCashier.total)
                lItemCashierQuantity.tieAmount.setOnEditorActionListener { _, _, _ ->
                    hideKeyboard(binding.lItemCashierQuantity.tieAmount)
                    true
                }
                lItemCashierQuantity.btnMinus.setOnClickListener {
                    onDecrementQuantityListener?.invoke(itemInCashier, position)?.let { updatedItemCashier ->
                        binding.quantity = formatQuantity(updatedItemCashier.quantity)
                        binding.total = ThousandFormatter.format(updatedItemCashier.total)
                        binding.mcvItemCashierAdjusted.visibility = if (updatedItemCashier.totalPriceType == TotalPriceType.ADJUSTED || updatedItemCashier.totalPriceType == TotalPriceType.SPECIAL) View.VISIBLE else View.GONE
                        binding.totalPriceType = when(updatedItemCashier.totalPriceType){
                            TotalPriceType.ADJUSTED -> context.getString(R.string.adjusted)
                            TotalPriceType.SPECIAL -> context.getString(R.string.special_price)
                            TotalPriceType.ORIGINAL -> context.getString(R.string.adjusted)
                        }
                    }
                }
                mcvItemCashierAdjusted.visibility = if (itemInCashier.totalPriceType == TotalPriceType.ADJUSTED || itemInCashier.totalPriceType == TotalPriceType.SPECIAL) View.VISIBLE else View.GONE
                totalPriceType = when(itemInCashier.totalPriceType){
                    TotalPriceType.ADJUSTED -> context.getString(R.string.adjusted)
                    TotalPriceType.SPECIAL -> context.getString(R.string.special_price)
                    TotalPriceType.ORIGINAL -> context.getString(R.string.adjusted)
                }
                lItemCashierQuantity.btnPlus.setOnClickListener {
                    onIncrementQuantityListener?.invoke(itemInCashier, position)?.let { updatedItemCashier ->
                        binding.quantity = formatQuantity(updatedItemCashier.quantity)
                        binding.total = ThousandFormatter.format(updatedItemCashier.total)
                        binding.mcvItemCashierAdjusted.visibility = if (updatedItemCashier.totalPriceType == TotalPriceType.ADJUSTED || updatedItemCashier.totalPriceType == TotalPriceType.SPECIAL) View.VISIBLE else View.GONE
                        binding.totalPriceType = when(updatedItemCashier.totalPriceType){
                            TotalPriceType.ADJUSTED -> context.getString(R.string.adjusted)
                            TotalPriceType.SPECIAL -> context.getString(R.string.special_price)
                            TotalPriceType.ORIGINAL -> context.getString(R.string.adjusted)
                        }
                    }
                }
                lItemCashierQuantity.tieAmount.doAfterTextChanged {
                    try {
                        val requestQuantity = it.toString().toDouble()
                        if (requestQuantity == itemInCashier.quantity) return@doAfterTextChanged
                        onQuantityChangedListener?.invoke(itemInCashier, requestQuantity, position)?.let {updatedItemCashier ->
                            binding.total = ThousandFormatter.format(updatedItemCashier.total)
                            binding.mcvItemCashierAdjusted.visibility = if (updatedItemCashier.totalPriceType == TotalPriceType.ADJUSTED || updatedItemCashier.totalPriceType == TotalPriceType.SPECIAL) View.VISIBLE else View.GONE
                            binding.totalPriceType = when(updatedItemCashier.totalPriceType){
                                TotalPriceType.ADJUSTED -> context.getString(R.string.adjusted)
                                TotalPriceType.SPECIAL -> context.getString(R.string.special_price)
                                TotalPriceType.ORIGINAL -> context.getString(R.string.adjusted)
                            }
                        }
                    }catch (_:Exception){}
                }
                btnItemCashierEditTotal.setOnClickListener {
                    onRequestTotalAdjustmentListener?.invoke(itemInCashier, position)
                }
            }

            val possibleSubPrice = mutableListOf<SubPriceWithSpecialPrice>()
            itemInCashier.itemWithPrices?.prices?.filter { priceAndSubPrice ->
                priceAndSubPrice.price.barcode == itemInCashier.barcode
            }?.forEach { priceAndSubPrice ->
                possibleSubPrice.add(priceAndSubPrice.merchantSubPrice)
                possibleSubPrice.add(priceAndSubPrice.consumerSubPrice)
            }
            val priceArrayAdapter = ArrayAdapter<String>(
                context, R.layout.item_auto_complete, R.id.tv_item_auto_complete,
                possibleSubPrice.map { subPriceWithSpecialPrice ->
                    val subPrice = subPriceWithSpecialPrice.getSubPrice()
                    generatePriceHint(context, subPrice, itemInCashier)
                }
            )

            binding.actvItemCashierPrice.apply {
                setAdapter(priceArrayAdapter)
                hint = generatePriceHint(context, itemInCashier.usedSubPrice?.getSubPrice(), itemInCashier)
                setHintTextColor(ResourcesCompat.getColor(context.resources, R.color.black_100, null))
                setOnItemClickListener { _, _, i, _ ->
                    val changedSubPrice = possibleSubPrice[i]
                    onSubPriceChangedListener?.invoke(itemInCashier, changedSubPrice, position)?.let { updatedItemCashier ->
                        binding.actvItemCashierPrice.apply {
                            setText("")
                            hint = generatePriceHint(context, updatedItemCashier.usedSubPrice?.getSubPrice(), updatedItemCashier)
                            setHintTextColor(ResourcesCompat.getColor(context.resources, R.color.black_100, null))
                        }
                        binding.total = ThousandFormatter.format(updatedItemCashier.total)
                        binding.mcvItemCashierAdjusted.visibility = if (updatedItemCashier.totalPriceType == TotalPriceType.ADJUSTED || updatedItemCashier.totalPriceType == TotalPriceType.SPECIAL) View.VISIBLE else View.GONE
                        binding.totalPriceType = when(updatedItemCashier.totalPriceType){
                            TotalPriceType.ADJUSTED -> context.getString(R.string.adjusted)
                            TotalPriceType.SPECIAL -> context.getString(R.string.special_price)
                            TotalPriceType.ORIGINAL -> context.getString(R.string.adjusted)
                        }
                    }

                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemInCashierViewHolder {
        return ItemInCashierViewHolder(ItemCashierBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemInCashierViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    private fun formatQuantity(value : Double): String {
        var valueString = value.toString()
        val dotIndex = valueString.indexOf('.')
        if (dotIndex >= 0 && dotIndex < valueString.lastIndex && valueString.substring(dotIndex+1).all { it == '0' }){
            valueString = valueString.substring(0, dotIndex)
        }
        return valueString
    }

    private fun generatePriceHint(context: Context, subPrice : SubPrice?, itemInCashier: ItemInCashier): String {
        return "${context.getString(R.string.rp_, ThousandFormatter.format(ceil(subPrice?.price ?: 0.0).toLong()))}/${itemInCashier.itemWithPrices?.prices?.find { priceAndSubPrice -> priceAndSubPrice.price.id == subPrice?.priceId }?.price?.unitName} (${
            when(subPrice){
                is SubPrice.ConsumerSubPrice -> context.getString(R.string.consumer)
                is SubPrice.MerchantSubPrice -> context.getString(R.string.merchant)
                null -> ""
            }
        })"
    }

}