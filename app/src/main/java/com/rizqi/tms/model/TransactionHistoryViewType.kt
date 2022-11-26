package com.rizqi.tms.model

sealed class TransactionHistoryViewType(
    val viewType : Int,
    val data : Any
){
    companion object {
        const val DATE = 1
        const val TRANSACTION = 2
    }
    class Date(data : Long) : TransactionHistoryViewType(DATE, data)
    class Transaction(data : TransactionWithItemInCashier) : TransactionHistoryViewType(TRANSACTION, data)
}
