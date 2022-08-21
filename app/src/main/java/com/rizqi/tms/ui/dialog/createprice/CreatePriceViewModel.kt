package com.rizqi.tms.ui.dialog.createprice

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rizqi.tms.model.Unit
import com.rizqi.tms.utility.CONNECTOR_TEXT_FORMAT

class CreatePriceViewModel : ViewModel(){
    private val _quantityConnector = MutableLiveData<Double?>(null)
    private val _connectorText = MutableLiveData("")
    private val _barcode = MutableLiveData("")
    private val _unit = MutableLiveData<Unit>()
    private val _merchantPrice = MutableLiveData(0.0)
    private val _consumerPrice = MutableLiveData(0.0)

    fun setQuantityConnector(value : String){
        try {
            _quantityConnector.value = value.toDouble()
        }catch (e : Exception){}
    }

    fun setConnectorText(prevUnit : Unit, nextUnit: Unit){
        _connectorText.value = String.format(CONNECTOR_TEXT_FORMAT, nextUnit.name, prevUnit.name)
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
}