package com.rizqi.tms.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val TEMP_BITMAP = "temp_bitmap"

suspend fun encodeAndSaveBitmapTemporary(context: Context, bitmap: Bitmap?, fileName : String = TEMP_BITMAP){
    withContext(Dispatchers.IO){
        val stream =context.openFileOutput(fileName, Context.MODE_PRIVATE)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.close()
        bitmap?.recycle()
    }
}

suspend fun getAndDecodeBitmapTemporary(context: Context?, fileName: String = TEMP_BITMAP) : Bitmap? {
    return withContext(Dispatchers.IO){
        val stream = context?.openFileInput(fileName)
        val bitmap = BitmapFactory.decodeStream(stream)
        stream?.close()
        bitmap
    }
}