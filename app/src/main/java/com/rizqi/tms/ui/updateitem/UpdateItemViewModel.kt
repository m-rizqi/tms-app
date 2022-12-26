package com.rizqi.tms.ui.updateitem

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rizqi.tms.model.Item
import com.rizqi.tms.model.ItemWithPrices
import com.rizqi.tms.model.PriceAndSubPrice

class UpdateItemViewModel : ViewModel() {
    private val _name = MutableLiveData("")
    private val _imageBitmap = MutableLiveData<Bitmap?>(null)
    private val _isReminded = MutableLiveData(false)

    val name : LiveData<String>
        get() = _name
    val imageBitmap : LiveData<Bitmap?>
        get() = _imageBitmap
    val isReminded : LiveData<Boolean>
        get() = _isReminded

    fun setName(value : String){
        _name.value = value
    }
    fun setImageBitmap(value: Bitmap){
        _imageBitmap.value = value
    }
    fun setReminded(value : Boolean){
        _isReminded.value = value
    }
}