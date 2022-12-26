package com.rizqi.tms.ui.dialog.finishconfirmation

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import com.rizqi.tms.databinding.DialogFinishConfirmationBinding
import com.rizqi.tms.databinding.DialogItemNotFoundBinding

class FinishConfirmationDialog(
    private val onPositiveClickListener : () -> Unit,
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val binding = DialogFinishConfirmationBinding.inflate(inflater, null, false)
            binding.btnFinishConfirmationNo.setOnClickListener {
                dismiss()
            }
            binding.btnFinishConfirmationYes.setOnClickListener {
                dismiss()
                onPositiveClickListener()
            }
            builder.setView(binding.root)
            builder.create().apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}