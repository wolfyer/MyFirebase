package com.wolfyer.myfirebase

import android.app.ProgressDialog
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.wolfyer.myfirebase.databinding.ActivityFireCloudStorageBinding
import java.io.File

class FireCloudStorageActivity : AppCompatActivity() {
    lateinit var binding: ActivityFireCloudStorageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityFireCloudStorageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.bCloudImage.setOnClickListener {
            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Fetching image...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            val imageName = binding.edImageName.text.toString()
            val storageRef = FirebaseStorage.getInstance().reference.child("images/$imageName.jpg")
            val localfile = File.createTempFile("tempImage","jpg")
            storageRef.getFile(localfile).addOnSuccessListener {

                if (progressDialog.isShowing)
                    progressDialog.dismiss()

                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                binding.imageViewCloud.setImageBitmap(bitmap)
            }.addOnFailureListener{

                if (progressDialog.isShowing)
                    progressDialog.dismiss()

                Toast.makeText(this,"Failed to  retrieve image",Toast.LENGTH_LONG).show()
            }
        }
    }
}