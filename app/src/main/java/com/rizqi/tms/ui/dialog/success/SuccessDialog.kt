package com.rizqi.tms.ui.dialog.success

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.rizqi.tms.R
import com.rizqi.tms.databinding.DialogSuccessBinding

class SuccessDialog(
    private val description : String = "",
    private val onDismissListener : () -> Unit,
    private val title : String? = null,
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val binding : DialogSuccessBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_success, null, false)
            binding.ibSuccessXmark.setOnClickListener {
                dismiss()
            }
            binding.btnSuccessClose.setOnClickListener { dismiss() }
            if (title != null){
                binding.tvSuccessTitle.text = Html.fromHtml(title)
            }
            binding.tvSuccessDescription.text = Html.fromHtml(description)
            builder.setView(binding.root)
            builder.create().apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener()
    }
}