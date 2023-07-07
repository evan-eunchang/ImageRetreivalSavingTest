package com.eigenfaces.imageretreival

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.eigenfaces.imageretreival.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {
    private val previewImage by lazy { findViewById<MyImageView2>(R.id.ivPortrait) }

    private val selectImageFromGalleryResult = registerForActivityResult(ActivityResultContracts.GetContent())
    { uri: Uri? -> uri?.let {
            val imageStream = contentResolver.openInputStream(uri)
            val yourSelectedImage = BitmapFactory.decodeStream(imageStream)
            previewImage.setImageBitmap(yourSelectedImage)
        }
    }
    private lateinit var binding : ActivityMainBinding
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnChoosePicture.setOnClickListener {
            selectImageFromGallery()
        }

        binding.btnSaveImage.setOnClickListener {
            saveImageView()
        }


    }

    private fun saveImageView() {

        var bitmap = Bitmap.createBitmap(
            binding.ivPortrait.width, binding.ivPortrait.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        binding.ivPortrait.draw(canvas)

        val outStream: FileOutputStream?
        val dir = File("/data/data/com.eigenfaces.imageretreival/files")
        val fileName = String.format("%d.jpg", System.currentTimeMillis())
        val outFile = File(dir, fileName)
        outStream = FileOutputStream(outFile)
        bitmap = Bitmap.createScaledBitmap(bitmap, 92, 112, false)
        bitmap = bitmapToGrayscale(bitmap)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
        outStream.flush()
        outStream.close()

        return
    }

    private fun bitmapToGrayscale(bm : Bitmap) : Bitmap {
        val bmGrayscale = Bitmap.createBitmap(
            bm.width, bm.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bmGrayscale)
        val paint = Paint()
        val cm = ColorMatrix()
        cm.setSaturation(0f)
        val filter = ColorMatrixColorFilter(cm)
        paint.colorFilter = filter
        canvas.drawBitmap(bm, 0f, 0f, paint)
        return bmGrayscale
    }

    private fun selectImageFromGallery() {
        selectImageFromGalleryResult.launch("image/*")
    }
}