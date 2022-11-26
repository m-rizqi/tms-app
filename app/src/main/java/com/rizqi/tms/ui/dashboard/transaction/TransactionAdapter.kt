package com.rizqi.tms.ui.dashboard.transaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ItemDateTransactionBinding
import com.rizqi.tms.databinding.ItemTransactionHistoryBinding
import com.rizqi.tms.model.Transaction
import com.rizqi.tms.model.TransactionHistoryViewType
import com.rizqi.tms.model.TransactionWithItemInCashier
import com.rizqi.tms.utility.EEE_DD_MMM_YYYY_HH_MM
import com.rizqi.tms.utility.ThousandFormatter
import com.rizqi.tms.utility.getBitmapFromPath
import com.rizqi.tms.utility.getFormattedDateString
import java.util.Calendar

class TransactionAdapter : ListAdapter<TransactionHistoryViewType, TransactionAdapter.TransactionViewHolder>(DiffCallback){

    companion object DiffCallback : DiffUtil.ItemCallback<TransactionHistoryViewType>() {
        override fun areItemsTheSame(
            oldItem: TransactionHistoryViewType,
            newItem: TransactionHistoryViewType
        ): Boolean {
            if (oldItem.areClassTheSame(newItem)) return true
            when(newItem){
                is TransactionHistoryViewType.Date -> {
                    if (oldItem.data !is Long || newItem.data !is Long) return false
                    return oldItem.data as Long == newItem.data as Long
                }
                is TransactionHistoryViewType.TransactionItem -> {
                    if (oldItem.data !is TransactionWithItemInCashier || newItem.data !is TransactionWithItemInCashier) return false
                    return oldItem.data.transaction.id == newItem.data.transaction.id
                }
            }
        }

        override fun areContentsTheSame(
            oldItem: TransactionHistoryViewType,
            newItem: TransactionHistoryViewType
        ): Boolean {
            if (oldItem.areClassTheSame(newItem)) return true
            when(newItem){
                is TransactionHistoryViewType.Date -> {
                    if (oldItem.data !is Long || newItem.data !is Long) return false
                    return oldItem.data as Long == newItem.data as Long
                }
                is TransactionHistoryViewType.TransactionItem -> {
                    if (oldItem.data !is TransactionWithItemInCashier || newItem.data !is TransactionWithItemInCashier) return false
                    return oldItem.data as TransactionWithItemInCashier == newItem.data as TransactionWithItemInCashier
                }
            }
        }

    }

    sealed class TransactionViewHolder(
        val view : View
    ) : ViewHolder(view){
        abstract fun <T>bind(data : T)
        class DateViewHolder(val binding : ItemDateTransactionBinding) : TransactionViewHolder(binding.root){
            override fun <T> bind(data: T) {
                if (data is Long){
                    binding.date = getFormattedDateString(data)
                }
            }
        }
        class TransactionItemViewHolder(val binding : ItemTransactionHistoryBinding) : TransactionViewHolder(binding.root){
            override fun <T> bind(data: T) {
                if (data is TransactionWithItemInCashier){
                    val context = binding.root.context
                    binding.apply {
                        date = getFormattedDateString(data.transaction.time, EEE_DD_MMM_YYYY_HH_MM)
                        id = data.transaction.id
                        total = ThousandFormatter.format(data.transaction.total)
                        itemNames = data.itemInCashiers.joinToString { it.itemName }
                    }
                    data.transaction.getThumbnailsAt(0)?.let {
                        Glide.with(context).load(context.getBitmapFromPath(it)).placeholder(R.drawable.image_placeholder).into(binding.ivTransactionHistoryImage1)
                    }
                    data.transaction.getThumbnailsAt(1)?.let {
                        Glide.with(context).load(context.getBitmapFromPath(it)).placeholder(R.drawable.image_placeholder).into(binding.ivTransactionHistoryImage2)
                    }
                    data.transaction.getThumbnailsAt(2)?.let {
                        Glide.with(context).load(context.getBitmapFromPath(it)).placeholder(R.drawable.image_placeholder).into(binding.ivTransactionHistoryImage3)
                    }
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return when(viewType){
            TransactionHistoryViewType.Date.viewType -> TransactionViewHolder.DateViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), viewType, parent, false)
            )
            else -> {
                TransactionViewHolder.TransactionItemViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), viewType, parent, false)
                )
            }
        }
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).getViewType()
    }
}