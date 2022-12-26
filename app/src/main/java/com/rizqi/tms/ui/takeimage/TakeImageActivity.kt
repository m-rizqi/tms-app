package com.rizqi.tms.ui.takeimage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.rizqi.tms.databinding.ActivityTakeImageBinding
import com.rizqi.tms.utility.encodeAndSaveBitmap

class TakeImageActivity : AppCompatActivity() {
    private lateinit var binding : ActivityTakeImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTakeImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cameraViewTakeImage.apply {
            open()
            addCameraListener(object : CameraListener(){
                override fun onPictureTaken(result: PictureResult) {
                    super.onPictureTaken(result)
                    result.toBitmap { bitmap ->
                        binding.cameraViewTakeImage.close()
                        encodeAndSaveBitmap(this@TakeImageActivity, bitmap)
                        sendResult()
                    }
                }
            })
        }
        binding.mcvTakeImage.setOnClickListener {
            binding.cameraViewTakeImage.takePicture()
        }
        binding.cvTakeImageBack.setOnClickListener { onBackPressed() }
    }

    private fun sendResult(resultCode : Int = RESULT_OK){
        val intent = Intent()
        setResult(resultCode, intent)
        finish()
    }

    override fun onBackPressed() {
        sendResult(RESULT_CANCELED)
    }
}