package com.fairfareindia.ui.drawer.myrides

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fairfareindia.R
import com.fairfareindia.ui.drawer.myrides.pojo.GetRideResponsePOJO
import com.fairfareindia.utils.AddressPopUp
import com.fairfareindia.utils.ProjectUtilities
import com.iarcuschin.simpleratingbar.SimpleRatingBar
import java.text.SimpleDateFormat
import java.util.ArrayList

class MyTripsAdapter(
    var context: FragmentActivity?,
    private val MyRideList: List<GetRideResponsePOJO.DataItem>,
    var currentCity: String?
) : RecyclerView.Adapter<MyTripsAdapter.MyViewHolder>() {


    private var iclickListener: IClickListener? = null
    var streetAddress: String? = null
    var deststreetAddress: String? = null

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): MyTripsAdapter.MyViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.templete_my_ride, viewGroup, false)
        return MyViewHolder(v)

    }

    override fun getItemCount(): Int {
        return MyRideList.size
    }

    override fun onBindViewHolder(holder: MyTripsAdapter.MyViewHolder, position: Int) {

        holder.tvRateRide!!.visibility = View.GONE
        holder.ratingBar!!.visibility = View.GONE
        holder.btnStartRide!!.visibility = View.GONE

        if (MyRideList.get(position).status.equals("Completed")) {
            holder.ivViewInfo!!.visibility = View.VISIBLE
            holder.tv_status!!.text = MyRideList.get(position).status
            holder.tv_status!!.setTextColor(Color.parseColor("#749E47"))
        } else {
            holder.ivViewInfo!!.visibility = View.GONE
            holder.tv_status!!.text = MyRideList.get(position).status
            holder.tv_status!!.setTextColor(Color.parseColor("#F15E38"))

        }
        holder.tv_actualFare!!.text = "₹ " + MyRideList[position].fare.toString()
        holder.tv_vahicalName!!.text =
            MyRideList[position].vehicleName + " " + MyRideList[position].vehicleNo


        val formatviewRide = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

        val formatRide = SimpleDateFormat("dd MMM, hh:mm a")
        val formaredDate =
            formatRide.format(formatviewRide.parse((MyRideList[position].dateTime).toString()))
        val strformaredDate = formaredDate.replace("am", "AM").replace("pm", "PM")


        holder.tv_dateandTime!!.text = strformaredDate


        Glide.with(context!!)
            .load(MyRideList[position].vehicleImageUrl)
            .apply(
                RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .dontTransform()
            ).into(holder.iv_vehical!!)




        holder.tv_myCurrentLocation!!.text = (MyRideList[position].originFullAddress)!!
        holder.destnationAddress!!.text = (MyRideList[position].destinationFullAddress)!!


        if (currentCity!!.equals(MyRideList[position].cityName)) {

        } else {


        }

        if (MyRideList[position].status.equals("Completed") || MyRideList[position].status.equals("Cancelled")) {
            holder.btnStartRide!!.visibility = View.GONE
        } else {

            //  holder.btnStartRide!!.visibility = View.VISIBLE

            if (MyRideList[position].rideStatus.equals("Yes")) {
                holder.btnStartRide!!.visibility = View.VISIBLE

            } else {
                holder.btnStartRide!!.visibility = View.VISIBLE
                holder.btnStartRide!!.isEnabled = false
                holder.btnStartRide!!.setBackgroundResource(R.drawable.btn_rounded_grey)
            }
        }

        if (MyRideList[position].status.equals("Completed")) {

            if (MyRideList[position].reviewStar!!.toFloat() >= 1.0f) {
                holder.ratingBar!!.visibility = View.VISIBLE
                holder.ratingBar!!.setRating(MyRideList[position].reviewStar!!.toFloat())

            } else {
                holder.tvRateRide!!.visibility = View.VISIBLE
            }

        }

    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {


        @JvmField
        @BindView(R.id.btnStartRide)
        var btnStartRide: Button? = null

        @JvmField
        @BindView(R.id.tv_status)
        var tv_status: TextView? = null

        @JvmField
        @BindView(R.id.tvRateRide)
        var tvRateRide: TextView? = null


        @JvmField
        @BindView(R.id.tv_actualFare)
        var tv_actualFare: TextView? = null

        @JvmField
        @BindView(R.id.tv_dateandTime)
        var tv_dateandTime: TextView? = null

        @JvmField
        @BindView(R.id.tv_vahicalName)
        var tv_vahicalName: TextView? = null

        @JvmField
        @BindView(R.id.tv_myCurrentLocation)
        var tv_myCurrentLocation: TextView? = null

        @JvmField
        @BindView(R.id.rlRideDetails)
        var rlRideDetails: RelativeLayout? = null

        @JvmField
        @BindView(R.id.destnationAddress)
        var destnationAddress: TextView? = null

        @JvmField
        @BindView(R.id.ratingBar)
        var ratingBar: SimpleRatingBar? = null


        @JvmField
        @BindView(R.id.iv_vehical)
        var iv_vehical: ImageView? = null

        @JvmField
        @BindView(R.id.ivViewInfo)
        var ivViewInfo: ImageView? = null


        init {
            ButterKnife.bind(this, itemView)
            btnStartRide!!.setOnClickListener(this)
            tvRateRide!!.setOnClickListener(this)
            rlRideDetails!!.setOnClickListener(this)
            ivViewInfo!!.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

            if (ProjectUtilities.checkInternetAvailable(context)) {

                if (v!!.id == R.id.btnStartRide) {

                    if ((MyRideList[adapterPosition].status.equals("Completed"))
                        || (MyRideList[adapterPosition].status.equals("Cancelled"))
                    ) {
                        Toast.makeText(context, "Your Ride is Completed", Toast.LENGTH_LONG).show()
                    } else {
                        iclickListener!!.startRide(
                            MyRideList[adapterPosition].id,
                            MyRideList[adapterPosition].airportRateCardId,
                            MyRideList[adapterPosition].vehicleRateCardId!!.toInt(),
                            MyRideList[adapterPosition].originPlaceLat,
                            MyRideList[adapterPosition].originPlaceLong,
                            MyRideList[adapterPosition].destinationPlaceLat,
                            MyRideList[adapterPosition].destinationPlaceLong,
                            MyRideList[adapterPosition].originFullAddress,
                            MyRideList[adapterPosition].destinationFullAddress, "MyRide",
                            MyRideList[adapterPosition].estimatedTrackRide!!.tolls as ArrayList<GetRideResponsePOJO.TollsItem>?
                        )
                    }
                } else if (v!!.id == R.id.tvRateRide) {
                    iclickListener!!.rateRide(MyRideList[adapterPosition].id)
                } else if (v!!.id == R.id.rlRideDetails) {

                    if ((MyRideList[adapterPosition].status.equals("Completed"))) {
                        iclickListener!!.rideDetails(MyRideList[adapterPosition].id)
                    } else if ((MyRideList[adapterPosition].status.equals("Scheduled"))) {
                        Toast.makeText(
                            context,
                            "Your ride is not completed yet.",
                            Toast.LENGTH_LONG
                        ).show()

                    }


                } else {


                    var eventDialogBind = AddressPopUp()

                    eventDialogBind?.eventInfoDialog =
                        context?.let { Dialog(it, R.style.dialog_style) }
                    eventDialogBind?.eventInfoDialog!!.setCancelable(true)
                    val inflater1 =
                        context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val view12: View = inflater1.inflate(R.layout.destination_address_popup, null)
                    eventDialogBind?.eventInfoDialog!!.setContentView(view12)
                    ButterKnife.bind(eventDialogBind!!, view12)

                    eventDialogBind!!.tvDestinationAddress!!.text =
                        MyRideList[adapterPosition].estimatedTrackRide!!.destinationFullAddress
                    eventDialogBind!!.eventInfoDialog!!.show()


                }
            }else{
                ProjectUtilities.showToast(
                    context,
                    "No internet connection"
                )
            }
        }


    }


    fun setClickListener(iclickListener: IClickListener?) {
        this.iclickListener = iclickListener
    }


    interface IClickListener {
        fun startRide(
            id: Int,
            airport: String?,
            vahicalRadeCardID: Int,
            sLat: String?,
            sLong: String?,
            dLAt: String?,
            dLong: String?,
            originAddress: String?,
            destinationAddress: String?,
            str: String?,
            dataItem: ArrayList<GetRideResponsePOJO.TollsItem>?
        )

        fun rateRide(id: Int)
        fun rideDetails(id: Int)
    }

}