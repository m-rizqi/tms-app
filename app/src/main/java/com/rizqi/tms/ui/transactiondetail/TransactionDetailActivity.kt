package com.rizqi.tms.ui.transactiondetail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.viewModelScope
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityTransactionDetailBinding
import com.rizqi.tms.model.TransactionWithItemInCashier
import com.rizqi.tms.ui.bill.BillPrintActivity
import com.rizqi.tms.utility.*
import com.rizqi.tms.viewmodel.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.ceil

@AndroidEntryPoint
class TransactionDetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityTransactionDetailBinding
    private val transactionViewModel : TransactionViewModel by viewModels()
    private var transactionId = 1L
    private var transaction : TransactionWithItemInCashier? = null
    private val itemInCashierAdapter = ItemInCashierAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transactionId = intent.getLongExtra(TRANSACTION_ID, 0)
        transactionViewModel.getTransactionById(transactionId).observe(this){transaction ->
            this.transaction = transaction
            if (transaction == null) return@observe
            itemInCashierAdapter.submitList(transaction.itemInCashiers)
            binding.apply {
                id = transaction.transaction.id
                time = getFormattedDateString(transaction.transaction.time, EEE_DD_MMM_YYYY_HH_MM)
                total = getString(R.string.rp_, ThousandFormatter.format(transaction.transaction.total))
                pay = getString(R.string.rp_, ThousandFormatter.format(transaction.transaction.pay))
                moneyChange = getString(R.string.rp_, ThousandFormatter.format(transaction.transaction.changeMoney))
            }
        }

        binding.apply {
            rvTransactionDetailItems.adapter = itemInCashierAdapter
            btnTransactionDetailBack.setOnClickListener { onBackPressed() }
            tvTransactionDetailDelete.setOnClickListener {
                transaction?.let {tr ->
                    transactionViewModel.deleteTransaction(tr.transaction)
                    finish()
                }
            }
            tvTransactionDetailViewBill.setOnClickListener {
                Intent(this@TransactionDetailActivity, BillPrintActivity::class.java).apply {
                    putExtra(TRANSACTION_WITH_ITEM_IN_CASHIER, transaction)
                }.also { itn ->
                    startActivity(itn)
                }
            }
        }

    }
}