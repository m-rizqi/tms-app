package com.rizqi.tms.ui.createitem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rizqi.tms.databinding.ItemPriceBinding
import com.rizqi.tms.model.PriceAndSubPrice

class PriceAdapter(
    private var priceAndSubPriceList: MutableList<PriceAndSubPrice>
) : RecyclerView.Adapter<PriceAdapter.PriceViewHolder>(){
    class PriceViewHolder(val binding : ItemPriceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PriceViewHolder {
        return PriceViewHolder(ItemPriceBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: PriceViewHolder, position: Int) {

    }

    override fun getItemCount() = priceAndSubPriceList.size

    fun addPriceAndSubPrice(priceAndSubPrice: PriceAndSubPrice){
        priceAndSubPriceList.add(priceAndSubPrice)
        notifyItemInserted(priceAndSubPriceList.lastIndex)
    }
}