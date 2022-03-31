package com.wolfyer.myfirebase

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.wolfyer.myfirebase.databinding.ActivityUploadVideoBinding
import java.io.File

class UploadVideoActivity : AppCompatActivity() {
    lateinit var binding: ActivityUploadVideoBinding
    lateinit var actionBar: ActionBar
    private val PICK_VIDEO = 1
    private val VIDEO_PICK_GALLERY = 2
    private val VIDEO_PICK_CAMERA = 3
    private val CAMERA_REQUEST_CODE = 4
    private lateinit var cameraPermissions: Array<String>
    private var videoUri:Uri? = null
    //progressDialog
    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityUploadVideoBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val videoView = binding.uploadVideoView
        videoView.setMediaController(android.widget.MediaController(this))
        videoView.start()

        binding.bChooseVideo.setOnClickListener{
            val intent = Intent()
            intent.type = "video/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent,PICK_VIDEO)
        }
        //actionBar
        actionBar = supportActionBar!!
        actionBar.title = "Add New Video"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)
        //init camera permission array
        cameraPermissions = arrayOf(android.Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        //init progressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Uploading Video...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.bPickvideo.setOnClickListener {
            videoPickDialog()
        }
        //upload video fun01
        binding.bShowVideo.setOnClickListener {
            uploadVideo()
        }
        //upload video
        binding.bUploadVideo.setOnClickListener {
            val title  = binding.edVideoName.text.toString().trim()
            if (TextUtils.isEmpty(title)){
                Toast.makeText(this,"Give video a name first!",Toast.LENGTH_LONG).show()
            }
            else if (videoUri == null){
                Toast.makeText(this,"Pick video first!",Toast.LENGTH_LONG).show()
            }
            else{
                uploadVideoFirebase()
            }
        }

    }

    private fun uploadVideoFirebase() {
        //show progress
        progressDialog.show()
        //timestamp
        val titleName = binding.edVideoName.text.toString()
        val timestamp = ""+System.currentTimeMillis()
        val filePathAndName = "Videos/video_$timestamp"
        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        //upload video using uri of video to storage
        storageReference.putFile(videoUri!!).addOnSuccessListener { taskSnapshot ->
            val uriTask = taskSnapshot.storage.downloadUrl
            while (!uriTask.isSuccessful);
                val downloadUri = uriTask.result
            if (uriTask.isSuccessful){
                val hasMap = HashMap<String,Any>()
                hasMap["id"] = timestamp.toString()
                hasMap["title"] = titleName
                hasMap["timestamp"] = timestamp.toString()
                hasMap["videoUri"] = downloadUri.toString()

                //put the above into db
                val dbReference = FirebaseDatabase.getInstance().getReference("Videos")
                //val vvideos = Member(timestamp,title,downloadUri)
                dbReference.child(timestamp)
                    .setValue(hasMap)
                    .addOnSuccessListener { taskSnapshot->
                        progressDialog.dismiss()
                    }.addOnFailureListener {
                        Toast.makeText(this,"failed adding video inf ",Toast.LENGTH_LONG).show()
                    }

            }
        }.addOnFailureListener {
            progressDialog.dismiss()
            Toast.makeText(this,"fail loading",Toast.LENGTH_LONG).show()
        }
    }
    private fun setVideoToView(){
        val mediaController = android.widget.MediaController(this)
        val videoView = binding.uploadVideoView
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)
        videoView.setVideoURI(videoUri)
        videoView.requestFocus()
        videoView.setOnPreparedListener{
            videoView.pause()
        }

    }

    private fun videoPickDialog(){
        //options to display in dialog
        val options = arrayOf("Camera","Gallery")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Video From")
            .setItems(options){
                dialogInterface,i->
                if (i==0){
                    //camera clicked
                    if (!checkCameraPermission()){
                        requestCameraPermissions()
                    }else{
                        videoPickCamera()
                    }
                }else{
                    //gallery clicked
                    videoPickGallery()
                }
            }.show()

    }
    private fun requestCameraPermissions(){
        //request camera permissions
        ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_CODE)
    }
    private fun checkCameraPermission():Boolean{
        val result1 = ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val result2 = ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        return result1 && result2
    }
    private fun videoPickGallery(){
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(
            Intent.createChooser(intent,"Choose video"),VIDEO_PICK_GALLERY
        )
    }
    private fun  videoPickCamera(){
        /*val capturedVideo = File(externalCacheDir, "My_Captured_video.mp4")
        if(capturedVideo.exists()) {
            capturedVideo.delete()
        }
        capturedVideo.createNewFile()
        videoUri = if(Build.VERSION.SDK_INT >= 24){
            FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider",
                capturedVideo)
        } else {
            Uri.fromFile(capturedVideo)
        }
        val i = Intent("android.media.action.VIDEO_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT,videoUri)*/
        val i = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(i,VIDEO_PICK_CAMERA)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() //goto previous activity
        return super.onSupportNavigateUp()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            CAMERA_REQUEST_CODE->
                if (grantResults.isNotEmpty()){
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (cameraAccepted && storageAccepted){
                        videoPickCamera()
                    }else{
                        Toast.makeText(this,"Permissions denied",Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK){
            if (requestCode == VIDEO_PICK_CAMERA){
                //videoUri = data?.data
                binding.uploadVideoView.setVideoURI(videoUri)
                Log.d("camera catch", "${data?.data}")
                //binding.uploadVideoView.start()
                //setVideoToView()
            }
            else if (requestCode == VIDEO_PICK_GALLERY){
                videoUri = data!!.data
                //setVideoToView()
                binding.uploadVideoView.setVideoURI(videoUri)
            }
            else if (requestCode == PICK_VIDEO || data!=null || data?.data !== null){
                videoUri = data?.data!!
                binding.uploadVideoView.setVideoURI(videoUri)
            }
        }
        else{
            Toast.makeText(this,"Cancelled",Toast.LENGTH_LONG).show()
        }
    }
    private fun uploadVideo(){
        val videoName = binding.edVideoName.text.toString()
        val storageRef = FirebaseStorage.getInstance().getReference("VideoStorage/$videoName")
        val databaseRef = FirebaseDatabase.getInstance().getReference("videoData")

        val searchName = binding.edVideoName.text.toString().toLowerCase()
        if (videoUri != null || !TextUtils.isEmpty(videoName)){
            binding.pbLoadingVideo.visibility = View.VISIBLE

            //val ref = storageRef.child( System.currentTimeMillis().toString()+"."+getExt(videoUri!!))
            val ref = storageRef.child(videoName)
            //val uploadTask:UploadTask =storageRef.putFile(videoUri)
            //uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot,Task<Uri>>){}
            //val urltask:Task<Uri> =
            storageRef.putFile(videoUri!!).continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                    return@Continuation ref.downloadUrl
            }).addOnCompleteListener {
                        task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        binding.pbLoadingVideo.visibility = View.INVISIBLE
                        Toast.makeText(this,"Data saved",Toast.LENGTH_LONG).show()
                        Log.d("STORAGE_UTIL", "downloadUri: $downloadUri")
                        val member = Member(videoName.toString(),searchName.toString(),downloadUri.toString())
                        //val i = databaseRef.push().key
                        databaseRef.child(videoName).setValue(member).addOnSuccessListener {
                            Toast.makeText(this,"Successfully Saved DATA",Toast.LENGTH_LONG).show()
                        }.addOnFailureListener {
                            Toast.makeText(this,"Failed DATA",Toast.LENGTH_LONG).show()
                        }
                    } else {
                        // Handle failures
                        Toast.makeText(this,"Failed",Toast.LENGTH_LONG).show()
                    }
                }
        }else{
            Toast.makeText(this,"All Files are required",Toast.LENGTH_LONG).show()
        }
    }

    private fun getExt(uri: Uri):String{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri))!!
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_VIDEO || resultCode == RESULT_OK || data!=null || data?.data !== null){
            videoUri = data?.data!!
            binding.uploadVideoView.setVideoURI(videoUri)
        }

    }*/

}