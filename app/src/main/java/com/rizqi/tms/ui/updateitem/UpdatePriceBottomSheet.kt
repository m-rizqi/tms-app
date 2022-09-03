package com.rizqi.tms.ui.updateitem

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
import com.rizqi.tms.R
import com.rizqi.tms.databinding.BottomSheetCreatePriceBinding
import com.rizqi.tms.model.Info
import com.rizqi.tms.model.PriceAndSubPrice
import com.rizqi.tms.model.SpecialPrice
import com.rizqi.tms.model.Unit
import com.rizqi.tms.ui.dialog.createprice.CreatePriceViewModel
import com.rizqi.tms.ui.dialog.createprice.SpecialPriceAdapter
import com.rizqi.tms.ui.dialog.createprice.UnitsAdapter
import com.rizqi.tms.ui.dialog.createunit.CreateUnitDialog
import com.rizqi.tms.ui.dialog.info.InfoDialog
import com.rizqi.tms.ui.scan.ScanBarcodeActivity
import com.rizqi.tms.utility.*
import com.rizqi.tms.viewmodel.UnitViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdatePriceBottomSheet(
    private val isConnectorVisible : Boolean = false,
    private val prevUnit: Unit? = null,
    private val selectedUnitList: List<Unit?>,
    private var isMainPrice : Boolean,
    private val updatePriceAndSubPrice: PriceAndSubPrice? = null,
    private val crudState: CrudState = CrudState.CREATE,
    private val onSaveListener : (PriceAndSubPrice) -> kotlin.Unit,
) : BottomSheetDialogFragment(){
    private var _binding : BottomSheetCreatePriceBinding? = null
    val binding : BottomSheetCreatePriceBinding
        get() = _binding!!
    private lateinit var dialog : BottomSheetDialog
    private val unitViewModel : UnitViewModel by viewModels()
    private var unitList : List<Unit>? = null
    private val viewModel : CreatePriceViewModel by viewModels()
    private var merchantSpecialPriceList = mutableListOf<SpecialPrice>()
    private var consumerSpecialPriceList = mutableListOf<SpecialPrice>()
    private val merchantSpecialPriceAdapter = SpecialPriceAdapter(merchantSpecialPriceList)
    private val consumerSpecialPriceAdapter = SpecialPriceAdapter(consumerSpecialPriceList)
    private val unitsAdapter = UnitsAdapter(selectedUnitList)
    var onDeleteListener : (() -> kotlin.Unit)? = null

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

        binding.lCreatePriceIsMainPrice.visibility = View.VISIBLE

        viewModel.setPrevUnit(prevUnit)
        if (crudState == CrudState.UPDATE && updatePriceAndSubPrice != null){
            viewModel.setUpdatePriceAndSubPrice(updatePriceAndSubPrice)
            binding.apply {
                tilCreatePriceBarcode.editText.setText(updatePriceAndSubPrice.price.barcode)
                tilCreatePriceMerchant.editText.setText(ThousandFormatter.format(updatePriceAndSubPrice.merchantSubPrice.subPrice.price))
                tilCreatePriceConsumer.editText.setText(ThousandFormatter.format(updatePriceAndSubPrice.consumerSubPrice.subPrice.price))
                merchantSpecialPriceList = updatePriceAndSubPrice.merchantSubPrice.specialPrices.toMutableList()
                merchantSpecialPriceAdapter.setList(merchantSpecialPriceList)
                consumerSpecialPriceList = updatePriceAndSubPrice.consumerSubPrice.specialPrices.toMutableList()
                consumerSpecialPriceAdapter.setList(consumerSpecialPriceList)
                btnCreatePriceCancelDelete.text = getString(R.string.delete_allcaps)
                tilCreatePriceConnector.editText.setText(updatePriceAndSubPrice.price.prevQuantityConnector?.toFormattedString())
                switchCreatePriceMainPrice.isChecked = isMainPrice
                setSwitchTheme(binding.switchCreatePriceMainPrice, isMainPrice)
                if (updatePriceAndSubPrice.price.isMainPrice) {
                    binding.lCreatePriceIsMainPrice.visibility = View.GONE
                }
                viewModel.setIsMainPrice(updatePriceAndSubPrice.price.isMainPrice)
            }
        }

        unitViewModel.getListUnit().observe(viewLifecycleOwner){
            unitList = it
            unitsAdapter.setList(unitList!!)
        }

        unitsAdapter.onUnitChangedListener = {unit ->
            viewModel.setUnit(unit)
        }

        viewModel.unit.value?.let {
            unitsAdapter.setInitialUnit(it)
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
                    tilCreatePriceMerchant.editText.setText("")
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
                    tilCreatePriceConsumer.editText.setText("")
                    tilCreatePriceConsumer.hint = getString(R.string.price_is_disabled)
                    btnCreatePriceConsumerVisibility.setImageDrawable(context?.getDrawable(R.drawable.ic_baseline_visibility_off_24))
                }
            }
        }

        viewModel.connectorText.observe(this){
            binding.tilCreatePriceConnector.suffixText = it
        }

        binding.apply {
            switchCreatePriceMainPrice.setOnCheckedChangeListener { _, b ->
                viewModel.setIsMainPrice(b)
                setSwitchTheme(binding.switchCreatePriceMainPrice, b)
            }
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
                when(crudState){
                    CrudState.CREATE -> dismiss()
                    else -> {
                        onDeleteListener?.invoke()
                        dismiss()
                    }
                }
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
                val priceAndSubPrice = when(crudState){
                    CrudState.CREATE -> viewModel.getPriceAndSubPrice(merchantSpecialPriceList, consumerSpecialPriceList)
                    else -> {
                        viewModel.getUpdatedPriceAndSubPrice(merchantSpecialPriceList, consumerSpecialPriceList)
                    }
                }
                onSaveListener(priceAndSubPrice)
                dismiss()
            }
            btnCreatePriceMerchantVisibility.setOnClickListener { viewModel.toggleMerchantEnabled() }
            btnCreatePriceConsumerVisibility.setOnClickListener { viewModel.toggleConsumerEnabled() }
            lCreatePriceConnectorTitle.btnQuestion.setOnClickListener { showInfoDialog(Info.Connector()) }
            tvCreatePriceBarcodeTitle.btnQuestion.setOnClickListener { showInfoDialog(Info.Barcode()) }
            lCreatePriceUnitTitle.btnQuestion.setOnClickListener { showInfoDialog(Info.Unit()) }
            lCreatePriceMerchantTitle.btnQuestion.setOnClickListener { showInfoDialog(Info.MerchantPrice()) }
            lCreatePriceConsumerTitle.btnQuestion.setOnClickListener { showInfoDialog(Info.ConsumerPrice()) }
        }
    }

    private fun showInfoDialog(info: Info){
        InfoDialog(info).show(parentFragmentManager, null)
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

//    private fun startShimmer(){
//        binding.shimmerCreatePriceUnits.apply {
//            startShimmer()
//            visibility = View.VISIBLE
//        }
//    }
//
//    private fun stopShimmer(){
//        binding.shimmerCreatePriceUnits.apply {
//            stopShimmer()
//            visibility = View.GONE
//        }
//    }

}