package com.rizqi.tms.ui.dialog.info

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.rizqi.tms.R
import com.rizqi.tms.databinding.DialogInfoBinding
import com.rizqi.tms.databinding.DialogSkipAlertBinding
import com.rizqi.tms.model.Info

class InfoDialog(
    private val info: Info
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val binding : DialogInfoBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_info, null, false)
            binding.ibSkipXmark.setOnClickListener { dismiss() }
            binding.btnSkipCancel.setOnClickListener { dismiss() }
            binding.title = getString(info.title)
            binding.description = getString(info.description)
            builder.setView(binding.root)
            builder.create().apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}