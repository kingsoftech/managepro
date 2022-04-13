package com.flyconcept.managepro.firebase

import android.app.Activity
import com.flyconcept.managepro.model.User
import com.flyconcept.managepro.utils.Constants
import com.flyconcept.managepro.view.MainActivity
import com.flyconcept.managepro.view.SignInActivity
import com.flyconcept.managepro.view.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass(){

    private val mFireStore = FirebaseFirestore.getInstance()

     fun registerUser(activity: SignUpActivity, userInfo: User){
            mFireStore.collection(Constants.USER)
                .document(getCurrentUserId())
                .set(userInfo, SetOptions.merge()).addOnSuccessListener {
                    activity.userRegisteredSuccess()
                }
     }

    fun signInUser(activity: Activity){

        mFireStore.collection(Constants.USER)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener{document->
                val loggedInUser = document.toObject(User::class.java)
                if(loggedInUser!=null){
                    when(activity) {
                       is SignInActivity-> {activity.signInSuccess(loggedInUser) }
                        is MainActivity -> {
                            activity.updateNavigationUserDetails(loggedInUser)
                        }
                    }

                }

            }
            .addOnFailureListener {
                when(activity) {
                    is SignInActivity-> {activity.hideProgressDialog() }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                }
            }

    }

    fun getCurrentUserId(): String {

        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = " "
        if(currentUser != null){
            currentUserId = currentUser.uid
        }
        return currentUserId
    }

}


