package com.fairfareindia.ui.drawer.mydisput.disputDetail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fairfareindia.R

class setImgAdapter (
    var context: Context,
    var stringArrayList: ArrayList<String?>?
) : RecyclerView.Adapter<setImgAdapter.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.setimage_disput, viewGroup, false)
        return ViewHolder(view)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (stringArrayList?.get(position) != null) {


            Glide.with(context!!)
                .load(stringArrayList!![position])
                .apply(
                    RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .dontTransform()
                ).into(holder.iv_vehical!!)




        }

     }


    override fun getItemCount(): Int {
        return stringArrayList!!.size
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        @JvmField
        @BindView(R.id.iv1)
        var iv_vehical: ImageView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }

}


