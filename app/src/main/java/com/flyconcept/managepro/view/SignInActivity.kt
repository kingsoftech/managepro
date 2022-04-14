package com.flyconcept.managepro.view

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.flyconcept.managepro.R
import com.flyconcept.managepro.databinding.ActivitySignInBinding
import com.flyconcept.managepro.firebase.FirestoreClass
import com.flyconcept.managepro.model.User
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : BaseActivity() {
    private val auth = FirebaseAuth.getInstance()
    private var activitySignInBinding: ActivitySignInBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySignInBinding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(activitySignInBinding!!.root)
        setUpActionBar()
        activitySignInBinding!!.btnSignIn.setOnClickListener {
            signInRegisteredUser()
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(activitySignInBinding!!.toolbarSignUpActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            activitySignInBinding!!.toolbarSignUpActivity.setNavigationOnClickListener { onBackPressed() }
        }
    }

    private fun signInRegisteredUser(){
        val email: String= activitySignInBinding!!.etEmail.text.toString().trim{it<= ' '}
        val password:String = activitySignInBinding!!.etPassword.text.toString().trim{it<= ' '}
        if(validateForm(email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))

            auth.
            signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        FirestoreClass().loadUserData(this)
                        Log.d("SignIn Error", "signInWithEmail:success")
                        val user = auth.currentUser
                        startActivity(Intent(this,  MainActivity::class.java))
                        //updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("SignIn Error", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                        //updateUI(null)
                    }
                }


            // Toast.makeText(this, "now you can register users", Toast.LENGTH_SHORT).show()
        }
    }
    fun validateForm( email:String, password:String): Boolean{
        return when{

            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please enter an email")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("Please enter a password")
                false
            }
            else->{
                true
            }
        }
    }

    fun signInSuccess(loggedInUser: User) {
        hideProgressDialog()
        startActivity(Intent(this,  MainActivity::class.java))
        finish()

    }
}