package com.flyconcept.managepro.view

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build

import android.os.Bundle
import android.provider.MediaStore

import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi

import com.bumptech.glide.Glide
import com.flyconcept.managepro.R
import com.flyconcept.managepro.databinding.ActivityCreateBoardBinding
import com.flyconcept.managepro.utils.Constants
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.IOException

class CreateBoardActivity :BaseActivity() {
    var activityCreateBoardBinding: ActivityCreateBoardBinding? = null
    private var mSelectedImageFileUri: Uri? = null
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
    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        finish()
    }
}