package com.rizqi.tms.ui.unit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityUnitListBinding
import com.rizqi.tms.model.Unit
import com.rizqi.tms.ui.dialog.createunit.CreateUpdateUnitDialog
import com.rizqi.tms.utility.copy
import com.rizqi.tms.utility.showLoading
import com.rizqi.tms.viewmodel.UnitViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UnitListActivity : AppCompatActivity() {
    private lateinit var binding : ActivityUnitListBinding
    private val unitViewModel : UnitViewModel by viewModels()
    private val unitAdapter = UnitAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUnitListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        unitAdapter.onUpdateUnitListener = {unit, i ->
            showUpdateUnitDialog(unit, i)
        }
        unitAdapter.onDeleteListener = {unit ->
            unitViewModel.deleteUnit(unit)
        }

        binding.apply {
            rvUnitList.adapter = unitAdapter
            btnUnitListBack.setOnClickListener { onBackPressed() }
            fabUnitListCreate.setOnClickListener { CreateUpdateUnitDialog({}).show(supportFragmentManager, null) }
        }

        unitViewModel.getListUnit().observe(this){
            unitAdapter.setList(it)
        }

    }

    private fun showUpdateUnitDialog(unit: Unit, position : Int){
        CreateUpdateUnitDialog(
            {
                unitAdapter.updateUnit(it, position)
            }, unit
        ).show(supportFragmentManager, null)
    }
}