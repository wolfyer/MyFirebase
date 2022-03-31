package com.wolfyer.myfirebase

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.wolfyer.myfirebase.databinding.ActivityUploadCloudBinding
import java.text.SimpleDateFormat
import java.util.*

class UploadCloudActivity : AppCompatActivity() {
    lateinit var binding: ActivityUploadCloudBinding
    lateinit var ImageUri:Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityUploadCloudBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.bSelectImage.setOnClickListener {
            selectImage()
        }

        binding.bUploadImage.setOnClickListener {
            uploadImage()
        }

    }

    private fun uploadImage() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading File.....")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storageRef = FirebaseStorage.getInstance().getReference("images/$fileName")

        storageRef.putFile(ImageUri).addOnSuccessListener {
            binding.uploadImageView.setImageURI(null)
            Toast.makeText(this,"Success Upload!",Toast.LENGTH_LONG).show()
            if (progressDialog.isShowing) progressDialog.dismiss()

        }.addOnFailureListener{
            if (progressDialog.isShowing) progressDialog.dismiss()
            Toast.makeText(this,"Failed",Toast.LENGTH_LONG).show()
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI)

        startActivityForResult(intent,100)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode ==100 && resultCode == RESULT_OK){
            ImageUri = data?.data!!
            binding.uploadImageView.setImageURI(ImageUri)
        }
    }
}