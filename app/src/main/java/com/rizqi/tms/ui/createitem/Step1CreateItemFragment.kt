package com.rizqi.tms.ui.createitem

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.rizqi.tms.databinding.FragmentStep1CreateItemBinding
import com.rizqi.tms.ui.getAndDecodeBitmapTemporary
import com.rizqi.tms.ui.takeimage.TakeImageActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Step1CreateItemFragment : Fragment() {

    private var _binding : FragmentStep1CreateItemBinding? = null
    private val binding : FragmentStep1CreateItemBinding
        get() = _binding!!
    private val viewModel : CreateItemViewModel by activityViewModels()

    private val takeImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK && it.data != null){
            CoroutineScope(Dispatchers.Main).launch{
                getAndDecodeBitmapTemporary(context)?.let { bitmap ->
                    viewModel.setItemImage(bitmap)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStep1CreateItemBinding.inflate(inflater, container, false)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.step1UiState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect { step1UiState ->
                    step1UiState.itemImage.let {
                        binding.ivStep1CreateItemImage.setImageBitmap(it)
                    }
                    step1UiState.requestNextStepSuccess.let {
                        if (it){
                            binding.root.findNavController().navigate(Step1CreateItemFragmentDirections.step1ToStep2CreateItem())
                        }
                    }
                    step1UiState.itemNameErrorMessage.let {
                        binding.tilStep1CreateItemName.errorText = it?.asString(requireContext())
                        binding.tilStep1CreateItemName.inputLayout.requestFocus()
                    }
                }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnStep1CreateItemTakeImage.setOnClickListener {
                takeImageLauncher.launch(
                    Intent(context, TakeImageActivity::class.java)
                )
            }
            tilStep1CreateItemName.editText.doAfterTextChanged { viewModel.setItemName(it.toString()) }
            btnStep1CreateItemNext.setOnClickListener {
                viewModel.step1RequestNextStep()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.step1UiState.value.let {
            binding.ivStep1CreateItemImage.setImageBitmap(it.itemImage)
            binding.tilStep1CreateItemName.editText.setText(it.itemName)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}