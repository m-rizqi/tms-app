package com.rizqi.tms.utility

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException

abstract class ExternalStorageUtility {
    var hasReadPermission = false
    var hasWritePermission = false
    protected lateinit var activityCompat: AppCompatActivity
    protected lateinit var context : Context
    protected lateinit var permissionsLauncher : ActivityResultLauncher<Array<String>>
    abstract fun setup(activityCompat: AppCompatActivity)
    abstract fun checkRequestPermission()
    abstract fun save(displayName : String, bitmap : Bitmap) : Boolean
}

class ExternalStorageUtilityImpl() : ExternalStorageUtility() {
    override fun setup(activityCompat: AppCompatActivity) {
        this.activityCompat = activityCompat
        this.context = activityCompat.applicationContext
        permissionsLauncher = activityCompat.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){}
        checkRequestPermission()
    }

    override fun checkRequestPermission() {
        val isMinSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        hasReadPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        hasWritePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED || isMinSdk29

        val permissionsToRequest = mutableListOf<String>()
        if (!hasReadPermission) permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        if (!hasWritePermission) permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissionsToRequest.isNotEmpty()){
            permissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    override fun save(displayName : String, bitmap : Bitmap) : Boolean{
        if (!hasWritePermission) checkRequestPermission()
        val imageCollection = sdk29AndUp {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
            put(MediaStore.Images.Media.WIDTH, bitmap.width)
            put(MediaStore.Images.Media.HEIGHT, bitmap.height)
        }

        return try {
            context.contentResolver.insert(imageCollection, contentValues)?.also { uri ->
                context.contentResolver.openOutputStream(uri).use {outputStream ->
                    if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)){
                        throw IOException("Couldn't save bitmap")
                    }
                }
            } ?: throw IOException("Couldn't create MediaStore entry")
            true
        }catch (e : IOException){
            e.printStackTrace()
            false
        }
    }

}