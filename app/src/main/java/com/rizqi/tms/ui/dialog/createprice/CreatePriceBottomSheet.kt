package com.rizqi.tms.ui.dialog.createprice

import android.app.Dialog
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.rizqi.tms.R
import com.rizqi.tms.databinding.BottomSheetCreatePriceBinding
import com.rizqi.tms.model.Unit
import com.rizqi.tms.ui.scan.ScanBarcodeActivity
import com.rizqi.tms.utility.BARCODE_NUMBER
import com.rizqi.tms.utility.Resource
import com.rizqi.tms.utility.ThousandTextWatcher
import com.rizqi.tms.viewmodel.UnitViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatePriceBottomSheet(
    val isConnectorVisible : Boolean = false
) : BottomSheetDialogFragment(){
    private var _binding : BottomSheetCreatePriceBinding? = null
    private val binding : BottomSheetCreatePriceBinding
        get() = _binding!!
    private lateinit var dialog : BottomSheetDialog
    private val unitViewModel : UnitViewModel by viewModels()
    private var unitList : List<Unit>? = null
    private val viewModel : CreatePriceViewModel by viewModels()

    private val scanBarcodeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == AppCompatActivity.RESULT_OK && it.data != null){
            it.data!!.getStringExtra(BARCODE_NUMBER)?.let { barcode ->
                binding.tilCreatePriceBarcode.editText.setText(barcode)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetCreatePriceBinding.inflate(inflater, container, false)
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
        bottomSheetBehavior.maxHeight = Resources.getSystem().displayMetrics.heightPixels

        unitViewModel.listUnit.observe(viewLifecycleOwner){res ->
            when(res){
                is Resource.Error -> {
                    binding.shimmerCreatePriceUnits.stopShimmer()
                    Toast.makeText(context, res.message?.asString(requireContext()), Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    stopShimmer()
                    res.data?.let { l ->
                        unitList = l
                        l.forEachIndexed { index, unit ->
                            val chip = LayoutInflater.from(requireContext()).inflate(R.layout.chip_unit, null) as Chip
                            chip.text = unit.name
                            chip.isChecked = index == 0
                            binding.chipGroupCreateItemUnits.addView(chip)
                        }
                    }
                }
            }
        }

        if (unitList == null){
            unitViewModel.getListUnit()
        }

        binding.apply {
            lCreatePriceConnectorTitle.root.visibility = if (isConnectorVisible) View.VISIBLE else View.GONE
            tilCreatePriceConnector.root.visibility = if (isConnectorVisible) View.VISIBLE else View.GONE
            tilCreatePriceMerchant.editText.addTextChangedListener(ThousandTextWatcher(binding.tilCreatePriceMerchant.editText))
            tilCreatePriceConsumer.editText.addTextChangedListener(ThousandTextWatcher(binding.tilCreatePriceConsumer.editText))
            tilCreatePriceConnector.editText.doAfterTextChanged { viewModel.setQuantityConnector(it.toString()) }
            tilCreatePriceBarcode.editText.doAfterTextChanged { viewModel.setBarcode(it.toString()) }
            tilCreatePriceMerchant.editText.doAfterTextChanged { viewModel.setMerchantPrice(it.toString()) }
            tilCreatePriceConsumer.editText.doAfterTextChanged { viewModel.setConsumerPrice(it.toString()) }
            btnCreatePriceScan.setOnClickListener { 
                val intent = Intent(context, ScanBarcodeActivity::class.java)
                scanBarcodeLauncher.launch(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startShimmer(){
        binding.shimmerCreatePriceUnits.apply {
            startShimmer()
            visibility = View.VISIBLE
        }
    }

    private fun stopShimmer(){
        binding.shimmerCreatePriceUnits.apply {
            stopShimmer()
            visibility = View.GONE
        }
    }

}