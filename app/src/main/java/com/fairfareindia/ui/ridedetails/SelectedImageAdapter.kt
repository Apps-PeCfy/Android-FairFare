package com.fairfareindia.ui.ridedetails

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fairfareindia.R
import java.io.File
import kotlin.collections.ArrayList

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

    fun updateAdapter(list : ArrayList<String>) {
        stringArrayList = list
        notifyDataSetChanged()
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

        if (stringArrayList[position] != null && stringArrayList[position].contains("http" , ignoreCase = true)) {
            Glide.with(context!!)
                .load(stringArrayList[position])
                .apply(
                    RequestOptions()
                        .dontAnimate()
                        .dontTransform()
                ).into(holder.image!!)
            holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP)
            holder.imgRemove.visibility = View.GONE

        }else {
            val imgFile = File(stringArrayList[position])
            if (imgFile.exists()) {
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                holder.image.setImageBitmap(myBitmap)
            }
            holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP)
            holder.imgRemove.visibility = View.VISIBLE
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