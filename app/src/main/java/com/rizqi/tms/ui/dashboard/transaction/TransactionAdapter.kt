package com.rizqi.tms.ui.dashboard.transaction

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ItemDateTransactionBinding
import com.rizqi.tms.databinding.ItemTransactionHistoryBinding
import com.rizqi.tms.model.TransactionWithItemInCashier
import com.rizqi.tms.ui.transactiondetail.TransactionDetailActivity
import com.rizqi.tms.utility.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionAdapter : ListAdapter<Comparable<*>, TransactionAdapter.BaseTransactionViewHolder<*>>(diffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseTransactionViewHolder<*> {
        return when(viewType){
            DATE -> DateViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_date_transaction, parent, false)
            )
            TRANSACTION -> {
                TransactionItemViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_transaction_history, parent, false)
                )
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: BaseTransactionViewHolder<*>, position: Int) {
        when(val data = getItem(position)){
            is Long -> (holder as DateViewHolder).bind(data)
            is TransactionWithItemInCashier -> (holder as TransactionItemViewHolder).bind(data)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Long -> DATE
            is TransactionWithItemInCashier -> TRANSACTION
            else -> throw IllegalArgumentException("Invalid type of data $position")
        }
    }

    abstract class BaseTransactionViewHolder<in T>(itemView : View) : RecyclerView.ViewHolder(itemView){
        abstract fun bind(data : T)
    }
    class DateViewHolder(val binding : ItemDateTransactionBinding) : BaseTransactionViewHolder<Long>(binding.root){
        override fun bind(data: Long) {
            binding.date = getFormattedDateString(data)
        }
    }
    class TransactionItemViewHolder(val binding : ItemTransactionHistoryBinding) : BaseTransactionViewHolder<TransactionWithItemInCashier>(binding.root){
        @SuppressLint("UseCompatLoadingForDrawables")
        override fun bind(data: TransactionWithItemInCashier) {
            val context = binding.root.context
            binding.apply {
                date = getFormattedDateString(data.transaction.time, EEE_DD_MMM_YYYY_HH_MM)
                id = data.transaction.id
                total = ThousandFormatter.format(data.transaction.total)
                itemNames = data.itemInCashiers.joinToString { it.itemName }
                root.setOnClickListener {
                    Intent(context.applicationContext, TransactionDetailActivity::class.java).apply {
                        putExtra(TRANSACTION_ID, data.transaction.id)
                    }.also { itn ->
                        context.startActivity(itn)
                    }
                }
            }
            CoroutineScope(Dispatchers.IO).launch {
                data.transaction.getThumbnailsAt(0).also {
                    val bmp = context.getBitmapFromPath(it ?: "")
                    withContext(Dispatchers.Main){
                        Glide.with(context).load(bmp).placeholder(context.getDrawable(R.drawable.image_placeholder)).into(binding.ivTransactionHistoryImage1)
                    }
                }
                data.transaction.getThumbnailsAt(1).also {
                    val bmp = context.getBitmapFromPath(it ?: "")
                    withContext(Dispatchers.Main){
                        Glide.with(context).load(bmp).placeholder(context.getDrawable(R.drawable.image_placeholder)).into(binding.ivTransactionHistoryImage2)
                    }
                }
                data.transaction.getThumbnailsAt(2).also {
                    val bmp = context.getBitmapFromPath(it ?: "")
                    withContext(Dispatchers.Main){
                        Glide.with(context).load(bmp).placeholder(context.getDrawable(R.drawable.image_placeholder)).into(binding.ivTransactionHistoryImage3)
                    }
                }
            }
        }
    }
    class TransactionDiffUtil : DiffUtil.ItemCallback<Comparable<*>>(){
        override fun areItemsTheSame(oldItem: Comparable<*>, newItem: Comparable<*>): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Comparable<*>, newItem: Comparable<*>): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }
    companion object {
        const val DATE = 1
        const val TRANSACTION = 2
        val diffCallback = TransactionDiffUtil()
    }
}