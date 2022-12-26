package com.rizqi.tms.ui.dialog.adjusttotalprice

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.rizqi.tms.R
import com.rizqi.tms.databinding.DialogAdjustTotalPriceBinding
import com.rizqi.tms.utility.ThousandFormatter
import com.rizqi.tms.utility.ThousandTextWatcher
import kotlin.math.ceil

class AdjustTotalPriceDialog(
    private val itemName : String,
    private val requestTotalPrice : Long,
    private val onApplyListener : (Long) -> Unit
) : DialogFragment() {
    private var _binding : DialogAdjustTotalPriceBinding? = null
    private val binding : DialogAdjustTotalPriceBinding
        get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val inflater = requireActivity().layoutInflater
            _binding = DialogAdjustTotalPriceBinding.inflate(inflater, null, false)
            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)
            builder.create().apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.itemName = itemName
        binding.tilAdjustTotalPrice.editText.addTextChangedListener(ThousandTextWatcher(binding.tilAdjustTotalPrice.editText))
        binding.tilAdjustTotalPrice.editText.setText(ThousandFormatter.format(requestTotalPrice))
        binding.btnAdjustTotalCancel.setOnClickListener {
            dismiss()
        }
        binding.btnAdjustTotalSave.setOnClickListener {
            if (binding.tilAdjustTotalPrice.editText.text.toString().isBlank()){
                binding.tilAdjustTotalPrice.errorText = getString(R.string.field_must_be_filled, getString(R.string.total_price))
                return@setOnClickListener
            }
            var totalPrice = 0L
            try {
                totalPrice = binding.tilAdjustTotalPrice.editText.text.toString().replace(".","").toLong()
            }catch (e : Exception){
                binding.tilAdjustTotalPrice.errorText = getString(R.string.input_format_must_number)
                return@setOnClickListener
            }
            onApplyListener(totalPrice)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}