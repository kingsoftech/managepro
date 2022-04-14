package com.flyconcept.managepro.view

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.flyconcept.managepro.R
import com.flyconcept.managepro.databinding.ActivityMyProfileBinding
import com.flyconcept.managepro.firebase.FirestoreClass
import com.flyconcept.managepro.model.User
import com.flyconcept.managepro.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.IOException

class MyProfileActivity :BaseActivity() {
    private val mAuth = FirebaseAuth.getInstance()
    private lateinit var storage:FirebaseStorage
    private lateinit var database: FirebaseDatabase
    private lateinit var mUserDetails: User
    private var mSelectedImageFileUri:Uri? = null
    private var mProfileImageUri:String = ""

//    private lateinit var mAuth: FirebaseAuth
    val openGalleryResultLauncher:ActivityResultLauncher<Intent> = registerForActivityResult(
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
                    .into(activityMyProfileBinding!!.ivProfileUserImage)
            }catch (e:IOException){
                e.printStackTrace()
            }
            //storing the image on the database Storage under the folder profile image->userID

        }
    }
    private var activityMyProfileBinding:ActivityMyProfileBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMyProfileBinding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(activityMyProfileBinding!!.root)
        setUpActionBar()
        FirestoreClass().loadUserData(this)
        val ivImage = findViewById<ImageView>(R.id.iv_profile_user_image)
        activityMyProfileBinding!!.ivProfileUserImage.setOnClickListener {
            Toast.makeText(
                this@MyProfileActivity,
                "You have denied the storage permission to select image.",
                Toast.LENGTH_SHORT
            ).show()
            Dexter.withContext(this@MyProfileActivity)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object: PermissionListener{
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {

                        val galleryIntent = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )
                        openGalleryResultLauncher.launch(galleryIntent)


                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                        Toast.makeText(
                            this@MyProfileActivity,
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
       activityMyProfileBinding!!.btnUpdate.setOnClickListener {
           if(mSelectedImageFileUri != null){
               uploadUserImage()
           }
           else{
               showProgressDialog(resources.getString(R.string.please_wait))
               updateUserProfileDate()
           }
       }
    }

    private fun setUpActionBar() {
        setSupportActionBar(activityMyProfileBinding!!.toolbarMyProfileActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile)

        }

        activityMyProfileBinding!!.toolbarMyProfileActivity.setNavigationOnClickListener { onBackPressed() }
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
                "USER_IMAGE" + System.currentTimeMillis() + "."
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
                            mProfileImageUri = uri.toString()
                            hideProgressDialog()

                            // Call a function to update user details in the database.
                            updateUserProfileDate()
                        }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        this@MyProfileActivity,
                        exception.message,
                        Toast.LENGTH_LONG
                    ).show()

                    hideProgressDialog()
                }

        }
    }
    fun profileUpdateSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }
    fun updateUserProfileDate(){
        val userHashMap= HashMap<String, Any>()
        var anychangesMade = false
        if(mProfileImageUri.isNotEmpty() && mProfileImageUri != mUserDetails.image){
            userHashMap[Constants.IMAGE] = mProfileImageUri
            anychangesMade =true
        }
        if(activityMyProfileBinding!!.etName.text.toString() != mUserDetails.name){
            userHashMap[Constants.NAME] = activityMyProfileBinding!!.etName.text.toString()
            anychangesMade = true
        }
        if(activityMyProfileBinding!!.etMobile.text.toString() != mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE] = activityMyProfileBinding!!.etMobile.text.toString().toLong()
            anychangesMade = true
        }
        if(anychangesMade) {
            FirestoreClass().updateUserProfileData(this, userHashMap)
        }
    }
    fun setUserDataInUI(user: User){
        mUserDetails = user
        Glide
            .with(this@MyProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(activityMyProfileBinding!!.ivProfileUserImage)
        activityMyProfileBinding!!.etEmail.setText(user.email)
        if(user.mobile != 0L) {
            activityMyProfileBinding!!.etMobile.setText(user.mobile.toString())
        }
        activityMyProfileBinding!!.etName.setText(user.name)



    }
}