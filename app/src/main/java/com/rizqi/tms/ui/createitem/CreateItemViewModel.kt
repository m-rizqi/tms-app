package com.rizqi.tms.ui.createitem

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rizqi.tms.R
import com.rizqi.tms.model.Price
import com.rizqi.tms.model.PriceAndSubPrice
import com.rizqi.tms.utility.Message

class CreateItemViewModel : ViewModel() {
    private val _prices = MutableLiveData(mutableListOf<PriceAndSubPrice>())

    private val _image = MutableLiveData<Bitmap?>()
    val image : LiveData<Bitmap?>
        get() = _image

    private val _name = MutableLiveData("")
    val name : LiveData<String>
        get() = _name

    fun setImage(value : Bitmap){
        _image.value = value
    }

    fun setName(value : String){
        _name.value = value
    }

    fun setPrices(value : MutableList<PriceAndSubPrice>){
        _prices.value = value
    }

    fun validateStep1(context: Context) = Step1CreateItemFragment.Step1CreateItemValidation(
        if (_name.value.isNullOrBlank()){
            Message.StringResource(R.string.field_must_be_filled, context.getString(R.string.item_name))
        }else{
            null
        }
    )

}
