package com.rizqi.tms.ui.bill

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ItemBillPrintBinding
import com.rizqi.tms.model.ItemInCashier
import com.rizqi.tms.utility.ThousandFormatter
import com.rizqi.tms.utility.formatQuantity

class BillPrintItemAdapter : ListAdapter<ItemInCashier, BillPrintItemAdapter.BillPrintItemViewHolder>(DiffCallback){

    class BillPrintItemViewHolder(val binding : ItemBillPrintBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(itemInCashier: ItemInCashier){
            val context = binding.root.context
            binding.apply {
                itemName = itemInCashier.itemName
                pricePerItem = "${context.getString(R.string.rp_, ThousandFormatter.format(itemInCashier.pricePerItem))}/${itemInCashier.unitName}"
                quantity = "${formatQuantity(itemInCashier.quantity)} ${itemInCashier.unitName}"
                total = context.getString(R.string.rp_, ThousandFormatter.format(itemInCashier.total))
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillPrintItemViewHolder {
        return BillPrintItemViewHolder(ItemBillPrintBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: BillPrintItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}