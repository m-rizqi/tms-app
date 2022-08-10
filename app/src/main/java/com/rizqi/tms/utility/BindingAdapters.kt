package com.rizqi.tms.utility

import android.annotation.SuppressLint
import android.widget.ImageView
import androidx.databinding.BindingAdapter

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