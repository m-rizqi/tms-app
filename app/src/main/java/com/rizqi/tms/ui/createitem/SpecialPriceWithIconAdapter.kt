package com.rizqi.tms.ui.createitem

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ItemSpecialPriceWithIconBinding
import com.rizqi.tms.model.SpecialPrice
import com.rizqi.tms.model.SpecialPriceWithIcon
import com.rizqi.tms.utility.ThousandFormatter
import com.rizqi.tms.utility.toFormattedString

class SpecialPriceWithIconAdapter(
    private var specialPriceList: List<SpecialPriceWithIcon>,
    private val unitName : String
) : RecyclerView.Adapter<SpecialPriceWithIconAdapter.SpecialPriceViewHolder>(){
    class SpecialPriceViewHolder(val binding : ItemSpecialPriceWithIconBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialPriceViewHolder {
        return SpecialPriceViewHolder(ItemSpecialPriceWithIconBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: SpecialPriceViewHolder, position: Int) {
        val specialPrice = specialPriceList[position].specialPrice
        val iconType = specialPriceList[position].iconType
        val binding = holder.binding
        val context = binding.root.context

        binding.text = context.getString(R.string.special_price_format, "${specialPrice.quantity.toFormattedString()} $unitName", ThousandFormatter.format(specialPrice.price))
        binding.ivSpecialPriceIconType.setImageDrawable(
            context.resources.getDrawable(iconType.iconRes)
        )
    }

    override fun getItemCount() = specialPriceList.size
}