package com.flyconcept.managepro.view

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.flyconcept.managepro.R
import com.flyconcept.managepro.databinding.ActivitySplashBinding
import com.flyconcept.managepro.firebase.FirestoreClass

class SplashActivity : BaseActivity() {
    var splashActivityBinding: ActivitySplashBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashActivityBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashActivityBinding!!.root)

        val typeFace:Typeface = Typeface.createFromAsset(assets,"carbon bl.ttf")
        splashActivityBinding!!.tvAppName.typeface  = typeFace
        Handler(Looper.getMainLooper()).postDelayed({
            var currentUserID = FirestoreClass().getCurrentUserId()
            if(currentUserID.isNotEmpty()){
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }
            else {
                val intent = Intent(this@SplashActivity, IntroActivity::class.java)
                startActivity(intent)
            }
                finish()

        }, 2500)
    }
}