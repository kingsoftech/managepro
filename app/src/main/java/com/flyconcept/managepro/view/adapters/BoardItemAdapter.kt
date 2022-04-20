package com.flyconcept.managepro.view.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.flyconcept.managepro.R
import com.flyconcept.managepro.databinding.ItemBoardBinding
import com.flyconcept.managepro.model.Board

class BoardItemAdapter(private val context: Context,
                       private var list: ArrayList<Board>)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener:OnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       var itemBoardBinding = ItemBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(itemBoardBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.ivImage)
            holder.tvCreatedBy.text ="Created by: ${model.createdBy}"
            holder.tvName.text = model.name

            holder.itemView.setOnClickListener {
                if(onClickListener != null)
                    //passing the model and position to the on clicked event
                    onClickListener!!.onClick(position, model)
            }
        }
    }
    interface  OnClickListener{
        fun onClick(position: Int, model:Board)
    }
    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(binding:ItemBoardBinding):RecyclerView.ViewHolder(binding.root){
        val ivImage = binding.ivBoardImage
        val tvName = binding.tvName
        val tvCreatedBy = binding.tvCreatedBy

    }
}