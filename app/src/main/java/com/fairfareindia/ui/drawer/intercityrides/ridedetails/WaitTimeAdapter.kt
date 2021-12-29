package com.fairfareindia.ui.drawer.intercityrides.ridedetails

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fairfareindia.R
import com.fairfareindia.ui.intercitytrackpickup.RideDetailModel
import com.fairfareindia.utils.ProjectUtilities

class WaitTimeAdapter() : RecyclerView.Adapter<WaitTimeAdapter.MyViewHolder>() {
    private var context: Context? = null
    var mList: ArrayList<RideDetailModel.WaitTimeModel>? = null
    var mListener: WaitTimeAdapterInterface? = null

    interface WaitTimeAdapterInterface {
        fun onItemSelected(model: RideDetailModel.WaitTimeModel?)
    }

    fun updateAdapter(mList: ArrayList<RideDetailModel.WaitTimeModel>?) {
        this.mList = mList
        notifyDataSetChanged()
    }

    constructor(context: Context?, mList: ArrayList<RideDetailModel.WaitTimeModel>?, mListener: WaitTimeAdapterInterface?) : this() {
        this.context = context
        this.mList = mList
        this.mListener = mListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.templete_popup, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model: RideDetailModel.WaitTimeModel = mList!![position]
        holder.txtAddress.text = model.fullAddress
        holder.txtWaitTime.text = ProjectUtilities.timeInSecondsConvertingToString(context, model.waitingTime.toString())
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