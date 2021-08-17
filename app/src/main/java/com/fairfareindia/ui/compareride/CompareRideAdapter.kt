package com.fairfareindia.ui.compareride

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fairfareindia.R
import com.fairfareindia.ui.compareride.pojo.CompareRideResponsePOJO
import com.fairfareindia.utils.ProjectUtilities
import java.util.*

class CompareRideAdapter(var context: Context, private val compareRideList: ArrayList<CompareRideResponsePOJO.VehiclesItem?>,
    var distance: String?,
    var baggs: String?,
    var estTime: String?
) : RecyclerView.Adapter<CompareRideAdapter.MyViewHolder>() {
    var spinnr: List<String?>? = null

    var mItemClickListener: OnItemClickListener? =
        null

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.templete_compareride, viewGroup, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        if(compareRideList.isEmpty()){

        }else{
            holder.tv_preson!!.text = compareRideList[position]!!.noOfSeater.toString()
            holder.distance!!.text = distance
            holder.tv_carName!!.text = compareRideList[position]!!.vehicleName
            holder.tv_vehialType!!.text = compareRideList[position]!!.label
            holder.tv_time!!.text = estTime
            holder.total!!.text = "₹ " +compareRideList[position]!!.total



            Glide.with(context)
                .load(compareRideList[position]!!.vehicleImageUrl)
                .apply(
                    RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .dontTransform()
                )
                .into(holder.ivVehical!!)
        }

    }

    override fun getItemCount(): Int {
        return compareRideList.size
    }

    fun SetOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        this.mItemClickListener = mItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    inner class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        @JvmField
        @BindView(R.id.tv_total)
        var total: TextView? = null

        @JvmField
        @BindView(R.id.place_area)
        var distance: TextView? = null

        @JvmField
        @BindView(R.id.tv_time)
        var tv_time: TextView? = null

        @JvmField
        @BindView(R.id.tv_preson)
        var tv_preson: TextView? = null

        @JvmField
        @BindView(R.id.tv_carName)
        var tv_carName: TextView? = null

        @JvmField
        @BindView(R.id.tv_vehialType)
        var tv_vehialType: TextView? = null

        @JvmField
        @BindView(R.id.iv_vehical)
        var ivVehical: ImageView? = null
        override fun onClick(v: View) {

            if(ProjectUtilities.checkInternetAvailable(context)){
            if (mItemClickListener != null) {
                mItemClickListener!!.onItemClick(
                    v,
                    position
                )
            }
            }else{
                ProjectUtilities.showToast(context,"No internet connection")
            }

        }

        init {
            ButterKnife.bind(this, itemView)
            itemView.setOnClickListener(this)
            }


    }

}