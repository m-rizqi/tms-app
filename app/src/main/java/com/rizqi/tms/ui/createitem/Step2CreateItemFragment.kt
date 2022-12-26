package com.rizqi.tms.ui.createitem

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.rizqi.tms.R
import com.rizqi.tms.databinding.FragmentStep2CreateItemBinding
import com.rizqi.tms.model.PriceAndSubPrice
import com.rizqi.tms.ui.dialog.createprice.CreatePriceBottomSheet
import com.rizqi.tms.utility.CrudState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Step2CreateItemFragment : Fragment() {
    private var _binding : FragmentStep2CreateItemBinding? = null
    private val binding : FragmentStep2CreateItemBinding
        get() = _binding!!
    private var stepChangedListener : OnStepChangedListener? = null
    private val createItemViewModel : CreateItemViewModel by activityViewModels()
    private var priceAndSubPriceList : MutableList<PriceAndSubPrice> = mutableListOf()
    private val priceAdapter = PriceAdapter(priceAndSubPriceList)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            if (context is Activity) stepChangedListener = context as OnStepChangedListener
        }catch (e : ClassCastException){
            throw ClassCastException(context.toString() + "must implement OnStepChangedListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStep2CreateItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        priceAdapter.onItemClickListener = {priceAndSubPrice, position ->
            showUpdatePriceDialog(priceAndSubPrice, position)
        }

        binding.apply {
            rvCreateItemPriceList.adapter = priceAdapter
            fabStep2CreateItemCreatePrice.setOnClickListener { showCreatePriceDialog() }
            btnStep2CreateItemNext.setOnClickListener {
                if (priceAndSubPriceList.isEmpty()){
                    Toast.makeText(context, getString(R.string.create_min_1_price), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                createItemViewModel.setPrices(priceAndSubPriceList)
                stepChangedListener?.changeToStep(3)
                it.findNavController().navigate(Step2CreateItemFragmentDirections.step2ToStep3CreateItem())
            }
        }

        createItemViewModel.prices.value?.let {
            priceAndSubPriceList = it
            priceAdapter.setList(priceAndSubPriceList)
        }

        if (priceAndSubPriceList.isEmpty()){
            showCreatePriceDialog()
        }

    }

    private fun showCreatePriceDialog(){
        activity?.let {
            CreatePriceBottomSheet(
                priceAndSubPriceList.isNotEmpty(),
                priceAndSubPriceList.lastOrNull()?.unit,
                priceAndSubPriceList.map { priceAndSubPrice ->  priceAndSubPrice.unit}
            ){priceAndSubPrice ->
                priceAdapter.addPriceAndSubPrice(priceAndSubPrice)
            }.show(it.supportFragmentManager, null)
        }
    }

    private fun showUpdatePriceDialog(priceAndSubPrice: PriceAndSubPrice, position : Int){
        activity?.let {
            CreatePriceBottomSheet(
                priceAndSubPrice.price.prevQuantityConnector != null,
                priceAndSubPriceList.find { priceSubPrice ->  priceSubPrice.price.unitName == priceAndSubPrice.price.prevUnitName}?.unit,
                priceAndSubPriceList.map { priceSubPrice ->  priceSubPrice.unit}.filter { unit -> unit?.name != priceAndSubPrice.price.unitName  },
                priceAndSubPrice,
                CrudState.UPDATE
            ){updatedPriceSubPrice ->
                priceAdapter.updatePriceAndSubPrice(updatedPriceSubPrice, position)
            }.apply {
                onDeleteListener = {
                    if (position < priceAdapter.itemCount - 1){
                        val posPrice = priceAdapter.getPriceAndSubPriceAt(position)
                        val nextPrice = priceAdapter.getPriceAndSubPriceAt(position + 1)
                        nextPrice.price.apply {
                            prevQuantityConnector = posPrice.price.prevQuantityConnector
                            prevUnitName = posPrice.price.prevUnitName
                        }
                    }
                    priceAdapter.deletePriceAndSubPrice(position)
                }
            }.show(it.supportFragmentManager, null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}