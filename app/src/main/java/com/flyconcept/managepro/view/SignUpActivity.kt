package com.flyconcept.managepro.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.flyconcept.managepro.R
import com.flyconcept.managepro.databinding.ActivitySignUpBinding
import com.flyconcept.managepro.firebase.FirestoreClass
import com.flyconcept.managepro.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : BaseActivity() {

    var activitySignUpBinding: ActivitySignUpBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySignUpBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(activitySignUpBinding!!.root)
        setUpActionBar()
        activitySignUpBinding!!.btnSignUp.setOnClickListener {
            registerUser()
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(activitySignUpBinding!!.toolbarSignUpActivity)

        val actionBar =supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        activitySignUpBinding!!.toolbarSignUpActivity.setNavigationOnClickListener { onBackPressed() }
    }
    private fun registerUser(){

        val name: String = activitySignUpBinding!!.etName.text.toString().trim{it <= ' '}
        val email: String= activitySignUpBinding!!.etEmail.text.toString().trim{it<= ' '}
        val password:String = activitySignUpBinding!!.etPassword.text.toString().trim{it<= ' '}
        if(validateForm(name,email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))

            FirebaseAuth.
            getInstance().
            createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    task->
                    //hideProgressDialog()
                    if(task.isSuccessful){
                        val firebaseUser:FirebaseUser = task.result!!.user!!
                        val registerEmail= firebaseUser.email!!
                        val user = User(firebaseUser.uid, name, registerEmail)

                        FirestoreClass().registerUser(this, user)
//                        Toast.makeText(this,
//                            " $name you have successfully registered the email address $registerEmail",
//                            Toast.LENGTH_SHORT)
//                            .show()
//
//                        FirebaseAuth.getInstance().signOut()
                       /// finish()
                    }
                    else{
                        Toast.makeText(this,
                        task.exception!!.message,
                            Toast.LENGTH_LONG)
                            .show()
                    }
                }
           // Toast.makeText(this, "now you can register users", Toast.LENGTH_SHORT).show()
        }
    }
    fun validateForm(name: String, email:String, password:String): Boolean{
        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("please enter your name")
                false
            }
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

    fun  userRegisteredSuccess(){

        Toast.makeText(this,
        "you have " +
                "successfully register", Toast.LENGTH_SHORT).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }
}
