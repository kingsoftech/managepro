package com.flyconcept.managepro.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import com.flyconcept.managepro.R
import com.flyconcept.managepro.databinding.ActivityIntroBinding
import com.flyconcept.managepro.databinding.ActivitySignUpBinding

class IntroActivity : BaseActivity() {
    var activityIntroBinding: ActivityIntroBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityIntroBinding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(activityIntroBinding!!.root)
        val btnSignUp:Button = findViewById(R.id.btn_sign_up)

        btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        activityIntroBinding!!.btnSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }


}