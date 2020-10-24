package com.example.fairfare.ui.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.fairfare.R
import com.example.fairfare.ui.home.pojo.GetSaveLocationResponsePOJO.LocationsItem

class RecyclerViewAdapter(var context: Context, private val LocatoinList: List<LocationsItem>) : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {
    private var iclickListener: IClickListener? = null
    var fClick ="1"
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.place_recycler_item_layout, viewGroup, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        holder.place_address!!.text = LocatoinList[position].fullAddress
        holder.iv_fav!!.setBackgroundResource(R.drawable.ic_fav_checked)
    }

    override fun getItemCount(): Int {
        return LocatoinList.size
    }

    inner class MyViewHolder(itemView: View?) :
        RecyclerView.ViewHolder(itemView!!), View.OnClickListener {
        @JvmField
        @BindView(R.id.place_address)
        var place_address: TextView? = null

        @JvmField
        @BindView(R.id.place_area)
        var place_area: TextView? = null

        @JvmField
        @BindView(R.id.place_item)
        var place_item: RelativeLayout? = null

        @JvmField
        @BindView(R.id.iv_fav)
        var iv_fav: ImageView? = null
        override fun onClick(v: View) {
            if (v.id == R.id.iv_fav) {
                if(fClick=="1") {
                    iclickListener!!.favClick(LocatoinList[adapterPosition].id)
                    fClick = "2"
                }

            }
            if (v.id == R.id.place_item) {
                iclickListener!!.seveRecent(LocatoinList[adapterPosition].placeId,LocatoinList[adapterPosition].fullAddress)
            }
        }

        init {
            ButterKnife.bind(this, itemView!!)
            iv_fav!!.setOnClickListener(this)
            place_item!!.setOnClickListener(this)
        }
    }

    fun setClickListener(iclickListener: IClickListener?) {
        this.iclickListener = iclickListener
    }

    interface IClickListener {
        fun seveRecent(placeID: String?,fulladdress:String?)
        fun favClick(id: Int)
    }

}