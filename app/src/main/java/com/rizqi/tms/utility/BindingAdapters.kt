package com.rizqi.tms.utility

import android.annotation.SuppressLint
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@SuppressLint("UseCompatLoadingForDrawables")
@BindingAdapter(
    "android:src"
)
fun setImageView(
    imageView: ImageView, drawableId : Int
){
    imageView.setImageDrawable(
        imageView.context.getDrawable(drawableId)
    )
}

@BindingAdapter(
    "errorText"
)
fun setErrorTextInputLayout(
    inputLayout: TextInputLayout, errorText : String?
){
    inputLayout.error = errorText
}