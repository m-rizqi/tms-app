package com.rizqi.tms.ui.updateitem

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UpdateItemViewModel : ViewModel() {
    private val _name = MutableLiveData("")
    private val _imageBitmap = MutableLiveData<Bitmap?>(null)
    private val _isReminded = MutableLiveData(false)

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