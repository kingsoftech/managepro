package com.flyconcept.managepro.view

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.flyconcept.managepro.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

open class BaseActivity : AppCompatActivity() {
    private var doubleBackToExitPressedOnce = false
    private lateinit var mProgressDialog:Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun showProgressDialog(text:String){
            mProgressDialog = Dialog(this)
            mProgressDialog.setContentView(R.layout.dialog_progress)
            // var tvText = findViewById<TextView>(R.id.tv_progress_text)

            mProgressDialog.show()

    }
    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }

    fun getCurrentUserID():String{
        return FirebaseAuth.getInstance().currentUser!!.toString()
    }

    fun doubleBackToExit(){
        if(doubleBackToExitPressedOnce){
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this,resources.getString( R.string.please_click_back_again_to_exit),
            Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        },2000)
    }

    fun showErrorSnackBar(message:String) {
        val snackBar = Snackbar.make(findViewById(android.R.id.content),message,
        Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor( ContextCompat.getColor(this, R.color.snackbar_error_color))
        snackBar.show()
    }
    fun showRationalDialogForPermissions() {

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

}