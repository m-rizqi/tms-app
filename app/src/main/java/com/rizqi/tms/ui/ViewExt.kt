package com.rizqi.tms.ui

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.rizqi.tms.databinding.LayoutLoadingBinding

internal fun Resources.getColorSafe(colorId : Int): Int {
    return ResourcesCompat.getColor(this, colorId, null)
}
private fun getDp(context: Context): Float {
    var dp = 0f
    return context.resources.displayMetrics.density.apply {
        dp = this
    }
}
internal fun Float.dp(context: Context) = this * getDp(context)
internal fun Int.dp(context: Context) = this * getDp(context).toInt()

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