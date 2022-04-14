package com.flyconcept.managepro.view

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.flyconcept.managepro.R
import com.flyconcept.managepro.databinding.ActivityMainBinding
import com.flyconcept.managepro.databinding.NavHeaderMainBinding
import com.flyconcept.managepro.firebase.FirestoreClass
import com.flyconcept.managepro.model.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity(),NavigationView.OnNavigationItemSelectedListener{
    var activityMainBinding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding!!.root)
        setupActionBar()
        activityMainBinding!!.navView.setNavigationItemSelectedListener(this)
        FirestoreClass().signInUser(this)
    }

    private fun setupActionBar(){
        val toolbarMainActivity = activityMainBinding!!.appBarMain.toolbarMainActivity
        setSupportActionBar(toolbarMainActivity)
        toolbarMainActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        toolbarMainActivity.setNavigationOnClickListener {
            toggleDrawer()
        }

    }

    override fun onBackPressed() {
        val drawerLayout = activityMainBinding!!.drawerLayout
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            doubleBackToExit()
        }
    }
    private fun toggleDrawer() {
        val drawerLayout = activityMainBinding!!.drawerLayout
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile->{
                Toast.makeText(this, "my profile", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_sign_out->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

        }
        activityMainBinding!!.drawerLayout .closeDrawer(GravityCompat.START)
        return true
    }

    fun updateNavigationUserDetails(loggedInUser: User) {
//        val navView =  NavHeaderMainBinding.inflate(layoutInflater)
        val navViewUserImage = findViewById<ImageView>(R.id.nav_user_image)
        val tvUsername = findViewById<TextView>(R.id.tv_username)
        Toast.makeText(this, loggedInUser.image, Toast.LENGTH_SHORT).show()
        Glide
            .with(this)
            .load(loggedInUser.image)
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navViewUserImage)
        tvUsername.text = loggedInUser.name

    }
}