package com.example.fairfare.ui.ridedetails

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fairfare.R
import java.util.*

class SelectedImageAdapter(
    var context: Context,
    var stringArrayList: ArrayList<String>
) : RecyclerView.Adapter<SelectedImageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.selected_image_list, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(stringArrayList[position])
            .placeholder(R.color.Red)
            .centerCrop()
            .into(holder.image)




        holder.image.setOnClickListener { }
    }

    override fun getItemCount(): Int {
        return stringArrayList.size
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var image: ImageView

        init {
            image = itemView.findViewById<View>(R.id.iv1) as ImageView
        }
    }

}