package com.rizqi.tms.ui.dialog.createprice

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ChipUnitBinding
import com.rizqi.tms.model.Unit

class UnitsAdapter(
    private val selectedUnitList: List<Unit?>
) : RecyclerView.Adapter<UnitsAdapter.UnitViewHolder>() {
    private var unitList : MutableList<Unit> = mutableListOf()
    private var unitViewHolderList : MutableList<UnitViewHolder> = mutableListOf()
    private var initialUnit : Unit? = null
    var onUnitChangedListener : ((Unit) -> kotlin.Unit)? = null

    inner class UnitViewHolder(val binding : ChipUnitBinding) : RecyclerView.ViewHolder(binding.root)

    init {
        findAvailableUnit()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitViewHolder {
        return UnitViewHolder(
            ChipUnitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: UnitViewHolder, position: Int) {
        if (
            holder !in unitViewHolderList
        ){
            unitViewHolderList.add(holder)
        }
        val unit = unitList[position]
        val binding = holder.binding
        binding.chip.text = unit.name
        binding.chip.setOnClickListener {
            onChipCheckedChanged(holder, position, binding.chip.isChecked, unit)
        }
        if (
            (initialUnit != null && initialUnit?.name == unit.name)
        ){
            binding.chip.isChecked = true
            onChipCheckedChanged(holder, position, true, unit)
        }
        if (isInSelectedUnit(unit)){
            setDisableChip(binding.chip)
        }
    }

    override fun getItemCount() = unitList.size

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<Unit>){
        unitList = list.toMutableList()
        unitViewHolderList.clear()
        findAvailableUnit()
        notifyDataSetChanged()
    }

    private fun isInSelectedUnit(unit: Unit): Boolean {
        return unit.name in selectedUnitList.map { it?.name }
    }

    private fun findAvailableUnit(){
        if (initialUnit == null){
            initialUnit =
                unitList.firstOrNull { it.name !in selectedUnitList.map { sUnit -> sUnit?.name } }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addUnit(unit: Unit){
        unitList.add(unit)
        notifyItemInserted(unitList.lastIndex)
        if (initialUnit == null){
            findAvailableUnit()
            notifyDataSetChanged()
        }
    }

    private fun onChipCheckedChanged(holder: UnitViewHolder, position: Int, isChecked : Boolean, unit: Unit){
        if (isInSelectedUnit(unit)){
//            holder.binding.chip.isChecked = false
//            setDisableChip(holder.binding.chip)
            val context = holder.binding.root.context
            Toast.makeText(holder.binding.root.context, context.getString(R.string.this_unit_has_been_selected), Toast.LENGTH_SHORT).show()
//            return
        }
        if (!isChecked){
            holder.binding.chip.isChecked = true
            return
        }
        setCheckedChip(holder.binding.chip)
        unitViewHolderList.forEachIndexed { index, viewHolder ->
            if (index != position && isInSelectedUnit(unitList[index])){
                viewHolder.binding.chip.isChecked = false
                setDisableChip(viewHolder.binding.chip)
            }
            else if (index != position){
                viewHolder.binding.chip.isChecked = false
                setUnCheckedChip(viewHolder.binding.chip)
            }
        }
        onUnitChangedListener?.invoke(unit)
    }

    private fun setCheckedChip(chip: Chip){
        val resources = chip.resources
        chip.apply {
            chipBackgroundColor = ColorStateList.valueOf(resources.getColor(R.color.primary_20))
            chipStrokeColor = ColorStateList.valueOf(resources.getColor(R.color.primary_100))
            setTextColor(resources.getColor(R.color.black_100))
        }
    }

    private fun setUnCheckedChip(chip: Chip){
        val resources = chip.resources
        chip.apply {
            chipBackgroundColor = ColorStateList.valueOf(resources.getColor(R.color.black_10))
            chipStrokeColor = ColorStateList.valueOf(resources.getColor(R.color.black_20))
            setTextColor(resources.getColor(R.color.black_80))
        }
    }

    private fun setDisableChip(chip: Chip){
        val resources = chip.resources
        chip.apply {
            chipBackgroundColor = ColorStateList.valueOf(resources.getColor(R.color.disabled))
            chipStrokeColor = ColorStateList.valueOf(resources.getColor(R.color.black_20))
            setTextColor(resources.getColor(R.color.black_40))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setInitialUnit(unit: Unit?) {
        initialUnit = unit
        notifyDataSetChanged()
    }

}