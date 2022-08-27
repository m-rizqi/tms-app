package com.rizqi.tms.ui.dialog.createprice

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ItemSpecialPriceBinding
import com.rizqi.tms.model.SpecialPrice
import com.rizqi.tms.utility.ThousandTextWatcher

class SpecialPriceAdapter(
    private var specialPriceList : MutableList<SpecialPrice>
) :
    RecyclerView.Adapter<SpecialPriceAdapter.SpecialPriceViewHolder>() {
    private var unitName : String = ""
    private var specialPriceHolderList : MutableList<SpecialPriceViewHolder> = mutableListOf()

    inner class SpecialPriceViewHolder(val binding : ItemSpecialPriceBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialPriceViewHolder {
        return SpecialPriceViewHolder(
            ItemSpecialPriceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: SpecialPriceViewHolder, position: Int) {
        if (holder !in specialPriceHolderList){
            specialPriceHolderList.add(holder)
        }
        val specialPrice = specialPriceList[position]
        val binding = holder.binding
        binding.tilSpecialPrice.editText.addTextChangedListener(ThousandTextWatcher(binding.tilSpecialPrice.editText))
        binding.tilSpecialPriceQuantity.editText.doAfterTextChanged {
            try {
                specialPrice.quantity = it.toString().toDouble()
            }catch (e : Exception){}
        }
        binding.tilSpecialPrice.editText.doAfterTextChanged {
            try {
                specialPrice.price = it.toString().replace(".","").replace(",", ".").toDouble()
            }catch (e : Exception){}
        }

        if (specialPrice.price != 0.0){
            binding.tilSpecialPrice.editText.setText(specialPrice.price.toLong().toString())
        }
        if (specialPrice.quantity != 0.0){
            binding.tilSpecialPriceQuantity.editText.setText(specialPrice.quantity.toString())
        }

        binding.btnSpecialPriceDelete.setOnClickListener {
            removeAt(position)
        }
//        binding.tilSpecialPriceQuantity.suffixText = unitName
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setUnitName(value : String){
        unitName = value
        notifyDataSetChanged()
    }

    override fun getItemCount() = specialPriceList.size

    fun addSpecialPrice(specialPrice: SpecialPrice){
        specialPriceList.add(specialPrice)
        notifyItemInserted(specialPriceList.lastIndex)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun removeAt(position: Int){
        specialPriceList.removeAt(position)
        specialPriceHolderList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount - position)
    }

    fun validate(): Boolean {
        var isAllValid = true
        specialPriceHolderList.forEach {
            val binding = it.binding
            val context = binding.root.context
            if (binding.tilSpecialPriceQuantity.editText.text.toString().isBlank()){
                isAllValid = isAllValid && false
                binding.tilSpecialPriceQuantity.errorText = context.getString(R.string.field_must_be_filled, context.getString(R.string.this_part))
            }else{
                isAllValid = isAllValid && true
                binding.tilSpecialPriceQuantity.errorText = null
            }

            if (binding.tilSpecialPrice.editText.text.toString().isBlank()){
                isAllValid = isAllValid && false
                binding.tilSpecialPrice.errorText = context.getString(R.string.field_must_be_filled, context.getString(R.string.this_part))
            }else{
                isAllValid = isAllValid && true
                binding.tilSpecialPrice.errorText = null
            }
        }

        return isAllValid
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear(){
        specialPriceList.clear()
        specialPriceHolderList.clear()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: MutableList<SpecialPrice>){
        specialPriceList = list
        specialPriceHolderList.clear()
        notifyDataSetChanged()
    }

}