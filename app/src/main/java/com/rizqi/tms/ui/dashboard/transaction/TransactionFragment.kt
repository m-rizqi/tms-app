package com.rizqi.tms.ui.dashboard.transaction

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.rizqi.tms.databinding.FragmentTransactionBinding
import com.rizqi.tms.ui.cashiersystem.CashierSystemActivity
import com.rizqi.tms.ui.dialog.transactionfilter.TransactionFilterBottomSheet
import com.rizqi.tms.viewmodel.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TransactionFragment : Fragment() {

    private var _binding : FragmentTransactionBinding? = null
    private val binding : FragmentTransactionBinding
        get() = _binding!!
    private val transactionAdapter = TransactionAdapter()
    private val transactionViewModel : TransactionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transactionViewModel.getListTransaction().observe(viewLifecycleOwner){
            transactionViewModel.filterTransaction(it)
        }
        transactionViewModel.filteredTransaction.observe(viewLifecycleOwner){
            transactionAdapter.submitList(transactionViewModel.groupToTransactionHistoryViewType(it))
        }

        binding.apply {
            rvTransaction.adapter = transactionAdapter
            btnTransactionCreateTransaction.setOnClickListener {
                Intent(context, CashierSystemActivity::class.java).also {itn ->
                    startActivity(itn)
                }
            }
            btnTransactionFilter.setOnClickListener {
                TransactionFilterBottomSheet(
                    transactionViewModel.transactionFilter.value
                ).show(parentFragmentManager, null)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}