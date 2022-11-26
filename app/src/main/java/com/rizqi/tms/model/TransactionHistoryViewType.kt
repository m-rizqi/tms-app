package com.rizqi.tms.model

import androidx.annotation.LayoutRes
import com.rizqi.tms.R

sealed class TransactionHistoryViewType(
    layoutId : Int,
    data : Any
) : ViewType<Any>(layoutId, data) {

    abstract fun getViewType() : Int

    class Date(data : Long) : TransactionHistoryViewType(R.layout.item_date_transaction, data){
        companion object {
            const val viewType = R.layout.item_date_transaction
        }

        override fun getViewType(): Int {
            return viewType
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }
    class TransactionItem(data : TransactionWithItemInCashier) : TransactionHistoryViewType(R.layout.item_transaction_history, data){
        companion object {
            const val viewType = R.layout.item_transaction_history
        }

        override fun getViewType(): Int {
            return viewType
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    fun areClassTheSame(newItem: TransactionHistoryViewType): Boolean {
        return !((this is TransactionItem && newItem is Date) ||
                (this is Date && newItem is TransactionItem))
    }
}
