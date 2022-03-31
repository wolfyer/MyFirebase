package com.wolfyer.myfirebase

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.wolfyer.myfirebase.databinding.ActivitySaveImageBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class SaveImageActivity : AppCompatActivity() {
    lateinit var binding :ActivitySaveImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivitySaveImageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.bSave.setOnClickListener {
            val uri:Uri = saveImageToStorage(R.drawable.flower8)
            binding.imageViewSaved.setImageURI(uri)
            binding.textView.text = "Saved : $uri"
        }
        //val imgFile = File("/data/user/0/com.wolfyer.myfirebase/app_images/flower.jpg")
    }
    private fun saveImageToStorage(drawableId:Int):Uri{
        val drawable = ContextCompat.getDrawable(applicationContext,drawableId)
        val bitmap = (drawable as BitmapDrawable).bitmap
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir("images",Context.MODE_PRIVATE)
        //file = File(file,"${UUID.randomUUID()}.jpg")
        file = File(file,"flower.jpg")
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch (e:IOException){
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }
}