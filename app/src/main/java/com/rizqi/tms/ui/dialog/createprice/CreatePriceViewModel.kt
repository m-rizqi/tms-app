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
    private val _prevUnit = MutableLiveData<Unit?>()
    private val _merchantPrice = MutableLiveData(0.0)
    private val _consumerPrice = MutableLiveData(0.0)
    private val _isMerchantEnabled = MutableLiveData(true)
    private val _isConsumerEnabled = MutableLiveData(true)
    private val _updatePriceAndSubPrice = MutableLiveData<PriceAndSubPrice?>(null)

    val unit : LiveData<Unit?>
        get() = _unit

    val isMerchantEnabled : LiveData<Boolean>
        get() = _isMerchantEnabled

    val isConsumerEnabled : LiveData<Boolean>
        get() = _isConsumerEnabled

    val connectorText : LiveData<String>
        get() = _connectorText

    fun setQuantityConnector(value : String){
        try {
            _quantityConnector.value = value.toDouble()
        }catch (e : Exception){}
    }

    private fun setConnectorText(){
        _connectorText.value = String.format(CONNECTOR_TEXT_FORMAT, _prevUnit.value?.name, _unit.value?.name)
    }

    fun setPrevUnit(value: Unit?){
        _prevUnit.value = value
        setConnectorText()
    }

    fun setBarcode(value : String){
        _barcode.value = value
    }

    fun setUnit(value : Unit){
        _unit.value = value
        setConnectorText()
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

    fun toggleMerchantEnabled(){
        _isMerchantEnabled.value = !_isMerchantEnabled.value!!
        if (!_isMerchantEnabled.value!!) _merchantPrice.value = -1.0 else _merchantPrice.value = 0.0
    }

    fun toggleConsumerEnabled(){
        _isConsumerEnabled.value = !_isConsumerEnabled.value!!
        if (!_isConsumerEnabled.value!!) _consumerPrice.value = -1.0 else _consumerPrice.value = 0.0
    }

    fun setUpdatePriceAndSubPrice(value: PriceAndSubPrice){
        _updatePriceAndSubPrice.value = value
        value.unit?.let { setUnit(it) }
        value.price.quantityConnector?.let { setQuantityConnector(it.toString()) }
        setBarcode(value.price.barcode)
        setMerchantPrice(value.merchantSubPrice.subPrice.price.toString())
        setConsumerPrice(value.consumerSubPrice.subPrice.price.toString())
        if (!value.merchantSubPrice.subPrice.isEnabled) toggleMerchantEnabled()
        if (!value.consumerSubPrice.subPrice.isEnabled) toggleConsumerEnabled()

    }

    fun validate(isUsingConnector : Boolean, context : Context) = CreatePriceValidation(
        if (!isUsingConnector || (_quantityConnector.value != null && _quantityConnector.value != 0.0)) null else
            Message.StringResource(R.string.field_must_be_filled, context.getString(R.string.connector_between_price)),
        if (_barcode.value?.isBlank() == false) null else Message.StringResource(R.string.barcode_cannot_empty_press_the_icon),
        if (_unit.value != null) null else Message.StringResource(R.string.no_unit_yet_click_add_unit),
        if (!_isMerchantEnabled.value!! || _merchantPrice.value != 0.0) null else
            Message.StringResource(R.string.field_must_be_filled, context.getString(R.string.merchant_price)),
        if (!_isConsumerEnabled.value!! || _consumerPrice.value != 0.0) null else
            Message.StringResource(R.string.field_must_be_filled, context.getString(R.string.consumer_price))
    )

    fun getPriceAndSubPrice(merchantSpecialPriceList : List<SpecialPrice>, consumerSpecialPriceList: List<SpecialPrice>): PriceAndSubPrice {
        val merchantSubPrice = SubPrice(_merchantPrice.value!!, _isMerchantEnabled.value!!)
        val consumerSubPrice = SubPrice(_consumerPrice.value!!, _isConsumerEnabled.value!!)
        val merchantSubPriceWithSpecialPrice = SubPriceWithSpecialPrice(merchantSubPrice, merchantSpecialPriceList)
        val consumerSubPriceWithSpecialPrice = SubPriceWithSpecialPrice(consumerSubPrice, consumerSpecialPriceList)
        val price = Price(
            barcode = _barcode.value!!,
            quantityConnector = _quantityConnector.value,
            unitId = _unit.value!!.id,
            unitName = _unit.value!!.name,
            prevUnitName = _prevUnit.value?.name
        )
        return PriceAndSubPrice(
            price,
            merchantSubPriceWithSpecialPrice,
            consumerSubPriceWithSpecialPrice
        ).apply {
            unit = _unit.value
        }
    }

    fun getUpdatedPriceAndSubPrice(merchantSpecialPriceList : List<SpecialPrice>, consumerSpecialPriceList: List<SpecialPrice>): PriceAndSubPrice {
        return _updatePriceAndSubPrice.value!!.apply {
            price.apply {
                barcode = _barcode.value!!
                quantityConnector = _quantityConnector.value
                unitId = _unit.value!!.id
                unitName = _unit.value!!.name
                prevUnitName = _prevUnit.value?.name
            }
            merchantSubPrice.subPrice.apply {
                price = _merchantPrice.value!!
                isEnabled = _isMerchantEnabled.value!!
            }
            consumerSubPrice.subPrice.apply {
                price = _consumerPrice.value!!
                isEnabled = _isConsumerEnabled.value!!
            }
            merchantSubPrice.specialPrices = merchantSpecialPriceList
            consumerSubPrice.specialPrices = consumerSpecialPriceList
        }
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