package com.rizqi.tms.ui.dialog.totalpaychangemoney

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rizqi.tms.R
import com.rizqi.tms.databinding.BottomSheetTotalPayChangeMoneyBinding
import com.rizqi.tms.utility.ThousandFormatter
import com.rizqi.tms.utility.ThousandTextWatcher

class TotalPayChangeMoneyBottomSheet(
    private val mTotal : Long,
    private val onApplyListener : (Long, Long) -> Unit
) : BottomSheetDialogFragment(){
    private var _binding : BottomSheetTotalPayChangeMoneyBinding? = null
    val binding : BottomSheetTotalPayChangeMoneyBinding
        get() = _binding!!
    private lateinit var dialog : BottomSheetDialog
    private var mMoneyChange = 0L
    private var mPaid = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetTotalPayChangeMoneyBinding.inflate(inflater, container, false)
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

        binding.apply {
            total = getString(R.string.rp_, ThousandFormatter.format(mTotal))
            tilTotalPayChangeMoneyPay.editText.addTextChangedListener(ThousandTextWatcher(binding.tilTotalPayChangeMoneyPay.editText))
            tilTotalPayChangeMoneyPay.editText.doAfterTextChanged {
                mPaid = try {
                    it.toString().replace(".", "").toLong()
                }catch (_ : Exception){0}
                mMoneyChange = mPaid - mTotal
                binding.moneyChange = getString(R.string.rp_, ThousandFormatter.format(mMoneyChange))
            }
            buttonTotalPayChangeMoneyRight.setOnClickListener {
                tilTotalPayChangeMoneyPay.editText.setText("$mTotal")
            }
            btnTotalPayChangeMoneyFinish.setOnClickListener {
                if (binding.tilTotalPayChangeMoneyPay.editText.text.toString().isEmpty()){
                    binding.tilTotalPayChangeMoneyPay.errorText = getString(R.string.field_must_be_filled, getString(R.string.pay))
                    Toast.makeText(context, getString(R.string.pay_must_be_filled), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                onApplyListener(mPaid, mMoneyChange)
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}