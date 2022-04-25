package com.flyconcept.managepro.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flyconcept.managepro.R
import com.flyconcept.managepro.databinding.ActivityTaskListBinding
import com.flyconcept.managepro.firebase.FirestoreClass
import com.flyconcept.managepro.model.Board
import com.flyconcept.managepro.model.Task
import com.flyconcept.managepro.utils.Constants
import com.flyconcept.managepro.view.adapters.TaskListItemAdapter

class TaskListActivity : BaseActivity() {
    var taskListActivityBinding: ActivityTaskListBinding? = null
    private lateinit var mBoardDetails: Board
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
    fun addUpdateTaskListSuccess(){

        FirestoreClass().getBoardDetails(this, mBoardDetails.documentID)
    }
    fun boardDetails(board: Board) {
        hideProgressDialog()
        mBoardDetails = board
        setUpActionBar(board.name)
        val addTaskList = Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)
        var rvTaskList:RecyclerView =  taskListActivityBinding!!.rvTaskList
        rvTaskList.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL,
            false)
        rvTaskList.setHasFixedSize(true)
        val adapter = TaskListItemAdapter(this, board.taskList)
        rvTaskList.adapter = adapter

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