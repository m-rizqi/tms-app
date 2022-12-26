package com.rizqi.tms.ui.dialog.transactionfilter

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rizqi.tms.R
import com.rizqi.tms.databinding.BottomSheetTransactionFilterBinding
import com.rizqi.tms.model.DateRange
import com.rizqi.tms.model.Transaction
import com.rizqi.tms.model.TransactionFilter
import com.rizqi.tms.utility.*
import java.util.Calendar

class TransactionFilterBottomSheet(
    private val transactionFilter: TransactionFilter?,
    private val onApplyListener : (TransactionFilter?) -> Unit
) : BottomSheetDialogFragment(){
    private var _binding : BottomSheetTransactionFilterBinding? = null
    val binding : BottomSheetTransactionFilterBinding
        get() = _binding!!
    private lateinit var dialog : BottomSheetDialog
    private var tempTransactionFilter : TransactionFilter? = transactionFilter?.copy()

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

        transactionFilter?.dateRangeType?.let { toggleDateRangeType(it) }
        if (transactionFilter?.dateRangeType?.ordinal == DateRange.Custom().ordinal){
            transactionFilter.dateFrom?.let {
                binding.tilTransactionFilterDateFrom.editText.setText(getFormattedDateString(it,DD_MMM_YYYY))
            }
            transactionFilter.dateTo?.let {
                binding.tilTransactionFilterDateTo.editText.setText(getFormattedDateString(it,DD_MMM_YYYY))
            }
        }
        binding.apply {
            lTransactionFilterDateRangeAll.rangeDate = getString(R.string.all)
            lTransactionFilterDateRangeToday.rangeDate = DateRange.Today().getDateRangeString()
            lTransactionFilterDateRangeYesterday.rangeDate = DateRange.Yesterday().getDateRangeString()
            lTransactionFilterDateRangeThisWeek.rangeDate = DateRange.ThisWeek().getDateRangeString()
            lTransactionFilterDateRangeLastWeek.rangeDate = DateRange.LastWeek().getDateRangeString()
            lTransactionFilterDateRangeThisMonth.rangeDate = DateRange.ThisMonth().getDateRangeString()
            lTransactionFilterDateRangeLastMonth.rangeDate = DateRange.LastMonth().getDateRangeString()

            lTransactionFilterDateRangeAll.root.setOnClickListener { toggleDateRangeType(DateRange.All()) }
            lTransactionFilterDateRangeToday.root.setOnClickListener { toggleDateRangeType(DateRange.Today()) }
            lTransactionFilterDateRangeYesterday.root.setOnClickListener { toggleDateRangeType(DateRange.Yesterday()) }
            lTransactionFilterDateRangeThisWeek.root.setOnClickListener { toggleDateRangeType(DateRange.ThisWeek()) }
            lTransactionFilterDateRangeLastWeek.root.setOnClickListener { toggleDateRangeType(DateRange.LastWeek()) }
            lTransactionFilterDateRangeThisMonth.root.setOnClickListener { toggleDateRangeType(DateRange.ThisMonth()) }
            lTransactionFilterDateRangeLastMonth.root.setOnClickListener { toggleDateRangeType(DateRange.LastMonth()) }

            tilTransactionFilterDateFrom.editText.setOnFocusChangeListener { _, b ->
                if (b){
                    showDatePicker(requireContext(), binding.tilTransactionFilterDateFrom.editText, tempTransactionFilter?.dateFrom?.let {
                        Calendar.getInstance().apply { timeInMillis = it }
                    }){
                        toggleDateRangeType(DateRange.Custom())
                        tempTransactionFilter?.dateFrom = it
                        binding.tilTransactionFilterDateFrom.inputLayout.clearFocus()
                    }
                }
            }
            tilTransactionFilterDateTo.editText.setOnFocusChangeListener { _, b ->
                if (b){
                    showDatePicker(requireContext(), binding.tilTransactionFilterDateTo.editText, tempTransactionFilter?.dateTo?.let {
                        Calendar.getInstance().apply { timeInMillis = it }
                    }){
                        toggleDateRangeType(DateRange.Custom())
                        tempTransactionFilter?.dateTo = it
                        binding.tilTransactionFilterDateTo.inputLayout.clearFocus()
                    }
                }
            }
            tilTransactionFilterPriceFrom.editText.addTextChangedListener(ThousandTextWatcher(binding.tilTransactionFilterPriceFrom.editText))
            tilTransactionFilterPriceTo.editText.addTextChangedListener(ThousandTextWatcher(binding.tilTransactionFilterPriceTo.editText))
            tilTransactionFilterPriceFrom.editText.setOnEditorActionListener { textView, i, keyEvent ->
                hideKeyboard(textView)
                binding.tilTransactionFilterPriceFrom.inputLayout.clearFocus()
                true
            }
            tilTransactionFilterPriceTo.editText.setOnEditorActionListener { textView, i, keyEvent ->
                hideKeyboard(textView)
                binding.tilTransactionFilterPriceTo.inputLayout.clearFocus()
                true
            }

            btnTransactionFilterApply.setOnClickListener {
                val isDateFromError = tempTransactionFilter?.dateRangeType?.ordinal == DateRange.Custom().ordinal && tempTransactionFilter?.dateFrom == null
                val isDateToError = tempTransactionFilter?.dateRangeType?.ordinal == DateRange.Custom().ordinal && tempTransactionFilter?.dateTo == null
                val priceFrom = try {
                    binding.tilTransactionFilterPriceFrom.editText.text.toString().replace(".","").toLong()
                }catch (_ : Exception){null}
                val priceTo = try {
                    binding.tilTransactionFilterPriceTo.editText.text.toString().replace(".","").toLong()
                }catch (_ : Exception){null}
                binding.tilTransactionFilterDateFrom.errorText = if (isDateFromError) getString(R.string.field_must_be_filled, getString(R.string.this_field)) else null
                binding.tilTransactionFilterDateTo.errorText = if (isDateToError) getString(R.string.field_must_be_filled, getString(R.string.this_field)) else null
                binding.tilTransactionFilterPriceFrom.errorText = if (
                    priceFrom == null && priceTo != null
                ) getString(R.string.field_must_be_filled, getString(R.string.this_field)) else null
                binding.tilTransactionFilterDateTo.errorText = if (
                    priceTo == null && priceFrom != null
                ) getString(R.string.field_must_be_filled, getString(R.string.this_field)) else null
                if (isDateFromError || isDateToError || (priceFrom == null && priceTo != null) || (priceTo == null && priceFrom != null)){
                    return@setOnClickListener
                }
                tempTransactionFilter?.priceFrom = priceFrom
                tempTransactionFilter?.priceTo = priceTo
                onApplyListener(tempTransactionFilter)
                dismiss()
            }

            tvTransactionFilterReset.setOnClickListener {
                tempTransactionFilter = TransactionFilter()
                toggleDateRangeType(tempTransactionFilter?.dateRangeType ?: DateRange.All())
                binding.tilTransactionFilterDateFrom.editText.setText("")
                binding.tilTransactionFilterDateTo.editText.setText("")
                binding.tilTransactionFilterPriceFrom.editText.setText("")
                binding.tilTransactionFilterPriceTo.editText.setText("")
            }
        }

        transactionFilter?.priceFrom?.let {
            binding.tilTransactionFilterPriceFrom.editText.setText(it.toString())
        }

        transactionFilter?.priceTo?.let {
            binding.tilTransactionFilterPriceTo.editText.setText(it.toString())
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun toggleDateRangeType(dateRange: DateRange){
        val list = listOf(
            binding.lTransactionFilterDateRangeAll,
            binding.lTransactionFilterDateRangeToday,
            binding.lTransactionFilterDateRangeYesterday,
            binding.lTransactionFilterDateRangeThisWeek,
            binding.lTransactionFilterDateRangeLastWeek,
            binding.lTransactionFilterDateRangeThisMonth,
            binding.lTransactionFilterDateRangeLastMonth,
        )
        list.forEach {
            it.radioRangeDate.isChecked = false
        }
        when(dateRange.ordinal){
            DateRange.All().ordinal -> binding.lTransactionFilterDateRangeAll.radioRangeDate.isChecked = true
            DateRange.LastMonth().ordinal -> binding.lTransactionFilterDateRangeLastMonth.radioRangeDate.isChecked = true
            DateRange.LastWeek().ordinal -> binding.lTransactionFilterDateRangeLastWeek.radioRangeDate.isChecked = true
            DateRange.ThisMonth().ordinal -> binding.lTransactionFilterDateRangeThisMonth.radioRangeDate.isChecked = true
            DateRange.ThisWeek().ordinal -> binding.lTransactionFilterDateRangeThisWeek.radioRangeDate.isChecked = true
            DateRange.Today().ordinal -> binding.lTransactionFilterDateRangeToday.radioRangeDate.isChecked = true
            DateRange.Yesterday().ordinal -> binding.lTransactionFilterDateRangeYesterday.radioRangeDate.isChecked = true
            DateRange.Custom().ordinal -> {}
        }
        tempTransactionFilter?.dateRangeType = dateRange
        if (dateRange.ordinal != DateRange.Custom().ordinal && dateRange.ordinal != DateRange.All().ordinal){
            tempTransactionFilter?.dateFrom = dateRange.dateFrom
            tempTransactionFilter?.dateTo = dateRange.dateTo
            binding.tilTransactionFilterDateFrom.editText.setText("")
            binding.tilTransactionFilterDateTo.editText.setText("")
        }
    }
}