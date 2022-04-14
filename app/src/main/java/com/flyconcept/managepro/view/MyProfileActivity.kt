package com.flyconcept.managepro.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.flyconcept.managepro.R
import com.flyconcept.managepro.databinding.ActivityMyProfileBinding
import com.flyconcept.managepro.firebase.FirestoreClass
import com.flyconcept.managepro.model.User

class MyProfileActivity :BaseActivity() {

    var activityMyProfileBinding:ActivityMyProfileBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMyProfileBinding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(activityMyProfileBinding!!.root)
        setUpActionBar()
        FirestoreClass().loadUserData(this)
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

    fun setUserDataInUI(user: User){
        Glide
            .with(this)
            .load(user.image)
            .placeholder(R.drawable.ic_user_place_holder)
            .into(activityMyProfileBinding!!.ivProfileUserImage)
        activityMyProfileBinding!!.etEmail.setText(user.email)
        if(user.mobile != 0L) {
            activityMyProfileBinding!!.etMobile.setText(user.mobile.toString())
        }
        activityMyProfileBinding!!.etName.setText(user.name)



    }
}