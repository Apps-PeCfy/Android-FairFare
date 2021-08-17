package com.fairfareindia.ui.drawer.mycomplaints

import android.app.Dialog
import android.content.Context
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
import com.fairfareindia.R
import com.fairfareindia.ui.drawer.mydisput.pojo.GetDisputResponsePOJO
import com.fairfareindia.utils.AddressPopUp
import java.text.SimpleDateFormat

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

        holder.tv_myCurrentLocation!!.text = complaintList[position].originFullAddress
        holder.destnationAddress!!.text = complaintList[position].destinationFullAddress
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
        @BindView(R.id.ivViewInfo)
        var ivViewInfo: ImageView? = null


        @JvmField
        @BindView(R.id.rlHome)
        var rlHome: RelativeLayout? = null

        init {
            ButterKnife.bind(this, itemView)
            rlHome!!.setOnClickListener(this)
            ivViewInfo!!.setOnClickListener(this)

          }

        override fun onClick(v: View?) {
            if (v!!.id == R.id.rlHome) {
                iDisputClick!!.detailDisputClick(complaintList[adapterPosition].id)
            }else{
                var eventDialogBind = AddressPopUp()

                eventDialogBind?.eventInfoDialog = context?.let { Dialog(it, R.style.dialog_style) }
                eventDialogBind?.eventInfoDialog!!.setCancelable(true)
                val inflater1 =
                    context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val view12: View = inflater1.inflate(R.layout.destination_address_popup, null)
                eventDialogBind?.eventInfoDialog!!.setContentView(view12)
                ButterKnife.bind(eventDialogBind!!, view12)

                eventDialogBind!!.tvDestinationAddress!!.text =complaintList[adapterPosition].estimatedDestinationFullAddress
                eventDialogBind!!.eventInfoDialog!!.show()
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