package com.flyconcept.managepro.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.flyconcept.managepro.R
import com.flyconcept.managepro.databinding.ActivityCreateBoardBinding

class CreateBoardActivity : AppCompatActivity() {
    var activityCreateBoardBinding: ActivityCreateBoardBinding?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityCreateBoardBinding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(activityCreateBoardBinding!!.root)
        setUpActionBar()
    }

    private fun setUpActionBar() {
        setSupportActionBar(activityCreateBoardBinding!!.toolbarCreateBoardActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile)

        }

        activityCreateBoardBinding!!.toolbarCreateBoardActivity.setNavigationOnClickListener { onBackPressed() }
    }
}