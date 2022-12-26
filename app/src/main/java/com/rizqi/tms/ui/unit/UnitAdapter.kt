package com.rizqi.tms.ui.unit

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rizqi.tms.databinding.ItemUnitBinding
import com.rizqi.tms.model.Unit

class UnitAdapter(
    private var unitList: MutableList<Unit> = mutableListOf()
) : RecyclerView.Adapter<UnitAdapter.UnitViewHolder>(){
    var onUpdateUnitListener : ((Unit, Int) -> kotlin.Unit)? = null
    var onDeleteListener : ((Unit) -> kotlin.Unit)? = null

    class UnitViewHolder(val binding : ItemUnitBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitViewHolder {
        return UnitViewHolder(ItemUnitBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: UnitViewHolder, position: Int) {
        val unit = unitList[position]
        val binding = holder.binding
        binding.tvItemUnitName.text = unit.name
        binding.btnItemUnitEdit.setOnClickListener { onUpdateUnitListener?.invoke(unit, position) }
        binding.btnItemUnitDelete.setOnClickListener { onDeleteListener?.invoke(unit) }
    }

    override fun getItemCount(): Int {
        return unitList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<Unit>){
        unitList = list.toMutableList()
        notifyDataSetChanged()
    }

    fun updateUnit(unit: Unit, position: Int){
        unitList[position] = unit
        notifyItemChanged(position)
    }

}