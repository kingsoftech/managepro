package com.flyconcept.managepro.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.flyconcept.managepro.model.Board
import com.flyconcept.managepro.model.User
import com.flyconcept.managepro.utils.Constants
import com.flyconcept.managepro.view.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass(){

    private val mFireStore = FirebaseFirestore.getInstance()

     fun registerUser(activity: SignUpActivity, userInfo: User){
            mFireStore.collection(Constants.USER)
                .document(getCurrentUserId())
                .set(userInfo, SetOptions.merge())
                .addOnSuccessListener {
                    activity.userRegisteredSuccess()
                }
                .addOnFailureListener {
                    e->
                    Log.e(activity.javaClass.simpleName, "error writing document" +e)
                }

     }
    fun createBoard(activity: CreateBoardActivity, board:Board){
        mFireStore.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                activity.boardCreatedSuccessfully()
                Log.e(activity.javaClass.simpleName, "board created successfully")
                Toast.makeText(activity, "board created successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                    e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "error writing document" ,e)
            }
    }

    fun getBoardList(activity: MainActivity){
        mFireStore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId())
            .get()
            .addOnSuccessListener {
                document->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val boardList:ArrayList<Board> =ArrayList()
                for(i in document.documents)
                {
                    var  board = i.toObject(Board::class.java)!!
                    board.documentID =i.id
                    boardList.add(board)
                }
                activity.populateBoardsListToUI(boardList)
            }
            .addOnFailureListener {e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error updating data", e)
                Toast.makeText(activity, "error updating profile", Toast.LENGTH_SHORT).show()

            }
    }
    fun loadUserData(activity: Activity, readBoardList:Boolean = false){

        mFireStore.collection(Constants.USER)
            // The document id to get the Fields of user.
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener{document->
                val loggedInUser = document.toObject(User::class.java)
                if(loggedInUser!=null){
                    when(activity) {
                       is SignInActivity-> {activity.signInSuccess(loggedInUser) }
                        is MainActivity -> {
                            activity.updateNavigationUserDetails(loggedInUser,readBoardList)
                        }
                        is MyProfileActivity ->{
                            activity.setUserDataInUI(loggedInUser)


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
                    is MyProfileActivity ->{
                        activity.hideProgressDialog()
                    }
                }
            }

    }

    fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String, Any>){
        mFireStore.collection(Constants.USER)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Profile data updated Successfully")

                Toast.makeText(activity, "Profile updated Successfully", Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()
            }
            .addOnFailureListener {e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error updating data", e)
                Toast.makeText(activity, "error updating profile", Toast.LENGTH_SHORT).show()

            }
    }
//    fun signInUser(activity: Activity) {
//
//        // Here we pass the collection name from which we wants the data.
//        mFireStore.collection(Constants.USER)
//            // The document id to get the Fields of user.
//            .document(getCurrentUserId())
//            .get()
//            .addOnSuccessListener { document ->
//                Log.e(activity.javaClass.simpleName, document.toString())
//
//                // Here we have received the document snapshot which is converted into the User Data model object.
//                val loggedInUser = document.toObject(User::class.java)!!
//
//                // Here call a function of base activity for transferring the result to it.
//                when (activity) {
//                    is SignInActivity -> {
//                        activity.signInSuccess(loggedInUser!!)
//                    }
//                    is MainActivity -> {
//                        activity.updateNavigationUserDetails(loggedInUser!!)
//                    }
//
//                }
//            }
//            .addOnFailureListener { e ->
//                // Here call a function of base activity for transferring the result to it.
//                when (activity) {
//                    is SignInActivity -> {
//                        activity.hideProgressDialog()
//                    }
//                    is MainActivity -> {
//                        activity.hideProgressDialog()
//                    }
//
//                }
//                Log.e(
//                    activity.javaClass.simpleName,
//                    "Error while getting loggedIn user details",
//                    e
//                )
//            }
//    }
////    fun getCurrentUserId(): String {
//
//        val currentUser = FirebaseAuth.getInstance().currentUser
//        var currentUserId = " "
//        if(currentUser != null){
//            currentUserId = currentUser.uid
//        }
//        return currentUserId
//    }
    fun getCurrentUserId(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun getBoardDetails(activity: TaskListActivity, boardDocumentID: String) {
        mFireStore.collection(Constants.BOARDS)
            .document(boardDocumentID)
            .get()
            .addOnSuccessListener {
                    document->
                Log.i(activity.javaClass.simpleName, document.toString())

                activity.boardDetails(document.toObject(Board::class.java)!!)
            }
            .addOnFailureListener {e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error updating data", e)
                Toast.makeText(activity, "error updating profile", Toast.LENGTH_SHORT).show()

            }
    }
}


