package com.flyconcept.managepro.view.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.flyconcept.managepro.databinding.ItemTaskBinding
import com.flyconcept.managepro.model.Task
import com.flyconcept.managepro.view.TaskListActivity

class TaskListItemAdapter(private val context: Context, private var list: ArrayList<Task>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var itemTaskBinding = ItemTaskBinding.inflate(LayoutInflater.from(context), parent, false)
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width*0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins((15.toDP()).toPX(), 0,(40.toDP().toPX()), 0)
        itemTaskBinding.root.layoutParams = layoutParams
        return MyViewHolder(itemTaskBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            if(position == list.size - 1){
                holder.tvAddTaskList.visibility = View.VISIBLE
                holder.llTaskItem.visibility = View.GONE
            }
            else{
                holder.tvAddTaskList.visibility = View.GONE
                holder.llTaskItem.visibility = View.VISIBLE
            }
            holder.tvTaskListTitle.text = model.title
            holder.tvAddTaskList.setOnClickListener {
                holder.tvAddTaskList.visibility = View.GONE
                holder.cvAddTaskListName.visibility = View.VISIBLE
            }
        holder.ibCloseListName.setOnClickListener{
            holder.tvAddTaskList.visibility = View.VISIBLE
            holder.cvAddTaskListName.visibility = View.GONE

        }

            holder.ibDoneListName.setOnClickListener {
                val listName = holder.etTaskListName.text.toString()
                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){

                        context.createTaskList(listName)
                    }
                }
                else{
                    Toast.makeText(context, "Please enter list Name", Toast.LENGTH_SHORT).show()
                }
            }
            holder.ibDoneEditingListName.setOnClickListener {
                val listName = holder.etEditTaskListName.text.toString()
                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){

                        context.updateTaskList(position,listName,model)
                    }
                }
                else{
                    Toast.makeText(context, "Please enter list Name", Toast.LENGTH_SHORT).show()
                }
            }

            holder.ibCloseEditableView.setOnClickListener {
                holder.llTitleView.visibility = View.VISIBLE
                holder.cvEditTaskListName.visibility = View.GONE
            }
            holder.ibEditListName.setOnClickListener {

                holder.etEditTaskListName.setText(model.title) // Set the existing title
                holder.llTitleView.visibility = View.GONE
                holder.cvEditTaskListName.visibility = View.VISIBLE
            }
            holder.ibDeleteList.setOnClickListener {

                alertDialogForDeleteList(position, model.title)
            }



        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class MyViewHolder(binding:ItemTaskBinding):RecyclerView.ViewHolder(binding.root){
        val tvAddTaskList = binding.tvAddTaskList
        val llTaskItem = binding.llTaskItem
        val tvTaskListTitle = binding.tvTaskListTitle
        val cvAddTaskListName = binding.cvAddTaskListName
        val ibCloseListName = binding.ibCloseListName
        val ibDoneListName = binding.ibDoneListName
        val etTaskListName = binding.etTaskListName
        val ibDeleteList = binding.ibDeleteList
        val ibEditListName = binding.ibEditListName
        val etEditTaskListName = binding.etEditTaskListName
        val llTitleView = binding.llTitleView
        val ibCloseEditableView = binding.ibCloseEditableView
        val cvEditTaskListName = binding.cvEditTaskListName
        val ibDoneEditingListName = binding.ibDoneEditListName

    }

    private fun Int.toDP():Int = (this/Resources.getSystem().displayMetrics.density).toInt()
    private fun Int.toPX():Int = (this*Resources.getSystem().displayMetrics.density).toInt()
    private fun alertDialogForDeleteList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed

            if (context is TaskListActivity) {
                context.deleteTaskList(position)
            }
        }

        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }

}