package com.fairfareindia.ui.intercitycompareride;

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fairfareindia.R

class IntercityCompareRideAdapter() :
    RecyclerView.Adapter<IntercityCompareRideAdapter.MyViewHolder>() {
    private var context: Context? = null
    var mList: ArrayList<InterCityCompareRideModel.VehiclesItem> = ArrayList()
    var distance: String? = null
    var estTime: String? = null
    var mListener: IntercityCompareRideAdapterInterface? = null

    fun updateAdapter(mList: ArrayList<InterCityCompareRideModel.VehiclesItem>) {
        this.mList = mList
        notifyDataSetChanged()
    }

    constructor(
        context: Context?, distance: String?, estTime: String?,
        mList: ArrayList<InterCityCompareRideModel.VehiclesItem>,
        mListener: IntercityCompareRideAdapterInterface?
    ) : this() {
        this.context = context
        this.mList = mList
        this.distance = distance
        this.estTime = estTime
        this.mListener = mListener
    }

    interface IntercityCompareRideAdapterInterface {
        fun onItemSelected(position: Int, model: InterCityCompareRideModel.VehiclesItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.templete_compareride, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model: InterCityCompareRideModel.VehiclesItem = mList[position]

        holder.txtPerson.text = model.vehicle?.noOfSeater.toString()
        holder.txtDistance.text = distance + " Km"
        holder.txtCarName.text = model.name
        holder.txtVehicleType.text = model.vehicle?.name
        holder.txtTime.text = estTime
        holder.txtTotal.text = "₹ " + model.totalPayableCharges



        Glide.with(context!!)
            .load(model.vehicle?.image)
            .apply(
                RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .dontTransform()
            )
            .into(holder.imgVehicle)

        holder.itemView.setOnClickListener{
            mListener?.onItemSelected(position, model)
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val txtTotal: TextView = itemView.findViewById(R.id.tv_total)
        val txtDistance: TextView = itemView.findViewById(R.id.place_area)
        val txtTime: TextView = itemView.findViewById(R.id.tv_time)
        val txtPerson: TextView = itemView.findViewById(R.id.tv_preson)
        val txtCarName: TextView = itemView.findViewById(R.id.tv_carName)
        val txtVehicleType: TextView = itemView.findViewById(R.id.tv_vehialType)
        val imgVehicle: ImageView = itemView.findViewById(R.id.iv_vehical)

    }
}
