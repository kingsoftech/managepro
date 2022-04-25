package com.flyconcept.managepro.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.flyconcept.managepro.R
import com.flyconcept.managepro.databinding.ActivityTaskListBinding
import com.flyconcept.managepro.firebase.FirestoreClass
import com.flyconcept.managepro.model.Board
import com.flyconcept.managepro.utils.Constants

class TaskListActivity : BaseActivity() {
    var taskListActivityBinding: ActivityTaskListBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskListActivityBinding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(taskListActivityBinding!!.root)
        var boardDocumentID = ""
        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            boardDocumentID = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this, boardDocumentID)
    }

    fun boardDetails(board: Board) {
        hideProgressDialog()
        setUpActionBar(board.name)

    }

    private fun setUpActionBar(title: String) {
        setSupportActionBar(taskListActivityBinding!!.toolbarTaskListActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = title

        }
        taskListActivityBinding!!.toolbarTaskListActivity.setNavigationOnClickListener { onBackPressed() }
    }
}