package com.rizqi.tms.ui.dialog.createprice

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ChipUnitBinding
import com.rizqi.tms.model.Unit

class UnitsAdapter(
) : RecyclerView.Adapter<UnitsAdapter.UnitViewHolder>() {
    private var unitList : MutableList<Unit> = mutableListOf()
    private var unitViewHolderList : MutableList<UnitViewHolder> = mutableListOf()
    private var initialUnit : Unit? = null
    var onUnitChangedListener : ((Unit) -> kotlin.Unit)? = null

    inner class UnitViewHolder(val binding : ChipUnitBinding) : RecyclerView.ViewHolder(binding.root)

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
            (initialUnit != null && initialUnit?.name == unit.name) ||
            (initialUnit == null && position == 0)
        ){
            binding.chip.isChecked = true
            onChipCheckedChanged(holder, position, true, unit)
        }
    }

    override fun getItemCount() = unitList.size

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<Unit>){
        unitList = list.toMutableList()
        unitViewHolderList.clear()
        notifyDataSetChanged()
    }

    fun addUnit(unit: Unit){
        unitList.add(unit)
        notifyItemInserted(unitList.lastIndex)
    }

    private fun onChipCheckedChanged(holder: UnitViewHolder, position: Int, isChecked : Boolean, unit: Unit){
        if (!isChecked){
            holder.binding.chip.isChecked = true
            return
        }
        setCheckedChip(holder.binding.chip)
        unitViewHolderList.forEachIndexed { index, viewHolder ->
            if (index != position){
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

    @SuppressLint("NotifyDataSetChanged")
    fun setInitialUnit(unit: Unit?) {
        initialUnit = unit
        notifyDataSetChanged()
    }

}