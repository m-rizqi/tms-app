package com.rizqi.tms.utility

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.rizqi.tms.R
import com.rizqi.tms.databinding.LayoutLoadingBinding

private fun disableScreen(appCompatActivity: AppCompatActivity){
    appCompatActivity.window.setFlags(
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
}

private fun enableScreen(appCompatActivity: AppCompatActivity){
    appCompatActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
}

fun AppCompatActivity.showLoading(layout: LayoutLoadingBinding){
    disableScreen(this)
    layout.root.visibility = View.VISIBLE
}

fun AppCompatActivity.hideLoading(layout: LayoutLoadingBinding){
    enableScreen(this)
    layout.root.visibility - View.GONE
}

fun encodeAndSaveBitmap(context : Context, bitmap : Bitmap?) {
    val stream =context.openFileOutput(BITMAP_JPG, Context.MODE_PRIVATE)
    bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    stream.close()
    bitmap?.recycle()
}

fun getAndDecodeBitmap(context: Context?): Bitmap? {
    val stream = context?.openFileInput(BITMAP_JPG)
    val bitmap = BitmapFactory.decodeStream(stream)
    stream?.close()
    return bitmap
}

fun Chip.setOnCheckedListener(onCheckedListener : (CompoundButton, Boolean) -> Unit){
    val resources = this.resources
    this.setOnClickListener {
        val b = !this.isChecked
        val chip = it as Chip
        chip.isChecked = b
        if (b){
            chip.apply {
                chipBackgroundColor = ColorStateList.valueOf(resources.getColor(R.color.primary_20))
                chipStrokeColor = ColorStateList.valueOf(resources.getColor(R.color.primary_100))
                setTextColor(resources.getColor(R.color.black_100))
            }
        }else{
            chip.apply {
                chipBackgroundColor = ColorStateList.valueOf(resources.getColor(R.color.black_10))
                chipStrokeColor = ColorStateList.valueOf(resources.getColor(R.color.black_20))
                setTextColor(resources.getColor(R.color.black_80))
            }
        }
        onCheckedListener(chip, b)
    }
}

fun Context.createChipView(unit: com.rizqi.tms.model.Unit, onCheckedListener: (CompoundButton, Boolean) -> Unit){
    val chip = Chip(this)

}
