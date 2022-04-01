package com.wolfyer.myfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*

class CloudFirestoreRecyclerActivity : AppCompatActivity() {
    private lateinit var recyclerView : RecyclerView
    private lateinit var userAgeArrayList : ArrayList<UserAge>
    private lateinit var ageAdapter : AgeAdapter
    private lateinit var db :FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cloud_firestore_recycler)
        recyclerView = findViewById(R.id.cfr_recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        userAgeArrayList = arrayListOf()
        ageAdapter = AgeAdapter(userAgeArrayList)
        recyclerView.adapter = ageAdapter
        EventChangeListener()
    }

    private fun EventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection("AgrUser")
            .orderBy("firstname",Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d("FireStore Error", error.message.toString())
                }
                for (dc : DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED){
                        userAgeArrayList.add(dc.document.toObject(UserAge::class.java))
                    }
                }
                ageAdapter.notifyDataSetChanged()
            }
    }
}