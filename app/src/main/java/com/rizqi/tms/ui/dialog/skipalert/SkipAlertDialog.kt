package com.rizqi.tms.ui.dialog.skipalert

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.rizqi.tms.R
import com.rizqi.tms.databinding.DialogSkipAlertBinding

class SkipAlertDialog(
    private val onPositiveClickListener : () -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val binding : DialogSkipAlertBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_skip_alert, null, false)
            binding.ibSkipXmark.setOnClickListener { dismiss() }
            binding.btnSkipCancel.setOnClickListener { dismiss() }
            binding.btnSkipYes.setOnClickListener { onPositiveClickListener() }
            builder.setView(binding.root)
            builder.create().apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}