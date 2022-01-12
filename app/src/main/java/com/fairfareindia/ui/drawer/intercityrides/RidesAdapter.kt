package com.fairfareindia.ui.drawer.intercityrides

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fairfareindia.R
import com.fairfareindia.ui.drawer.myrides.pojo.GetRideResponsePOJO
import com.fairfareindia.utils.AppUtils
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.ProjectUtilities
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
        fun onCancelRideClick(position: Int, model: GetRideResponsePOJO.DataItem)
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
                val model: GetRideResponsePOJO.DataItem = mList[position]

                holder.txtVehicleName.text = model.vehicleName + " " + model.vehicleNo

                holder.txtDate.text = AppUtils.changeDateFormat(model.dateTime, "yyyy-MM-dd HH:mm:ss", "dd MMM, h:mm a")

                holder.txtSourceAddress.text = (model.originFullAddress)
                if (model.actualEndAddress.isNullOrEmpty()){
                    holder.txtDestinationAddress.text = model.destinationFullAddress
                }else{
                    holder.txtDestinationAddress.text = model.actualEndAddress
                }


                holder.txtActualFare.text = ProjectUtilities.getAmountInFormat(model.totalfare?.toDouble())

                holder.txtStatus.text = model.status

                if (model.status == Constants.BOOKING_PENDING){
                    holder.txtStatus.text = context?.resources?.getString(R.string.status_booked)
                }else{
                    holder.txtStatus.text = model.status
                }

                if (model.status == Constants.BOOKING_COMPLETED) {
                    holder.imgViewInfo.visibility = View.VISIBLE
                    holder.txtStatus.setTextColor(context?.resources?.getColor(R.color.colorGreen)!!)
                }else{
                    holder.imgViewInfo.visibility = View.GONE
                    holder.txtStatus.setTextColor(context?.resources?.getColor(R.color.colorPrimary)!!)
                }


                if (model.status == Constants.BOOKING_SCHEDULED || model.status == Constants.BOOKING_PENDING || model.status == Constants.BOOKING_ARRIVING || model.status == Constants.BOOKING_ARRIVED){
                    holder.llCancelRide.visibility = View.VISIBLE
                }else{
                    holder.llCancelRide.visibility = View.GONE
                }

                if (model.permitType == Constants.TYPE_INTERCITY){
                    holder.txtPermitType.visibility = View.VISIBLE
                    holder.txtPermitType.text =  model.permitType + " (${model.intercityFromCity?.name} - ${model.intercitytoCity?.name})"
                }else{
                    if (model.status == Constants.BOOKING_COMPLETED) {
                        if (model.reviewStar.toFloat() >= 1.0f) {
                            holder.ratingBar.visibility = View.VISIBLE
                            holder.ratingBar.rating = model.reviewStar.toFloat()
                        } else {
                            holder.txtRateRide.visibility = View.VISIBLE
                        }
                    }
                }


                Glide.with(context!!)
                    .load(model.vehicleImageUrl)
                    .apply(
                        RequestOptions()
                            .centerCrop()
                            .dontAnimate()
                            .dontTransform()
                    ).into(holder.imgVehicle)



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

                holder.llCancelRide.setOnClickListener {
                    mListener?.onCancelRideClick(position, model)
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
        val v1 = inflater.inflate(R.layout.item_ride, parent, false)
        viewHolder = MyViewHolder(v1)
        return viewHolder
    }

    private class LoadingVH(itemView: View?) : RecyclerView.ViewHolder(itemView!!)

    override fun getItemCount(): Int {
        return mList.size
    }

    class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val txtPermitType: TextView = itemView.findViewById(R.id.txt_permit_type)
        val txtStatus: TextView = itemView.findViewById(R.id.txt_status)
        val txtRateRide: TextView = itemView.findViewById(R.id.txt_rate_ride)
        val txtActualFare: TextView = itemView.findViewById(R.id.txt_total_charges)
        val txtDate: TextView = itemView.findViewById(R.id.txt_date)
        val txtVehicleName: TextView = itemView.findViewById(R.id.txt_vehicle_name)
        val btnStartRide: Button = itemView.findViewById(R.id.btn_start_ride)
        val imgVehicle: ImageView = itemView.findViewById(R.id.img_vehicle)
        val imgViewInfo: ImageView = itemView.findViewById(R.id.img_view_info)
        val ratingBar: SimpleRatingBar = itemView.findViewById(R.id.ratingBar)
        val txtSourceAddress: TextView = itemView.findViewById(R.id.txt_source_address)
        val txtDestinationAddress : TextView = itemView.findViewById(R.id.txt_destination_address)
        val llCancelRide : LinearLayout = itemView.findViewById(R.id.ll_cancel_ride)

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