package com.wolfyer.myfirebase

import android.app.Dialog
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.wolfyer.myfirebase.databinding.ActivitySignBinding

class SignActivity : AppCompatActivity() {
    private val TAG = SignActivity::class.java.simpleName
    private lateinit var binding : ActivitySignBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var databaseReference : DatabaseReference
    private lateinit var storageReference : StorageReference
    private lateinit var imageUri : Uri
    private lateinit var dialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val uid =auth.currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("UserLoginData")
        binding.bSaveSign.setOnClickListener {
            showProgressBar()
            val firstname = binding.tvFirstname.text.toString()
            val lastname = binding.tvLastname.text.toString()
            val bio = binding.tvBio.text.toString()

            val user = UserForLogin(firstname,lastname, bio)
            if(uid != null){
                databaseReference.child(uid).setValue(user).addOnCompleteListener {
                    if (it.isSuccessful){
                        uploadProfile()
                    }else{
                        hideProgressBar()
                        Toast.makeText(this,"",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun uploadProfile() {
        imageUri = Uri.parse("android.resource://$packageName/${R.drawable.flower8}")
        storageReference = FirebaseStorage.getInstance().getReference("UserLoginData/"+auth.currentUser?.uid)
        storageReference.putFile(imageUri).addOnSuccessListener {

            Log.d(TAG, "uploadProfile: upload imageUri success ")
            hideProgressBar()

        }.addOnFailureListener {

            Log.d(TAG, "uploadProfile: upload imageUri Failed ")
            hideProgressBar()
        }

    }
    private fun showProgressBar(){
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }
    private fun hideProgressBar(){
        dialog.dismiss()
    }
}