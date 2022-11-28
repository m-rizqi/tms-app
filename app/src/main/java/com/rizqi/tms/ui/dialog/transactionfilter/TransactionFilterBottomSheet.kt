package com.rizqi.tms.ui.dialog.transactionfilter

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rizqi.tms.databinding.BottomSheetTransactionFilterBinding
import com.rizqi.tms.model.DateRange
import com.rizqi.tms.model.TransactionFilter

class TransactionFilterBottomSheet(
    private val transactionFilter: TransactionFilter?
//    private val searchHistoryViewModel: SearchHistoryViewModel,
//    private val onApplyListener : (SearchFilter?) -> Unit
) : BottomSheetDialogFragment(){
    private var _binding : BottomSheetTransactionFilterBinding? = null
    val binding : BottomSheetTransactionFilterBinding
        get() = _binding!!
    private lateinit var dialog : BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetTransactionFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.maxHeight = (0.9 * Resources.getSystem().displayMetrics.heightPixels).toInt()

        binding.apply {
            lTransactionFilterDateRangeToday.rangeDate = DateRange.Today().getDateRangeString()
            lTransactionFilterDateRangeYesterday.rangeDate = DateRange.Yesterday().getDateRangeString()
            lTransactionFilterDateRangeThisWeek.rangeDate = DateRange.ThisWeek().getDateRangeString()
            lTransactionFilterDateRangeLastWeek.rangeDate = DateRange.LastWeek().getDateRangeString()
            lTransactionFilterDateRangeThisMonth.rangeDate = DateRange.ThisMonth().getDateRangeString()
            lTransactionFilterDateRangeLastMonth.rangeDate = DateRange.LastMonth().getDateRangeString()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}