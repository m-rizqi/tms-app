package com.rizqi.tms.utility

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Build
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputLayout
import com.rizqi.tms.R

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

@BindingAdapter(
    "htmlText"
)
fun setTextViewHtml(
    textView: TextView, htmlText : String
){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        textView.text = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY)
    }else {
        textView.text = Html.fromHtml(htmlText)
    }
}

@BindingAdapter(
    "toggleEnabledButton"
)
fun setToggleEnabledButton(
    button: MaterialButton, isEnabled : Boolean
){
    val resources = button.resources
    button.isEnabled = isEnabled
    if (isEnabled) {
        button.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.primary_100))
        button.setTextColor(resources.getColor(R.color.white))
    }else {
        button.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.disabled))
        button.setTextColor(resources.getColor(R.color.black_60))
    }
}

@BindingAdapter(
    "chipState"
)
fun setChipState(
    chip: Chip, value : String
){
    val resources = chip.resources
    chip.chipBackgroundColor = ColorStateList(
        arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-(android.R.attr.state_checked))
        ),
        intArrayOf(
            resources.getColor(R.color.primary_20),
            resources.getColor(R.color.black_10)
        )
    )
    chip.chipStrokeColor = ColorStateList(
        arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-(android.R.attr.state_checked))
        ),
        intArrayOf(
            resources.getColor(R.color.primary_100),
            resources.getColor(R.color.black_20)
        )
    )
    chip.setTextColor(
        ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-(android.R.attr.state_checked))
            ),
            intArrayOf(
                resources.getColor(R.color.black_100),
                resources.getColor(R.color.black_80)
            )
        )
    )
}