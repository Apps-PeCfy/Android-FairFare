package com.fairfareindia.ui.drawer.intercityrides.ridedetails

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fairfareindia.R
import com.fairfareindia.ui.intercitytrackpickup.RideDetailModel

class TollAdapter() : RecyclerView.Adapter<TollAdapter.MyViewHolder>() {
    private var context: Context? = null
    var mList: ArrayList<RideDetailModel.Tolls>? = null
    var mListener:TollAdapterInterface? = null

    interface TollAdapterInterface {
        fun onItemSelected(model: RideDetailModel.Tolls?)
    }

    fun updateAdapter(mList: ArrayList<RideDetailModel.Tolls>?) {
        this.mList = mList
        notifyDataSetChanged()
    }

    constructor(context: Context?, mList: ArrayList<RideDetailModel.Tolls>?, mListener: TollAdapterInterface?) : this() {
        this.context = context
        this.mList = mList
        this.mListener = mListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.templete_popup, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model: RideDetailModel.Tolls = mList!![position]
        holder.txtAddress.text = model.road
        holder.txtWaitTime.text = model.charges
        holder.itemView.setOnClickListener { mListener!!.onItemSelected(model) }
    }

    override fun getItemCount(): Int {
        return mList!!.size
    }

    class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val txtAddress: TextView = itemView.findViewById(R.id.tvAddress)
        val txtWaitTime: TextView = itemView.findViewById(R.id.tvWaitTime)
    }
}