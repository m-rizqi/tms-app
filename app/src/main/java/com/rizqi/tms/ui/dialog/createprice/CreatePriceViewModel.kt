package com.rizqi.tms.ui.dialog.createprice

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rizqi.tms.R
import com.rizqi.tms.model.*
import com.rizqi.tms.model.Unit
import com.rizqi.tms.utility.CONNECTOR_TEXT_FORMAT
import com.rizqi.tms.utility.Message

class CreatePriceViewModel : ViewModel(){
    private val _quantityConnector = MutableLiveData<Double?>(null)
    private val _connectorText = MutableLiveData("")
    private val _barcode = MutableLiveData("")
    private val _unit = MutableLiveData<Unit?>()
    private val _merchantPrice = MutableLiveData(0.0)
    private val _consumerPrice = MutableLiveData(0.0)

    val unit : LiveData<Unit?>
        get() = _unit

    fun setQuantityConnector(value : String){
        try {
            _quantityConnector.value = value.toDouble()
        }catch (e : Exception){}
    }

    fun setConnectorText(prevUnit : Unit, currentUnit: Unit){
        _connectorText.value = String.format(CONNECTOR_TEXT_FORMAT, prevUnit.name, currentUnit.name)
    }

    fun setBarcode(value : String){
        _barcode.value = value
    }

    fun setUnit(value : Unit){
        _unit.value = value
    }

    fun setMerchantPrice(value : String){
        try {
            _merchantPrice.value = value.replace(".","").replace(",",".").toDouble()
        }catch (e : Exception){}
    }

    fun setConsumerPrice(value : String){
        try {
            _consumerPrice.value = value.replace(".","").replace(",",".").toDouble()
        }catch (e : Exception){}
    }

    fun validate(isUsingConnector : Boolean, context : Context) = CreatePriceValidation(
        if (isUsingConnector || _quantityConnector.value != null || _quantityConnector.value != 0.0) null else
            Message.StringResource(R.string.field_must_be_filled, context.getString(R.string.connector_between_price)),
        if (_barcode.value?.isBlank() == false) null else Message.StringResource(R.string.barcode_cannot_empty_press_the_icon),
        if (_unit.value != null) null else Message.StringResource(R.string.no_unit_yet_click_add_unit),
        if (_merchantPrice.value != 0.0) null else
            Message.StringResource(R.string.field_must_be_filled, context.getString(R.string.merchant_price)),
        if (_consumerPrice.value != 0.0) null else
            Message.StringResource(R.string.field_must_be_filled, context.getString(R.string.consumer_price))
    )

    fun getPriceAndSubPrice(merchantSpecialPriceList : List<SpecialPrice>, consumerSpecialPriceList: List<SpecialPrice>): PriceAndSubPrice {
        val merchantSubPrice = SubPrice(_merchantPrice.value!!)
        val consumerSubPrice = SubPrice(_consumerPrice.value!!)
        val merchantSubPriceWithSpecialPrice = SubPriceWithSpecialPrice(merchantSubPrice, merchantSpecialPriceList)
        val consumerSubPriceWithSpecialPrice = SubPriceWithSpecialPrice(consumerSubPrice, consumerSpecialPriceList)
        val price = Price(
            barcode = _barcode.value!!,
            quantityConnector = _quantityConnector.value,
            unitId = _unit.value!!.id,
            unitName = _unit.value!!.name
        )
        return PriceAndSubPrice(
            price,
            merchantSubPriceWithSpecialPrice,
            consumerSubPriceWithSpecialPrice
        )
    }

    class CreatePriceValidation(
        val connectorMessage: Message?,
        val barcodeMessage: Message?,
        val unitMessage: Message?,
        val merchantMessage: Message?,
        val consumerMessage: Message?
    ){
        val isAllValid = connectorMessage == null && barcodeMessage == null && unitMessage == null
                && merchantMessage == null && consumerMessage == null
    }

}