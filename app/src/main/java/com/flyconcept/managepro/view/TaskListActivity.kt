package com.flyconcept.managepro.view

import android.os.Bundle
import android.util.Log
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
    private lateinit var mBoardDocumentId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskListActivityBinding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(taskListActivityBinding!!.root)
        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            mBoardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this, mBoardDocumentId)
    }
    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this, mBoardDetails.documentID)
    }
    fun boardDetails(board: Board) {

        mBoardDetails = board
        hideProgressDialog()
        setUpActionBar(board.name)
        val addTaskList = Task(resources.getString(R.string.add_list))
        mBoardDetails.taskList.add(addTaskList)
        var rvTaskList:RecyclerView =  taskListActivityBinding!!.rvTaskList
        rvTaskList.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL,
            false)
        rvTaskList.setHasFixedSize(true)
        val adapter = TaskListItemAdapter(this, mBoardDetails.taskList)
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

    fun createTaskList(taskListName: String) {

        Log.e("Task List Name", taskListName)

        // Create and Assign the task details
        val task = Task(taskListName, FirestoreClass().getCurrentUserId())

        mBoardDetails.taskList.add(0, task) // Add task to the first position of ArrayList
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1) // Remove the last position as we have added the item manually for adding the TaskList.

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@TaskListActivity, mBoardDetails)
    }
    fun updateTaskList(position: Int, listName: String, model: Task) {

        val task = Task(listName, model.createdBy)

        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@TaskListActivity, mBoardDetails)
    }
    fun deleteTaskList(position: Int) {
        mBoardDetails.taskList.removeAt(position)

        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@TaskListActivity, mBoardDetails)

    }
}