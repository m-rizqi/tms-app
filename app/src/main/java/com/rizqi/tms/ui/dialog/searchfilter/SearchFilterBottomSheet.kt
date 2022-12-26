package com.rizqi.tms.ui.dialog.searchfilter

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rizqi.tms.databinding.BottomSheetSearchFilterBinding
import com.rizqi.tms.model.SearchFilter
import com.rizqi.tms.utility.ThousandFormatter
import com.rizqi.tms.utility.ThousandTextWatcher
import com.rizqi.tms.utility.insertUnitIntoChipGroup
import com.rizqi.tms.utility.setChipStyle
import com.rizqi.tms.viewmodel.SearchHistoryViewModel

class SearchFilterBottomSheet(
    private val searchFilter: SearchFilter?,
    private val searchHistoryViewModel: SearchHistoryViewModel,
    private val onApplyListener : (SearchFilter?) -> Unit
) : BottomSheetDialogFragment(){
    private var _binding : BottomSheetSearchFilterBinding? = null
    val binding : BottomSheetSearchFilterBinding
        get() = _binding!!
    private lateinit var dialog : BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetSearchFilterBinding.inflate(inflater, container, false)
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
        bottomSheetBehavior.maxHeight = (0.7 * Resources.getSystem().displayMetrics.heightPixels).toInt()

        searchFilter?.let {
            setChipStyle(binding.chipSearchFilterBarcode, it.isBarcodeItem)
            setChipStyle(binding.chipSearchFilterWithoutBarcode, it.isNonBarcodeItem)
            if (it.priceFrom != null && it.priceFrom != 0.0){
                binding.tilSearchFilterPriceFrom.editText.setText(ThousandFormatter.format(it.priceFrom!!))
            }
            if (it.priceTo != null && it.priceTo != 0.0){
                binding.tilSearchFilterPriceTo.editText.setText(ThousandFormatter.format(it.priceTo!!))
            }
        }

        searchHistoryViewModel.unitPaginate.observe(this){
            insertUnitIntoChipGroup(
                layoutInflater,
                it,
                searchFilter?.units,
                binding.chipGroupSearchFilterUnits
            ) { b, u ->
                if (b) searchFilter?.units?.add(u)
                else searchFilter?.units?.remove(u)
            }
        }
        searchHistoryViewModel.isUnitMaxPage.observe(this){
            binding.tvSearchFilterLoadMoreUnit.visibility = if (it) View.GONE else View.VISIBLE
        }

        binding.apply {
            chipSearchFilterBarcode.setOnCheckedChangeListener { _, b ->
                setChipStyle(binding.chipSearchFilterBarcode, b)
                searchFilter?.isBarcodeItem = b
            }
            chipSearchFilterWithoutBarcode.setOnCheckedChangeListener { _, b ->
                setChipStyle(binding.chipSearchFilterWithoutBarcode, b)
                searchFilter?.isNonBarcodeItem = b
            }
            tilSearchFilterPriceFrom.editText.addTextChangedListener(ThousandTextWatcher(binding.tilSearchFilterPriceFrom.editText))
            tilSearchFilterPriceTo.editText.addTextChangedListener(ThousandTextWatcher(binding.tilSearchFilterPriceTo.editText))
            tilSearchFilterPriceFrom.editText.doAfterTextChanged {
                try {
                    searchFilter?.priceFrom = it.toString().replace(".","").toDouble()
                }catch (e : Exception){}
            }
            tilSearchFilterPriceTo.editText.doAfterTextChanged {
                try {
                    searchFilter?.priceTo = it.toString().replace(".","").toDouble()
                }catch (e : Exception){}
            }
            tvSearchFilterLoadMoreUnit.setOnClickListener { searchHistoryViewModel.getUnitWithPaginate() }
            btnSearchFilterApply.setOnClickListener {
                onApplyListener(searchFilter)
                dismiss()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}