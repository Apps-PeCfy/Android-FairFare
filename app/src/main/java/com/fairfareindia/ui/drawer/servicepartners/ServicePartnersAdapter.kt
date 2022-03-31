package com.fairfareindia.ui.drawer.servicepartners;

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fairfareindia.R
import com.fairfareindia.ui.drawer.ratecard.pojo.RateCardModel
import com.fairfareindia.utils.ProjectUtilities

class ServicePartnersAdapter() :
    RecyclerView.Adapter<ServicePartnersAdapter.MyViewHolder>() {
    private var context: Context? = null
    var mList: ArrayList<ServicePartnerModel.DataItem> = ArrayList()
    var mListener: ServicePartnersAdapterInterface? = null

    fun updateAdapter(mList: ArrayList<ServicePartnerModel.DataItem>) {
        this.mList = mList
        notifyDataSetChanged()
    }

    constructor(
        context: Context?,
        mList: ArrayList<ServicePartnerModel.DataItem>,
        mListener: ServicePartnersAdapterInterface?
    ) : this() {
        this.context = context
        this.mList = mList
        this.mListener = mListener
    }

    interface ServicePartnersAdapterInterface {
        fun onItemSelected(position: Int, model: ServicePartnerModel.DataItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_service_partner, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model: ServicePartnerModel.DataItem = mList[position]

        holder.txtName.text = model.name
        holder.txtServiceType.text = model.permitType
        holder.txtRouteServed.text = model.route_served
        holder.txtAddress.text = model.address
        holder.txtContactNo.text = model.phone_no
        holder.txtEmail.text = model.email




        holder.itemView.setOnClickListener{
           // mListener?.onItemSelected(position, model)
            if (holder.llDetail.visibility == View.GONE){
                holder.llDetail.visibility = View.VISIBLE
                holder.imgArrow.rotation = 180F
            }else{
                holder.llDetail.visibility = View.GONE
                holder.imgArrow.rotation = 0F
            }
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val txtName: TextView = itemView.findViewById(R.id.txt_name)
        val txtServiceType: TextView = itemView.findViewById(R.id.txt_service_type)
        val txtRouteServed: TextView = itemView.findViewById(R.id.txt_route_served)
        val txtAddress: TextView = itemView.findViewById(R.id.txt_address)
        val txtContactNo: TextView = itemView.findViewById(R.id.txt_contact_no)
        val txtEmail: TextView = itemView.findViewById(R.id.txt_email)
        val llDetail: LinearLayout = itemView.findViewById(R.id.ll_detail)
        val imgArrow: ImageView = itemView.findViewById(R.id.img_arrow)

    }
}
