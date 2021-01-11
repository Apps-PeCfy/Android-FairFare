package com.example.fairfare.ui.ridedetails

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.fairfare.R
import java.io.File
import java.util.*

class SelectedImageAdapter(
    var context: Context,
    var stringArrayList: ArrayList<String>,
    var listener: SelectedImageAdapterInterface
) : RecyclerView.Adapter<SelectedImageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.selected_image_list, viewGroup, false)
        return ViewHolder(view)
    }

    interface SelectedImageAdapterInterface {
        fun itemClick(position: Int, imageName: String?)
        fun onRemoveClick(position: Int, imageName: String?)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /* Glide.with(context)
             .load(stringArrayList[position])
             .placeholder(R.color.Red)
             .centerCrop()
             .into(holder.image)*/

        /**
         * iLoma Team :- Mohasin 8 Jan
         */

        if (stringArrayList[position] != null) {

            val imgFile = File(stringArrayList[position])
            if (imgFile.exists()) {
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                holder.image.setImageBitmap(myBitmap)
            }
            holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP)

        }

        holder.image.setOnClickListener(View.OnClickListener {
            if (listener != null){
                listener.itemClick(position, stringArrayList[position])
            }
        })

        holder.imgRemove.setOnClickListener(View.OnClickListener {
            if (listener != null){
                listener.onRemoveClick(position, stringArrayList[position])
            }
        })
    }


    override fun getItemCount(): Int {
        return stringArrayList.size
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var image: ImageView
        var imgRemove: ImageView

        init {
            image = itemView.findViewById<View>(R.id.iv1) as ImageView
            imgRemove = itemView.findViewById<View>(R.id.img_remove) as ImageView
        }
    }

}