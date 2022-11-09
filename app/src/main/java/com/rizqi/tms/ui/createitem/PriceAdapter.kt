package com.rizqi.tms.ui.createitem

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ItemPriceBinding
import com.rizqi.tms.model.PriceAndSubPrice
import com.rizqi.tms.model.SpecialPriceWithIcon
import com.rizqi.tms.model.SubPriceIconType
import com.rizqi.tms.utility.*
import com.rizqi.tms.utility.dp

class PriceAdapter(
    private var priceAndSubPriceList: MutableList<PriceAndSubPrice>
) : RecyclerView.Adapter<PriceAdapter.PriceViewHolder>(){
    var onItemClickListener : ((PriceAndSubPrice, Int) -> Unit)? = null

    class PriceViewHolder(val binding : ItemPriceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PriceViewHolder {
        return PriceViewHolder(ItemPriceBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: PriceViewHolder, position: Int) {
        val priceAndSubPrice = priceAndSubPriceList[position]
        val price = priceAndSubPrice.price
        val merchantSubPrice = priceAndSubPrice.merchantSubPrice.subPrice
        val consumerSubPrice = priceAndSubPrice.consumerSubPrice.subPrice
        val merchantSpecialPrice = priceAndSubPrice.merchantSubPrice.specialPrices
        val consumerSpecialPrice = priceAndSubPrice.consumerSubPrice.specialPrices
        val binding = holder.binding
        val context = binding.root.context
        val resources = context.resources

        binding.apply {
            isConnectorVisible = price.prevQuantityConnector != null
            barcode = price.barcode
            unitName = price.unitName
//            connectorText = "${price.prevQuantityConnector} ${price.prevUnitName} = 1 ${price.unitName}"
            connectorText = String.format(CONNECTOR_TEXT_FORMAT, "${price.prevQuantityConnector?.toFormattedString()} ${price.prevUnitName}", price.unitName)
            root.setOnClickListener { onItemClickListener?.invoke(priceAndSubPrice, position) }
        }
        if (price.prevQuantityConnector != null){
            binding.mcvPriceMainPriceIndicator.visibility = View.INVISIBLE
        }else{
            binding.mcvPriceMainPriceIndicator.visibility = View.GONE
        }

        if (!merchantSubPrice.isEnabled){
            binding.tvPriceMerchant.apply {
                setTextColor(resources.getColor(R.color.black_60))
                textSize = 12f
                typeface = ResourcesCompat.getFont(context, R.font.karla_regular)
            }
            binding.merchantPrice =  context.getString(R.string.non_active)
        }else{
            binding.merchantPrice = context.getString(R.string.rp_, ThousandFormatter.format(merchantSubPrice.price.toLong()))
        }

        if (!consumerSubPrice.isEnabled){
            binding.tvPriceConsumer.apply {
                setTextColor(resources.getColor(R.color.black_60))
                textSize = 12f
                typeface = ResourcesCompat.getFont(context, R.font.karla_regular)
            }
            binding.consumerPrice =  context.getString(R.string.non_active)
        }else{
            binding.consumerPrice = context.getString(R.string.rp_, ThousandFormatter.format(consumerSubPrice.price.toLong()))
        }

        val rootParams = binding.root.layoutParams as RecyclerView.LayoutParams
        if (position == 0){
            rootParams.topMargin = 16.dp(context)
        }
        if (position == itemCount-1){
            rootParams.bottomMargin = 32.dp(context)
        }
        binding.root.apply {
            layoutParams = rootParams
            requestLayout()
        }

        // Binding Special Price
        val specialPriceList = mutableListOf<SpecialPriceWithIcon>()
        merchantSpecialPrice.forEach { specialPriceList.add(SpecialPriceWithIcon(it, SubPriceIconType.Merchant())) }
        consumerSpecialPrice.forEach { specialPriceList.add(SpecialPriceWithIcon(it, SubPriceIconType.Consumer())) }
        val specialPriceAdapter = SpecialPriceWithIconAdapter(specialPriceList, price.unitName)
        binding.rvPriceSpecialPrice.adapter = specialPriceAdapter
        collapseAccordion(binding.rvPriceSpecialPrice, binding.ivPriceSpecialPriceArrow)
        binding.cbPriceSpecialPriceAccordian.setOnCheckedChangeListener { _, b ->
            if (b){
                expandAccordion(binding.rvPriceSpecialPrice, binding.ivPriceSpecialPriceArrow)
            }else{
                collapseAccordion(binding.rvPriceSpecialPrice, binding.ivPriceSpecialPriceArrow)
            }
        }
    }

    override fun getItemCount() = priceAndSubPriceList.size

    fun addPriceAndSubPrice(priceAndSubPrice: PriceAndSubPrice){
        priceAndSubPriceList.add(priceAndSubPrice)
        notifyItemInserted(priceAndSubPriceList.lastIndex)
    }

    fun updatePriceAndSubPrice(priceAndSubPrice: PriceAndSubPrice, position: Int){
        priceAndSubPriceList[position] = priceAndSubPrice
        if (position != itemCount-1){
            val nextPriceAndSubPrice = priceAndSubPriceList[position+1]
            nextPriceAndSubPrice.price.apply {
                prevUnitName = priceAndSubPrice.unit?.name
            }
        }
        if (position != 0){
            val prevPriceAndSubPrice = priceAndSubPriceList[position-1]
            prevPriceAndSubPrice.price.apply {
                nextQuantityConnector = priceAndSubPrice.price.prevQuantityConnector
            }
        }
        notifyItemRangeChanged(position-1, 3)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deletePriceAndSubPrice(position: Int){
        priceAndSubPriceList.removeAt(position)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: MutableList<PriceAndSubPrice>){
        priceAndSubPriceList = list
        notifyDataSetChanged()
    }

    fun getPriceAndSubPriceAt(position: Int) = priceAndSubPriceList[position]

}