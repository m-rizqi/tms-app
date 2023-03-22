package com.rizqi.tms.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.view.Window
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.rizqi.tms.R
import com.rizqi.tms.databinding.DialogWarningBinding

abstract class WarningDialog(
    private val onPositiveClickListener : () -> Unit,
    @StringRes
    private val titleRes : Int,
    @StringRes
    private val descriptionRes : Int
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val binding : DialogWarningBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_warning, null, false)
            binding.tvAlertDialogTitle.text = Html.fromHtml(
                getString(titleRes)
            )
            binding.tvAlertDialogDescription.text = Html.fromHtml(
                getString(descriptionRes)
            )
            binding.ibWarningXmark.setOnClickListener { dismiss() }
            binding.btnWarningCancel.setOnClickListener { dismiss() }
            binding.btnWarningYes.setOnClickListener {
                onPositiveClickListener()
                dismiss()
            }
            builder.setView(binding.root)
            builder.create().apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}