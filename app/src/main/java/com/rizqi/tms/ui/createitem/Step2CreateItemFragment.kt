package com.rizqi.tms.ui.createitem

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.rizqi.tms.R
import com.rizqi.tms.databinding.FragmentStep2CreateItemBinding
import com.rizqi.tms.ui.dialog.createprice.CreatePriceBottomSheet

class Step2CreateItemFragment : Fragment() {
    private var _binding : FragmentStep2CreateItemBinding? = null
    private val binding : FragmentStep2CreateItemBinding
        get() = _binding!!
    private var stepChangedListener : OnStepChangedListener? = null
    private val createItemViewModel : CreateItemViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            if (context is Activity) stepChangedListener = context as OnStepChangedListener
        }catch (e : ClassCastException){
            throw ClassCastException(context.toString() + "must implement OnStepChagedListener")
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

        if (createItemViewModel.isPricesEmpty()){
            showPriceBottomSheet()
        }

    }

    private fun showPriceBottomSheet(){
        CreatePriceBottomSheet().show(parentFragmentManager, null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}