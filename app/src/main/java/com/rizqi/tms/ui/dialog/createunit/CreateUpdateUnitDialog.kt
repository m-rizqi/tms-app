package com.rizqi.tms.ui.dialog.createunit

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.rizqi.tms.R
import com.rizqi.tms.databinding.DialogCreateUnitBinding
import com.rizqi.tms.utility.Resource
import com.rizqi.tms.viewmodel.UnitViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateUpdateUnitDialog(
    private val onNewUnitCreated : (com.rizqi.tms.model.Unit) -> Unit,
    private val requestUnit : com.rizqi.tms.model.Unit? = null
) : DialogFragment() {
    private val unitViewModel : UnitViewModel by viewModels()
    private var _binding : DialogCreateUnitBinding? = null
    private val binding : DialogCreateUnitBinding
        get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val inflater = requireActivity().layoutInflater
            _binding = DataBindingUtil.inflate(inflater, R.layout.dialog_create_unit, null, false)
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

        if (requestUnit != null) {
            binding.tilCreateUnitName.editText.setText(requestUnit.name)
            binding.tvCreateUnitTitle.text = getString(R.string.edit_unit)
        }

        binding.btnCreateUnitCancel.setOnClickListener { dismiss() }
        binding.btnCreateUnitSave.setOnClickListener {
            val name = binding.tilCreateUnitName.editText.text.toString()
            binding.tilCreateUnitName.inputLayout.error = if (name.isNotBlank()) null else {
                resources.getString(R.string.field_must_be_filled, resources.getString(R.string.name))
            }
            if (name.isBlank()) return@setOnClickListener
            val resultUnit = requestUnit?.apply {
                this.name = name
            } ?: com.rizqi.tms.model.Unit(name)
            showLoading()
            val createUnitObserver = Observer<Resource<Long>>{res ->
                when(res){
                    is Resource.Error -> {
                        hideLoading()
                        Toast.makeText(context, res.message?.asString(requireContext()), Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Success -> {
                        hideLoading()
                        onNewUnitCreated(resultUnit.copy(id = res.data))
                        dismiss()
                    }
                }
            }
            unitViewModel.insertUnit(resultUnit)
            unitViewModel.insertUnit.observe(viewLifecycleOwner, createUnitObserver)
        }
    }

    private fun showLoading(){
        binding.progressBarCreateUnit.visibility = View.VISIBLE
    }

    private fun hideLoading(){
        binding.progressBarCreateUnit.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}