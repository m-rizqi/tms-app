package com.rizqi.tms.ui.dialog.createprice

import android.app.Dialog
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
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
import com.rizqi.tms.R
import com.rizqi.tms.databinding.BottomSheetCreatePriceBinding
import com.rizqi.tms.model.PriceAndSubPrice
import com.rizqi.tms.model.SpecialPrice
import com.rizqi.tms.model.Unit
import com.rizqi.tms.ui.dialog.createunit.CreateUnitDialog
import com.rizqi.tms.ui.scan.ScanBarcodeActivity
import com.rizqi.tms.utility.*
import com.rizqi.tms.viewmodel.UnitViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatePriceBottomSheet(
    private val isConnectorVisible : Boolean = false,
    private val prevUnit: Unit? = null,
    private val onSaveListener : (PriceAndSubPrice) -> kotlin.Unit
) : BottomSheetDialogFragment(){
    private var _binding : BottomSheetCreatePriceBinding? = null
    private val binding : BottomSheetCreatePriceBinding
        get() = _binding!!
    private lateinit var dialog : BottomSheetDialog
    private val unitViewModel : UnitViewModel by viewModels()
    private var unitList : List<Unit>? = null
    private val viewModel : CreatePriceViewModel by viewModels()
    private val merchantSpecialPriceList = mutableListOf<SpecialPrice>()
    private val consumerSpecialPriceList = mutableListOf<SpecialPrice>()
    private val merchantSpecialPriceAdapter = SpecialPriceAdapter(merchantSpecialPriceList)
    private val consumerSpecialPriceAdapter = SpecialPriceAdapter(consumerSpecialPriceList)
    private val unitsAdapter = UnitsAdapter()

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

        unitsAdapter.setInitialUnit(viewModel.unit.value)

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
                        unitsAdapter.setList(l)
                    }
                }
            }
        }

        if (unitList == null){
            startShimmer()
            unitViewModel.getListUnit()
        }else{
            stopShimmer()
            unitsAdapter.setList(unitList!!)
        }

        unitsAdapter.onUnitChangedListener = {unit ->
            viewModel.setUnit(unit)
        }

        viewModel.unit.observe(viewLifecycleOwner){
            if (prevUnit != null && it != null){
                viewModel.setConnectorText(prevUnit, it)
            }
        }
        viewModel.isMerchantEnabled.observe(viewLifecycleOwner){
            binding.tilCreatePriceMerchant.inputLayout.isEnabled = it
            if (it){
                binding.apply {
                    tvCreatePriceMerchantSpecialTitle.visibility = View.VISIBLE
                    rvCreatePriceMerchantSpecial.visibility = View.VISIBLE
                    btnCreatePriceAddMerchantSpecial.visibility = View.VISIBLE
                    tilCreatePriceMerchant.hint = getString(R.string.price_hint)
                    btnCreatePriceMerchantVisibility.setImageDrawable(context?.getDrawable(R.drawable.ic_baseline_visibility_24))
                }
            }else{
                merchantSpecialPriceAdapter.clear()
                binding.apply {
                    tvCreatePriceMerchantSpecialTitle.visibility = View.GONE
                    rvCreatePriceMerchantSpecial.visibility = View.GONE
                    btnCreatePriceAddMerchantSpecial.visibility = View.GONE
                    tilCreatePriceMerchant.hint = getString(R.string.price_is_disabled)
                    btnCreatePriceMerchantVisibility.setImageDrawable(context?.getDrawable(R.drawable.ic_baseline_visibility_off_24))
                }
            }
        }
        viewModel.isConsumerEnabled.observe(viewLifecycleOwner){
            binding.tilCreatePriceConsumer.inputLayout.isEnabled = it
            if (it){
                binding.apply {
                    tvCreatePriceConsumerSpecialTitle.visibility = View.VISIBLE
                    rvCreatePriceConsumerSpecial.visibility = View.VISIBLE
                    btnCreatePriceAddConsumerSpecial.visibility = View.VISIBLE
                    tilCreatePriceConsumer.hint = getString(R.string.price_hint)
                    btnCreatePriceConsumerVisibility.setImageDrawable(context?.getDrawable(R.drawable.ic_baseline_visibility_24))
                }
            }else{
                consumerSpecialPriceAdapter.clear()
                binding.apply {
                    tvCreatePriceConsumerSpecialTitle.visibility = View.GONE
                    rvCreatePriceConsumerSpecial.visibility = View.GONE
                    btnCreatePriceAddConsumerSpecial.visibility = View.GONE
                    tilCreatePriceConsumer.hint = getString(R.string.price_is_disabled)
                    btnCreatePriceConsumerVisibility.setImageDrawable(context?.getDrawable(R.drawable.ic_baseline_visibility_off_24))
                }
            }
        }

        binding.apply {
            rvCreateItemUnits.adapter = unitsAdapter
            rvCreatePriceMerchantSpecial.adapter = merchantSpecialPriceAdapter
            rvCreatePriceConsumerSpecial.adapter = consumerSpecialPriceAdapter
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
            btnCreatePriceAddMerchantSpecial.setOnClickListener {
                merchantSpecialPriceAdapter.addSpecialPrice(SpecialPrice())
            }
            btnCreatePriceAddConsumerSpecial.setOnClickListener {
                consumerSpecialPriceAdapter.addSpecialPrice(SpecialPrice())
            }
            tvCreatePriceAddUnit.setOnClickListener {
                showCreateUnitDialog()
            }
            btnCreatePriceCancelDelete.setOnClickListener {
                dismiss()
            }
            btnCreatePriceSave.setOnClickListener {
                val merchantSpecialPriceValidity = merchantSpecialPriceAdapter.validate()
                val consumerSpecialPriceValidity = consumerSpecialPriceAdapter.validate()
                val priceValidity = viewModel.validate(isConnectorVisible, requireContext())
                binding.apply {
                    tilCreatePriceConnector.errorText = priceValidity.connectorMessage?.asString(requireContext())
                    tilCreatePriceBarcode.errorText = priceValidity.barcodeMessage?.asString(requireContext())
                    tvCreatePriceUnitError.text = priceValidity.unitMessage?.asString(requireContext())
                    tilCreatePriceMerchant.errorText = priceValidity.merchantMessage?.asString(requireContext())
                    tilCreatePriceConsumer.errorText = priceValidity.consumerMessage?.asString(requireContext())
                }
                if (!merchantSpecialPriceValidity || !consumerSpecialPriceValidity || !priceValidity.isAllValid) return@setOnClickListener
                val priceAndSubPrice = viewModel.getPriceAndSubPrice(merchantSpecialPriceList, consumerSpecialPriceList)
                onSaveListener(priceAndSubPrice)
                dismiss()
            }
            btnCreatePriceMerchantVisibility.setOnClickListener { viewModel.toggleMerchantEnabled() }
            btnCreatePriceConsumerVisibility.setOnClickListener { viewModel.toggleConsumerEnabled() }
        }
    }

    private fun showCreateUnitDialog(){
        CreateUnitDialog{
            unitsAdapter.addUnit(it)
        }.show(parentFragmentManager, null)
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