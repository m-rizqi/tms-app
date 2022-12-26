package com.rizqi.tms.utility

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.Transformation
import android.view.inputmethod.InputMethodManager
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ChipUnitBinding
import com.rizqi.tms.databinding.LayoutLoadingBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.Calendar


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

fun Context.saveBitmapToFolder(bitmap: Bitmap?, imageName : String? = null) : String?{
    val mImageName = imageName ?: String.format(IMAGE_NAME_FORMAT, System.currentTimeMillis())
    val imageFile = getOutputMediaFile(this, mImageName)
//    if (imageFile == null || !imageFile.exists()){
//        imageFile?.mkdir()
//    }
    try {
        val fos = FileOutputStream(imageFile)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.close()
    }catch (e : Exception){
        e.printStackTrace()
        return null
    }
    return mImageName
}

fun Context.getBitmapFromPath(path: String): Bitmap? {
    val bmOptions = BitmapFactory.Options()
    return BitmapFactory.decodeFile(getOutputMediaFile(this, path)?.path, bmOptions)
}

private fun getOutputMediaFile(context: Context, path: String): File? {
    val mediaStorageDir = File(
        context.getExternalFilesDir(null).toString()
//                + ANDROID_DATA
//                + context.applicationContext.packageName
//                + FILES
    )

    if (!mediaStorageDir.exists()) {
        if (!mediaStorageDir.mkdirs()) {
            return null
        }
    }
    // Create a media file name
    return File(mediaStorageDir.path + File.separator, path)
}

fun deleteFile(context: Context,path: String): Boolean {
    return try {
        val file = getOutputMediaFile(context, path)
        file?.delete() ?: false
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

fun Context.getInitialBitmap(name : String, height : Float = 72f.dp(this), width : Float = 72f.dp(this), fontSize : Float = 29f.dp(this)) : Bitmap {
    var initials = ""
    name.uppercase().split(" ").forEach {
        if (initials.length < 2 && it.isNotEmpty()){
            initials += it[0]
        }
    }
    val textPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        textSize = fontSize
        color = Color.WHITE
        typeface = ResourcesCompat.getFont(this@getInitialBitmap, R.font.rubik_regular)
    }
    val circlePaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = ResourcesCompat.getColor(resources, R.color.primary_100, null)
    }
    val b = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
    val c = Canvas(b)
    c.drawColor(Color.WHITE)
    c.drawCircle(width/2, height/2, width/2, circlePaint)
    val textBound = Rect()
    textPaint.getTextBounds(initials, 0, initials.length, textBound)
    c.drawText(initials, width/2 - (textPaint.measureText(initials) / 2), height/2 + textBound.height()/2, textPaint)
    return b
}

fun Context.getInitialPlaceholder(name : String, height : Float = 72f.dp(this), width : Float = 72f.dp(this), fontSize : Float = 29f.dp(this)) : Drawable {
    return BitmapDrawable(resources, getInitialBitmap(name, height, width, fontSize))
}

@Throws(IOException::class)
fun copyFile(fromFile: FileInputStream, toFile: FileOutputStream) {
    var fromChannel: FileChannel? = null
    var toChannel: FileChannel? = null
    try {
        fromChannel = fromFile.getChannel()
        toChannel = toFile.channel
        fromChannel.transferTo(0, fromChannel.size(), toChannel)
    } finally {
        try {
            if (fromChannel != null) {
                fromChannel.close()
            }
        } finally {
            if (toChannel != null) {
                toChannel.close()
            }
        }
    }
}

fun hideKeyboard(view: View?){
    if (view != null) {
        val manager: InputMethodManager? = view.context.getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager?
        manager?.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }
}

@SuppressLint("SimpleDateFormat")
fun getFormattedDateString(timeInMillis : Long, format : String = EEE_DD_MMM_YYYY): String? {
    val cal = Calendar.getInstance()
    cal.timeInMillis = timeInMillis
    val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        SimpleDateFormat(format)
    } else {
        SimpleDateFormat()
    }
    return formatter.format(cal.time)
}

fun showDatePicker(context : Context, textInputEditText: TextInputEditText, cal : Calendar? = null, onDateSet : (Long) -> Unit){
    val c = cal ?: Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
    val dpd = DatePickerDialog(context,
        { _, yearOfDate, monthOfYear, dayOfMonth ->
            c.set(Calendar.YEAR, yearOfDate)
            c.set(Calendar.MONTH, monthOfYear)
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            textInputEditText.setText(getFormattedDateString(c.timeInMillis, DD_MMM_YYYY))
            onDateSet(c.timeInMillis)
        },
        year, month, day)
    dpd.show()
}

fun toCalendar(time: Long) : Calendar {
    return Calendar.getInstance().apply {
        timeInMillis = time
    }
}

fun convertViewToBitmap(view: View) : Bitmap {
    val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.draw(canvas)
    return bitmap
}
