package com.rizqi.tms.utility

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ChipUnitBinding
import com.rizqi.tms.databinding.LayoutLoadingBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream


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
    layout.root.visibility = View.GONE
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

fun Context.saveBitmapToFolder(bitmap: Bitmap?) : String?{
    val imageFile = getOutputMediaFile(this)
    try {
        val fos = FileOutputStream(imageFile)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.close()
    }catch (e : Exception){
        e.printStackTrace()
    }
    return imageFile?.path
}

fun Context.getBitmapFromPath(path: String): Bitmap? {
    val bmOptions = BitmapFactory.Options()
    return BitmapFactory.decodeFile(path, bmOptions)
}

private fun getOutputMediaFile(context: Context): File? {
    val mediaStorageDir = File(
        context.getExternalFilesDir(null)
            .toString() + ANDROID_DATA
                + context.applicationContext.packageName
                + FILES
    )

    if (!mediaStorageDir.exists()) {
        if (!mediaStorageDir.mkdirs()) {
            return null
        }
    }
    // Create a media file name
    val mediaFile: File
    val mImageName = String.format(IMAGE_NAME_FORMAT, System.currentTimeMillis())
    mediaFile = File(mediaStorageDir.path + File.separator + mImageName)
    return mediaFile
}

fun deleteFile(path: String): Boolean {
    return try {
        val file = File(path)
        file.delete()
    }catch (e : Exception){
        false
    }
}

fun expandAccordion(v: View, arrow : View?) {
    val matchParentMeasureSpec: Int = View.MeasureSpec.makeMeasureSpec(
        (v.parent as View).width,
        View.MeasureSpec.EXACTLY
    )
    val wrapContentMeasureSpec: Int =
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
    val targetHeight: Int = v.measuredHeight

    // Older versions of android (pre API 21) cancel animations for views with a height of 0.
    v.layoutParams.height = 0
    val a: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            v.layoutParams.height =
                if (interpolatedTime == 1f) ConstraintLayout.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
            if (interpolatedTime > 0.1f) v.visibility = View.VISIBLE
            v.requestLayout()
        }
        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    // Expansion speed of 1dp/ms
    a.duration = (targetHeight / v.context.resources.displayMetrics.density).toLong()
    v.startAnimation(a)
    ValueAnimator.ofFloat(-180f, 0f).apply {
        duration = a.duration
        addUpdateListener {
            arrow?.rotation = it.animatedValue as Float
        }
        start()
    }
}

fun collapseAccordion(v: View, arrow : View?) {
    val initialHeight: Int = v.measuredHeight
    val a: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            if (interpolatedTime == 1f) {
                v.visibility = View.GONE
            } else {
                v.layoutParams.height =
                    initialHeight - (initialHeight * interpolatedTime).toInt()
                v.requestLayout()
            }
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    // Collapse speed of 1dp/ms
    a.duration = (initialHeight / v.context.resources.displayMetrics.density).toLong()
    v.startAnimation(a)
    ValueAnimator.ofFloat(0f, -180f).apply {
        duration = a.duration
        addUpdateListener {
            arrow?.rotation = it.animatedValue as Float
        }
        start()
    }
}

fun Double.toFormattedString() : String{
    val mantissa = this - this.toInt()
    if (mantissa == 0.0) return this.toInt().toString()
    return this.toString()
}

fun setChipStyle(chip: Chip, isChecked : Boolean){
    if (isChecked) setCheckedChip(chip)
    else setUnCheckedChip(chip)
}

private fun setCheckedChip(chip: Chip){
    val resources = chip.resources
    chip.apply {
        chipBackgroundColor = ColorStateList.valueOf(resources.getColor(R.color.primary_20))
        chipStrokeColor = ColorStateList.valueOf(resources.getColor(R.color.primary_100))
        setTextColor(resources.getColor(R.color.black_100))
        isChecked = true
    }
}

private fun setUnCheckedChip(chip: Chip){
    val resources = chip.resources
    chip.apply {
        chipBackgroundColor = ColorStateList.valueOf(resources.getColor(R.color.black_10))
        chipStrokeColor = ColorStateList.valueOf(resources.getColor(R.color.black_20))
        setTextColor(resources.getColor(R.color.black_80))
        isChecked = false
    }
}

fun insertUnitIntoChipGroup(
    layoutInflater : LayoutInflater,
    unitList: List<com.rizqi.tms.model.Unit>,
    checkedUnits: List<com.rizqi.tms.model.Unit>?,
    chipGroup: ChipGroup,
    onCheckedChangedListener : (Boolean, com.rizqi.tms.model.Unit) -> Unit
){
    chipGroup.removeAllViews()
    unitList.forEach {
        val chip = ChipUnitBinding.inflate(layoutInflater)
        chip.chip.text = it.name
        chip.chip.setOnCheckedChangeListener { _, b ->
            setChipStyle(chip.chip, b)
            onCheckedChangedListener(b, it)
        }
        chip.chip.isChecked = it.id in (checkedUnits?.map { u -> u.id } ?: mutableListOf())
        chipGroup.addView(chip.root)
    }
}
