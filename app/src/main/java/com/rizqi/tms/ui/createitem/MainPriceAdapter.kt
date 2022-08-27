package com.rizqi.tms.ui.createitem

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ItemPriceBinding
import com.rizqi.tms.model.*
import com.rizqi.tms.utility.ThousandFormatter
import com.rizqi.tms.utility.dp

class MainPriceAdapter(
    private var priceAndSubPriceList: MutableList<PriceAndSubPrice>
) : RecyclerView.Adapter<MainPriceAdapter.MainPriceViewHolder>(){
    private var viewHolderList = mutableListOf<MainPriceViewHolder>()

    class MainPriceViewHolder(val binding : ItemPriceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainPriceViewHolder {
        return MainPriceViewHolder(ItemPriceBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MainPriceViewHolder, position: Int) {
        if (holder !in viewHolderList) viewHolderList.add(holder)
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
            isConnectorVisible = price.quantityConnector != null
            barcode = price.barcode
            unitName = price.unitName
            connectorText = "${price.quantityConnector} ${price.prevUnitName} = 1 ${price.unitName}"
            root.setOnClickListener { toggleMainPrice(position) }
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

    private fun toggleMainPrice(position: Int){
        viewHolderList.forEachIndexed { index, holder ->
            val priceAndSubPrice = priceAndSubPriceList[index]
            if (index == position) {
                setMainPriceTheme(
                    priceAndSubPrice.merchantSubPrice.subPrice,
                    priceAndSubPrice.consumerSubPrice.subPrice,
                    holder
                )
                priceAndSubPrice.price.isMainPrice = true
            }
            else {
                setNonMainPriceTheme(
                    priceAndSubPrice.merchantSubPrice.subPrice,
                    priceAndSubPrice.consumerSubPrice.subPrice,
                    holder
                )
                priceAndSubPrice.price.isMainPrice = false
            }
        }
    }

    fun getResultList() = priceAndSubPriceList

    private fun setMainPriceTheme(merchantSubPrice : SubPrice, consumerSubPrice: SubPrice, holder: MainPriceViewHolder){
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

    private fun setNonMainPriceTheme(merchantSubPrice : SubPrice, consumerSubPrice: SubPrice, holder: MainPriceViewHolder){
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

    private fun expandAccordion(v: View, arrow : View) {
        val matchParentMeasureSpec: Int = View.MeasureSpec.makeMeasureSpec(
            (v.parent as View).width,
            View.MeasureSpec.EXACTLY
        )
        val wrapContentMeasureSpec: Int =
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        val targetHeight: Int = v.measuredHeight

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.layoutParams.height = 0
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                v.layoutParams.height =
                    if (interpolatedTime == 1f) ConstraintLayout.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
                if (interpolatedTime > 0.1f) v.visibility = View.VISIBLE
                v.requestLayout()
            }
            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Expansion speed of 1dp/ms
        a.duration = (targetHeight / v.context.resources.displayMetrics.density).toLong()
        v.startAnimation(a)
        ValueAnimator.ofFloat(-180f, 0f).apply {
            duration = a.duration
            addUpdateListener {
                arrow.rotation = it.animatedValue as Float
            }
            start()
        }
    }

    private fun collapseAccordion(v: View, arrow : View) {
        val initialHeight: Int = v.measuredHeight
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Collapse speed of 1dp/ms
        a.duration = (initialHeight / v.context.resources.displayMetrics.density).toLong()
        v.startAnimation(a)
        ValueAnimator.ofFloat(0f, -180f).apply {
            duration = a.duration
            addUpdateListener {
                arrow.rotation = it.animatedValue as Float
            }
            start()
        }
    }

}