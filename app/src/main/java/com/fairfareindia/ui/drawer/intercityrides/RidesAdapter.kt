package com.fairfareindia.ui.drawer.intercityrides

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fairfareindia.R
import com.fairfareindia.ui.drawer.myrides.pojo.GetRideResponsePOJO
import com.fairfareindia.utils.AppUtils
import com.fairfareindia.utils.Constants
import com.iarcuschin.simpleratingbar.SimpleRatingBar


class RidesAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var context: Context? = null
    var mList: ArrayList<GetRideResponsePOJO.DataItem> = ArrayList()
    var distance: String? = null
    var estTime: String? = null
    var mListener: RidesAdapterInterface? = null

    //pagination
    private var isLoadingAdded = false
    private val ITEM = 0
    private val LOADING = 1

    fun updateAdapter(mList: ArrayList<GetRideResponsePOJO.DataItem>) {
        this.mList = mList
        notifyDataSetChanged()
    }

    constructor(
        context: Context?,
        mList: ArrayList<GetRideResponsePOJO.DataItem>,
        mListener: RidesAdapterInterface?
    ) : this() {
        this.context = context
        this.mList = mList
        this.mListener = mListener
    }

    interface RidesAdapterInterface {
        fun onItemSelected(position: Int, model: GetRideResponsePOJO.DataItem)
        fun onStartRideClick(position: Int, model: GetRideResponsePOJO.DataItem)
        fun onRateRideClick(position: Int, model: GetRideResponsePOJO.DataItem)
        fun onViewInfoClick(position: Int, model: GetRideResponsePOJO.DataItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            ITEM -> viewHolder = getViewHolder(parent, inflater)
            LOADING -> {
                val v2 = inflater.inflate(R.layout.item_progress, parent, false)
                viewHolder = LoadingVH(v2)
            }
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM -> {
                val holder = viewHolder as MyViewHolder
                val model: GetRideResponsePOJO.DataItem = mList!![position]

                holder.txtRateRide.visibility = View.GONE
                holder.ratingBar.visibility = View.GONE
                holder.btnStartRide.visibility = View.GONE

                if (model.status == Constants.BOOKING_COMPLETED) {
                    holder.imgViewInfo.visibility = View.VISIBLE
                    holder.txtStatus.text = model.status
                    holder.txtStatus.setTextColor(Color.parseColor("#749E47"))
                } else {
                    holder.imgViewInfo.visibility = View.GONE
                    holder.txtStatus.text = model.status
                    holder.txtStatus.setTextColor(Color.parseColor("#F15E38"))

                }
                holder.txtActualFare.text = "₹ " + model.fare.toString()
                holder.txtVehicleName.text = model.vehicleName + " " + model.vehicleNo

                holder.txtDate.text = AppUtils.changeDateFormat(model.dateTime, "yyyy-MM-dd HH:mm:ss", "dd MMM, hh:mm a")


                Glide.with(context!!)
                    .load(model.vehicleImageUrl)
                    .apply(
                        RequestOptions()
                            .centerCrop()
                            .dontAnimate()
                            .dontTransform()
                    ).into(holder.imgVehicle)




                holder.txtSourceAddress.text = (model.originFullAddress)
                holder.txtDestinationAddress.text = (model.destinationFullAddress)



                if (model.status == Constants.BOOKING_COMPLETED || model.status == Constants.BOOKING_CANCELLED) {
                    holder.btnStartRide.visibility = View.GONE
                } else {
                    if (model.rideStatus.equals("Yes")) {
                        holder.btnStartRide.visibility = View.VISIBLE

                    } else {
                        holder.btnStartRide.visibility = View.VISIBLE
                        holder.btnStartRide.isEnabled = false
                        holder.btnStartRide.setBackgroundResource(R.drawable.btn_rounded_grey)
                    }
                }

                if (model.status == Constants.BOOKING_COMPLETED) {

                    if (model.reviewStar.toFloat() >= 1.0f) {
                        holder.ratingBar.visibility = View.VISIBLE
                        holder.ratingBar.rating = model.reviewStar.toFloat()

                    } else {
                        holder.txtRateRide.visibility = View.VISIBLE
                    }

                }

                holder.itemView.setOnClickListener {
                    mListener?.onItemSelected(position, model)
                }

                holder.btnStartRide.setOnClickListener {
                    mListener?.onStartRideClick(position, model)
                }

                holder.txtRateRide.setOnClickListener {
                    mListener?.onRateRideClick(position, model)
                }

                holder.imgViewInfo.setOnClickListener {
                    mListener?.onViewInfoClick(position, model)
                }



            }
            LOADING -> {
            }
        }
    }


    private fun getViewHolder(
        parent: ViewGroup,
        inflater: LayoutInflater
    ): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val v1 = inflater.inflate(R.layout.templete_my_ride, parent, false)
        viewHolder = MyViewHolder(v1)
        return viewHolder
    }

    private class LoadingVH(itemView: View?) : RecyclerView.ViewHolder(itemView!!)

    override fun getItemCount(): Int {
        return mList!!.size
    }

    class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val txtStatus: TextView = itemView.findViewById(R.id.tv_status)
        val txtRateRide: TextView = itemView.findViewById(R.id.tvRateRide)
        val txtActualFare: TextView = itemView.findViewById(R.id.tv_actualFare)
        val txtDate: TextView = itemView.findViewById(R.id.tv_dateandTime)
        val txtVehicleName: TextView = itemView.findViewById(R.id.tv_vahicalName)
        val btnStartRide: Button = itemView.findViewById(R.id.btnStartRide)
        val imgVehicle: ImageView = itemView.findViewById(R.id.iv_vehical)
        val imgViewInfo: ImageView = itemView.findViewById(R.id.ivViewInfo)
        val ratingBar: SimpleRatingBar = itemView.findViewById(R.id.ratingBar)
        val txtSourceAddress: TextView = itemView.findViewById(R.id.tv_myCurrentLocation)
        val txtDestinationAddress : TextView = itemView.findViewById(R.id.destnationAddress)
        val rlRideDetails : RelativeLayout = itemView.findViewById(R.id.rlRideDetails)

    }

    override fun getItemViewType(position: Int): Int {
        return if (position == mList?.size!! - 1 && isLoadingAdded) LOADING else ITEM
    }

    fun addAll(pmList: List<GetRideResponsePOJO.DataItem?>) {
        for (pd in pmList) {
            add(pd)
        }
    }

    fun add(model: GetRideResponsePOJO.DataItem?) {
        mList?.add(model!!)
        notifyItemInserted(mList?.size!! - 1)
    }

    fun remove(model: GetRideResponsePOJO.DataItem?) {
        val position: Int = mList?.indexOf(model)!!
        if (position > -1) {
            mList?.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        isLoadingAdded = false
        mList?.clear()
        notifyDataSetChanged()
    }

    /**
     * ADD AND REMOVE LOADING FOOTER
     **/

    open fun addLoadingFooter(): Unit {
        isLoadingAdded = true
        add(GetRideResponsePOJO().DataItem())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        if (mList?.size!! > 0) {
            val position: Int = mList?.size!! - 1
            val item: GetRideResponsePOJO.DataItem? = getItem(position)
            if (item != null) {
                mList?.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }

    private fun getItem(position: Int): GetRideResponsePOJO.DataItem? {
        return mList?.get(position)
    }
}