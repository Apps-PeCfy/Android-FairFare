package com.fairfareindia.ui.viewride

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.fairfareindia.R
import com.fairfareindia.ui.compareride.pojo.CompareRideResponsePOJO

class ViewRideTollsPopUp (
    var arrWaitTime: List<CompareRideResponsePOJO.TollsItem>
) : RecyclerView.Adapter<ViewRideTollsPopUp.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.templete_popup, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return arrWaitTime.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.tvAddress!!.text = arrWaitTime[position].road

        holder.tvWaitTime!!.text =  "₹ "+arrWaitTime[position].charges.toString()


    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @JvmField
        @BindView(R.id.tvWaitTime)
        var tvWaitTime: TextView? = null


        @JvmField
        @BindView(R.id.tvAddress)
        var tvAddress: TextView? = null


        init {
            ButterKnife.bind(this, itemView)

        }


    }

}