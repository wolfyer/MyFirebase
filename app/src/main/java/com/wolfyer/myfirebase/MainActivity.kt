package com.wolfyer.myfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.wolfyer.myfirebase.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.bRegister.setOnClickListener {
            val name = binding.edName.text.toString()
            val email = binding.edEmail.text.toString()
            val password = binding.edPassword.text.toString()
            database = FirebaseDatabase.getInstance().getReference("Users")
            val Users = User(name, email, password)
            database.child(name).setValue(Users).addOnSuccessListener {
                binding.edName.text.clear()
                binding.edEmail.text.clear()
                binding.edPassword.text.clear()
                Toast.makeText(this,"Successfully Saved",Toast.LENGTH_LONG)
            }.addOnFailureListener {
                Toast.makeText(this,"Failed",Toast.LENGTH_LONG)
            }
        }
        binding.bRead.setOnClickListener {
            val name = binding.edName.text.toString()
            if (name.isNotEmpty()){
                database = FirebaseDatabase.getInstance().getReference("Users")
                database.child(name).get().addOnSuccessListener {
                    if (it.exists()){
                        val readname = it.child("name").value
                        val reademail = it.child("email").value
                        val pass = it.child("password").value
                        Toast.makeText(this,"Read Data Success",Toast.LENGTH_LONG).show()
                        binding.edName.text.clear()
                        binding.readName.text = readname.toString()
                        binding.readEmail.text = reademail.toString()
                        binding.readPass.text = pass.toString()

                    }else{
                        Toast.makeText(this,"NOt found this user",Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this,"Failed",Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this,"Read Data Failed",Toast.LENGTH_LONG).show()
            }

        }
        binding.bGoto.setOnClickListener {
            val intent = Intent(this,UploadVideoActivity::class.java)
            startActivity(intent)
        }


        /*binding.bLogin.setOnClickListener {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(
                binding.edEmail.text.toString(),
                binding.edPassword.text.toString()
            ).addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(this,"login success",Toast.LENGTH_LONG).show()
                }else Toast.makeText(this,"login fail",Toast.LENGTH_LONG).show()
            }
        }
        binding.bRegister.setOnClickListener {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                binding.edEmail.text.toString(),
                binding.edPassword.text.toString()
            ).addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(this,"register success",Toast.LENGTH_LONG).show()
                }else Toast.makeText(this,"register fail",Toast.LENGTH_LONG).show()
            }
        }*/
    }
    /*fun readData(name:String){
        database = FirebaseDatabase.getInstance().getReference("Users")
        database.child(name).get().addOnSuccessListener {
            if (it.exists()){
                val readname = it.child("name").value
                val reademail = it.child("email").value
                val pass = it.child("password").value
                Toast.makeText(this,"Read Data Success",Toast.LENGTH_LONG)
                binding.edName.text.clear()
                binding.readName.text = readname.toString()
                binding.readEmail.text = reademail.toString()
                binding.readPass.text = pass.toString()

            }else{
                Toast.makeText(this,"NOt found this user",Toast.LENGTH_LONG)
            }
        }.addOnFailureListener {
            Toast.makeText(this,"Failed",Toast.LENGTH_LONG)
        }

    }*/
}