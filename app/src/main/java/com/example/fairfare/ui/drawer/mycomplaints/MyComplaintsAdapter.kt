package com.example.fairfare.ui.drawer.mycomplaints

import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.fairfare.R
import com.example.fairfare.ui.drawer.mydisput.pojo.GetDisputResponsePOJO
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MyComplaintsAdapter(
    var context: FragmentActivity?,
    private val complaintList: List<GetDisputResponsePOJO.DataItem>
) : RecyclerView.Adapter<MyComplaintsAdapter.MyViewHolder>() {


    var streetAddress: String? = null
    var deststreetAddress: String? = null
    private var iDisputClick: IMyComplaintClickListener? = null




    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): MyComplaintsAdapter.MyViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.templete_my_complaints, viewGroup, false)
        return MyViewHolder(v)

    }

    override fun getItemCount(): Int {
        return complaintList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        Glide.with(context!!)
            .load(complaintList[position].vehicleImageUrl)
            .apply(
                RequestOptions()
                    .dontAnimate()
                    .dontTransform()
            ).into(holder.iv_vehical!!)


        val formatviewRide = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

        val formatRide = SimpleDateFormat("dd MMM, hh:mm a")
        val formaredDate = formatRide.format(formatviewRide.parse((complaintList[position].dateTime).toString()))
        val strformaredDate = formaredDate.replace("am", "AM").replace("pm", "PM")


        holder.tv_dateandTime!!.text = strformaredDate
        holder.tv_vahicalName!!.text =
            complaintList[position].vehicleName + " " + complaintList[position].vehicleNo


        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses =
                geocoder.getFromLocation(
                    (complaintList[position].originPlaceLat)!!.toDouble(),
                    (complaintList[position].originPlaceLong)!!.toDouble(), 1
                )
            if (addresses != null) {
                val returnedAddress = addresses[0]
                val strReturnedAddress =
                    StringBuilder()
                for (j in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(j))
                }
                streetAddress = strReturnedAddress.toString()
            }
        } catch (e: IOException) {
        }


        val geocoderDestination = Geocoder(context, Locale.getDefault())
        try {
            val addresses =
                geocoderDestination.getFromLocation(
                    (complaintList[position].destinationPlaceLat)!!.toDouble(),
                    (complaintList[position].destinationPlaceLong)!!.toDouble(), 1
                )
            if (addresses != null) {
                val returnedAddress = addresses[0]
                val strReturnedAddress =
                    StringBuilder()
                for (j in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(j))
                }
                deststreetAddress = strReturnedAddress.toString()
            }
        } catch (e: IOException) {
        }


        holder.tv_myCurrentLocation!!.text = streetAddress
        holder.destnationAddress!!.text = deststreetAddress
        holder.tv_status!!.text = complaintList[position].status
        holder.tvDisputNo!!.text = "ID: "+complaintList[position].disputeNo

        val formatview = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

        val formatR = SimpleDateFormat("dd MMM, hh:mm a")
        val formaredDateCreated = formatR.format(formatview.parse((complaintList[position].createdDateTime).toString()))

        val str = formaredDateCreated.replace("am", "AM").replace("pm", "PM")
        holder.tvFilledDate!!.text = "Filed Date: "+str

    }


    inner class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView),View.OnClickListener {
        @JvmField
        @BindView(R.id.tv_dateandTime)
        var tv_dateandTime: TextView? = null

        @JvmField
        @BindView(R.id.tvFilledDate)
        var tvFilledDate: TextView? = null

        @JvmField
        @BindView(R.id.tvDisputNo)
        var tvDisputNo: TextView? = null

        @JvmField
        @BindView(R.id.tv_vahicalName)
        var tv_vahicalName: TextView? = null

        @JvmField
        @BindView(R.id.tv_myCurrentLocation)
        var tv_myCurrentLocation: TextView? = null

        @JvmField
        @BindView(R.id.destnationAddress)
        var destnationAddress: TextView? = null

        @JvmField
        @BindView(R.id.tv_status)
        var tv_status: TextView? = null


        @JvmField
        @BindView(R.id.iv_vehical)
        var iv_vehical: ImageView? = null

        @JvmField
        @BindView(R.id.rlHome)
        var rlHome: RelativeLayout? = null

        init {
            ButterKnife.bind(this, itemView)
            rlHome!!.setOnClickListener(this)

          }

        override fun onClick(v: View?) {
            if (v!!.id == R.id.rlHome) {
                iDisputClick!!.detailDisputClick(complaintList[adapterPosition].id)
            }
        }


    }







    fun setClickListener(iDisputClick: IMyComplaintClickListener) {
        this.iDisputClick = iDisputClick
    }

    interface IMyComplaintClickListener {
        fun detailDisputClick(id: Int)
    }

}