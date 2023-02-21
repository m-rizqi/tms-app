package com.rizqi.tms.ui.espresso

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class DrawableMatcher(
    val expectedResourceId : Int
) : TypeSafeMatcher<View>() {
    private var resourceName : String? = null
    override fun describeTo(description: Description?) {
        description?.appendText("with drawable from resource id : ")
        description?.appendValue(expectedResourceId)
        if (resourceName != null) {
            description?.appendText("[");
            description?.appendText(resourceName);
            description?.appendText("]");
        }
    }

    override fun matchesSafely(target: View?): Boolean {
        if (target !is ImageView) return false
        val imageView = target as ImageView
        if (expectedResourceId < 0) return imageView.drawable == null
        val resources = target.context.resources
        val expectedDrawable = resources.getDrawable(expectedResourceId, null) ?: return false
        resourceName = resources.getResourceEntryName(expectedResourceId)

        val actualBitmap = getBitmap(imageView.drawable)
        val expectedBitmap = getBitmap(expectedDrawable)
        return actualBitmap.sameAs(expectedBitmap)
    }

    private fun getBitmap(drawable: Drawable) : Bitmap {
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}