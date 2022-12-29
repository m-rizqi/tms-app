package com.rizqi.tms.ui.createitem

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.rizqi.tms.databinding.FragmentStep3CreateItemBinding
import com.rizqi.tms.utility.saveBitmapToFolder
import com.rizqi.tms.utility.setCheckedAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Step3CreateItemFragment : Fragment() {
    private var _binding : FragmentStep3CreateItemBinding? = null
    private val binding : FragmentStep3CreateItemBinding
        get() = _binding!!
    private var stepChangedListener : OnStepChangedListener? = null
    private val createItemViewModel : CreateItemViewModel by activityViewModels()

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
        _binding = FragmentStep3CreateItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainPriceAdapter = createItemViewModel.prices.value?.let { MainPriceAdapter(it) }
        binding.rvStep3CreateItemPriceList.adapter = mainPriceAdapter
        binding.cbStep3CreateItemRemindMe.setOnCheckedChangeListener { _, b ->
            setCheckedAdapter(binding.cbStep3CreateItemRemindMe, b)
            createItemViewModel.setIsReminded(b)
        }
        binding.btnStep3CreateItemSave.setOnClickListener {
            stepChangedListener?.showLoading()
            val imagePath = requireContext().saveBitmapToFolder(createItemViewModel.image.value)
            val item = createItemViewModel.getCreatedItem(imagePath)
            stepChangedListener?.onJourneyFinished(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}