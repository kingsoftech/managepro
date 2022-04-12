package com.flyconcept.managepro.firebase

import com.flyconcept.managepro.model.User
import com.flyconcept.managepro.utils.Constants
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

    fun getCurrentUserId(): String {

        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = " "
        if(currentUser != null){
            currentUserId = currentUser.uid
        }
        return currentUserId
    }

}


