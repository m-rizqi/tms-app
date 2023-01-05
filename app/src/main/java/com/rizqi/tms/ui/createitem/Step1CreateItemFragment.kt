package com.rizqi.tms.ui.createitem

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.rizqi.tms.databinding.FragmentStep1CreateItemBinding
import com.rizqi.tms.ui.takeimage.TakeImageActivity
import com.rizqi.tms.utility.StringResource
import com.rizqi.tms.utility.getAndDecodeBitmap

class Step1CreateItemFragment : Fragment() {
    private var _binding : FragmentStep1CreateItemBinding? = null
    private val binding : FragmentStep1CreateItemBinding
        get() = _binding!!
    private val createItemViewModel : CreateItemViewModel by activityViewModels()
    private var stepChangedListener : OnStepChangedListener? = null

    private val takeImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == RESULT_OK && it.data != null){
            getAndDecodeBitmap(context)?.let { bitmap ->
                binding.ivStep1CreateItemImage.setImageBitmap(bitmap)
                createItemViewModel.setImage(bitmap)
            }
        }
    }

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
        _binding = FragmentStep1CreateItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStep1CreateItemTakeImage.setOnClickListener {
            takeImageLauncher.launch(Intent(context, TakeImageActivity::class.java))
        }
        binding.tilStep1CreateItemName.editText.doAfterTextChanged { createItemViewModel.setName(it.toString()) }
        binding.btnStep1CreateItemNext.setOnClickListener {
            val validation = createItemViewModel.validateStep1(requireContext())
            binding.tilStep1CreateItemName.errorText = validation.nameError?.asString(requireContext())
            if (validation.nameError != null) return@setOnClickListener
            it.findNavController().navigate(Step1CreateItemFragmentDirections.step1ToStep2CreateItem())
            stepChangedListener?.changeToStep(2)
        }
    }

    override fun onResume() {
        super.onResume()
        createItemViewModel.image.value?.let { binding.ivStep1CreateItemImage.setImageBitmap(it) }
        createItemViewModel.name.value?.let { binding.tilStep1CreateItemName.editText.setText(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class Step1CreateItemValidation(
        val nameError : StringResource?
    )

}