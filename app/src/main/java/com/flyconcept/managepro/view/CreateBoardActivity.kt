package com.flyconcept.managepro.view

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build

import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap

import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi

import com.bumptech.glide.Glide
import com.flyconcept.managepro.R
import com.flyconcept.managepro.databinding.ActivityCreateBoardBinding
import com.flyconcept.managepro.firebase.FirestoreClass
import com.flyconcept.managepro.model.Board
import com.flyconcept.managepro.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.IOException
import java.util.ArrayList

class CreateBoardActivity :BaseActivity() {
    var activityCreateBoardBinding: ActivityCreateBoardBinding? = null
    private var mSelectedImageFileUri: Uri? = null
    private var mBoardImageUrl:String = ""
    private lateinit var mUsername:String
    val openGalleryResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){result->
        if(result.resultCode == RESULT_OK && result.data  != null){
            mSelectedImageFileUri = result.data?.data!!
            try {
                Glide
                    .with(this)
                    .load(Uri.parse(mSelectedImageFileUri.toString()))
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(activityCreateBoardBinding!!.ivBoardImage)
            }catch (e: IOException){
                e.printStackTrace()
            }
            //storing the image on the database Storage under the folder profile image->userID

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityCreateBoardBinding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(activityCreateBoardBinding!!.root)
        setUpActionBar()
        if(intent.hasExtra(Constants.NAME))
        {
            mUsername = intent.getStringExtra(Constants.NAME)!!
        }
        activityCreateBoardBinding!!.btnCreate.setOnClickListener {
            if(mSelectedImageFileUri != null){
                uploadUserImage()
            }
            else{
                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
            }
        }
            activityCreateBoardBinding!!.ivBoardImage.setOnClickListener {
                Toast.makeText(
                    this@CreateBoardActivity,
                    "You have denied the storage permission to select image.",
                    Toast.LENGTH_SHORT
                ).show()
                Dexter.withContext(this@CreateBoardActivity)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(object : PermissionListener {
                        override fun onPermissionGranted(p0: PermissionGrantedResponse?) {

                            val galleryIntent = Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            )
                            openGalleryResultLauncher.launch(galleryIntent)


                        }

                        override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                            Toast.makeText(
                                this@CreateBoardActivity,
                                "You have denied the storage permission to select image.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        @RequiresApi(Build.VERSION_CODES.M)
                        override fun onPermissionRationaleShouldBeShown(
                            p0: PermissionRequest?,
                            p1: PermissionToken?
                        ) {
                            showRationalDialogForPermissions()
                        }

                    }).onSameThread()
                    .check()
            }
        }

    private fun setUpActionBar() {
        setSupportActionBar(activityCreateBoardBinding!!.toolbarCreateBoardActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)


        }


        activityCreateBoardBinding!!.toolbarCreateBoardActivity.setNavigationOnClickListener { onBackPressed() }
    }


    private fun createBoard(){
        val assignedUsersArrayList: ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(FirestoreClass().getCurrentUserId())
        var board: Board = Board(
            activityCreateBoardBinding!!.etBoardName.text.toString(),
            mBoardImageUrl,
            mUsername,
            assignedUsersArrayList

        )
        FirestoreClass().createBoard(this, board)
    }

    private fun getFileExtension(uri:Uri?):String?{
        return MimeTypeMap.getSingleton().
        getExtensionFromMimeType(contentResolver.getType(uri!!))
    }
    private fun uploadUserImage() {
        showProgressDialog(resources.getString(R.string.please_wait))

        if (mSelectedImageFileUri != null) {

            //getting the storage reference
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "BOARD_IMAGE" + System.currentTimeMillis() + "."
                        + getFileExtension(mSelectedImageFileUri)
            )

            //adding the file to reference
            sRef.putFile(mSelectedImageFileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    // The image upload is success
                    Log.e(
                        "Firebase Image URL",
                        taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                    )

                    // Get the downloadable url from the task snapshot
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            Log.e("Downloadable Image URL", uri.toString())

                            // assign the image url to the variable.
                            mBoardImageUrl = uri.toString()
                            hideProgressDialog()

                            // Call a function to update user details in the database.
                            createBoard()
                        }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        this@CreateBoardActivity,
                        exception.message,
                        Toast.LENGTH_LONG
                    ).show()

                    hideProgressDialog()
                }

        }
    }
    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        setResult(RESULT_OK)
        finish()
    }
}