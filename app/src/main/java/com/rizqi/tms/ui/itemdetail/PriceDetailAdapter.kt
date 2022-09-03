package com.rizqi.tms.ui.itemdetail

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
import com.rizqi.tms.model.SubPrice
import com.rizqi.tms.model.SubPriceIconType
import com.rizqi.tms.ui.createitem.SpecialPriceWithIconAdapter
import com.rizqi.tms.utility.*
import com.rizqi.tms.utility.dp

class PriceDetailAdapter(
    private var priceAndSubPriceList: MutableList<PriceAndSubPrice>
) : RecyclerView.Adapter<PriceDetailAdapter.PriceDetailViewHolder>(){
    var onItemClickListener : ((PriceAndSubPrice, Int) -> Unit)? = null

    class PriceDetailViewHolder(val binding : ItemPriceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PriceDetailViewHolder {
        return PriceDetailViewHolder(ItemPriceBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: PriceDetailViewHolder, position: Int) {
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
            root.setOnClickListener {
                onItemClickListener?.invoke(priceAndSubPrice, position)
            }
            isConnectorVisible = price.prevQuantityConnector != null
            barcode = price.barcode
            unitName = price.unitName
            connectorText = String.format(CONNECTOR_TEXT_FORMAT, "${price.prevQuantityConnector?.toFormattedString()} ${price.prevUnitName}", price.unitName)
        }
        if (price.isMainPrice){
            setMainPriceTheme(merchantSubPrice, consumerSubPrice, holder)
        }else{
            setNonMainPriceTheme(merchantSubPrice, consumerSubPrice, holder)
        }

        val rootParams = binding.root.layoutParams as RecyclerView.LayoutParams
        if (position == 0){
            rootParams.topMargin = 16.dp(context)
        }
        if (position == priceAndSubPriceList.lastIndex){
            rootParams.bottomMargin = 80.dp(context)
        }else {
            rootParams.bottomMargin = 16.dp(context)
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

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: MutableList<PriceAndSubPrice>){
        priceAndSubPriceList = list
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updatePrice(priceAndSubPrice: PriceAndSubPrice, position: Int){
        if (priceAndSubPrice.price.isMainPrice){
            priceAndSubPriceList.forEachIndexed { index, price ->
                if (index != position) price.price.isMainPrice = false
            }
        }
        priceAndSubPriceList[position] = priceAndSubPrice
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addPrice(priceAndSubPrice: PriceAndSubPrice){
        if (priceAndSubPrice.price.isMainPrice){
            priceAndSubPriceList.forEach {
                it.price.isMainPrice = false
            }
        }
        priceAndSubPriceList.add(priceAndSubPrice)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deletePrice(isMainPrice : Boolean, position: Int){
        if (position < itemCount - 1){
            val posPrice = getPriceAt(position)
            val nextPrice = getPriceAt(position + 1)
            nextPrice.price.apply {
                prevQuantityConnector = posPrice.price.prevQuantityConnector
                prevUnitName = posPrice.price.prevUnitName
            }
        }
        if (isMainPrice){
            priceAndSubPriceList.withIndex().firstOrNull { it.index != position }?.value?.let {
                it.price.isMainPrice = true
            }
        }
        priceAndSubPriceList.removeAt(position)
        notifyDataSetChanged()
    }

    fun getPriceAt(index : Int) = priceAndSubPriceList[index]

    fun getList() = priceAndSubPriceList

    private fun setMainPriceTheme(merchantSubPrice : SubPrice, consumerSubPrice: SubPrice, holder: PriceDetailViewHolder){
        val binding = holder.binding
        val resources = binding.root.resources
        binding.mcvPriceRoot.setCardBackgroundColor(resources.getColor(R.color.track_color))
        binding.tilPriceBarcode.boxBackgroundColor = resources.getColor(R.color.primary_15)
        binding.mcvPriceMainPriceIndicator.visibility = View.VISIBLE

        if (!merchantSubPrice.isEnabled){
            binding.tvPriceMerchant.apply {
                setTextColor(resources.getColor(R.color.black_80))
                textSize = 12f
                typeface = ResourcesCompat.getFont(context, R.font.karla_regular)
            }
            binding.merchantPrice =  resources.getString(R.string.non_active)
        }else{
            binding.tvPriceMerchant.apply {
                setTextColor(resources.getColor(R.color.black_100))
                textSize = 16f
                typeface = ResourcesCompat.getFont(context, R.font.karla_regular)
            }
            binding.merchantPrice = resources.getString(R.string.rp_, ThousandFormatter.format(merchantSubPrice.price.toLong()))
        }

        if (!consumerSubPrice.isEnabled){
            binding.tvPriceConsumer.apply {
                setTextColor(resources.getColor(R.color.black_80))
                textSize = 12f
                typeface = ResourcesCompat.getFont(context, R.font.karla_regular)
            }
            binding.consumerPrice =  resources.getString(R.string.non_active)
        }else{
            binding.tvPriceConsumer.apply {
                setTextColor(resources.getColor(R.color.black_100))
                textSize = 16f
                typeface = ResourcesCompat.getFont(context, R.font.karla_regular)
            }
            binding.consumerPrice = resources.getString(R.string.rp_, ThousandFormatter.format(consumerSubPrice.price.toLong()))
        }
    }

    private fun setNonMainPriceTheme(merchantSubPrice : SubPrice, consumerSubPrice: SubPrice, holder: PriceDetailViewHolder){
        val binding = holder.binding
        val resources = binding.root.resources
        binding.mcvPriceRoot.setCardBackgroundColor(resources.getColor(R.color.white))
        binding.tilPriceBarcode.boxBackgroundColor = resources.getColor(R.color.disabled)
        binding.mcvPriceMainPriceIndicator.visibility = View.INVISIBLE

        if (!merchantSubPrice.isEnabled){
            binding.tvPriceMerchant.apply {
                setTextColor(resources.getColor(R.color.black_60))
                textSize = 12f
                typeface = ResourcesCompat.getFont(context, R.font.karla_regular)
            }
            binding.merchantPrice =  resources.getString(R.string.non_active)
        }else{
            binding.tvPriceMerchant.apply {
                setTextColor(resources.getColor(R.color.black_100))
                textSize = 16f
                typeface = ResourcesCompat.getFont(context, R.font.karla_regular)
            }
            binding.merchantPrice = resources.getString(R.string.rp_, ThousandFormatter.format(merchantSubPrice.price.toLong()))
        }

        if (!consumerSubPrice.isEnabled){
            binding.tvPriceConsumer.apply {
                setTextColor(resources.getColor(R.color.black_60))
                textSize = 12f
                typeface = ResourcesCompat.getFont(context, R.font.karla_regular)
            }
            binding.consumerPrice =  resources.getString(R.string.non_active)
        }else{
            binding.tvPriceConsumer.apply {
                setTextColor(resources.getColor(R.color.black_100))
                textSize = 16f
                typeface = ResourcesCompat.getFont(context, R.font.karla_regular)
            }
            binding.consumerPrice = resources.getString(R.string.rp_, ThousandFormatter.format(consumerSubPrice.price.toLong()))
        }
    }

}