package com.fairfareindia.ui.intercity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fairfareindia.R
import com.fairfareindia.ui.home.pojo.GetAllowCityResponse

class CitySelectionAdapter() : RecyclerView.Adapter<CitySelectionAdapter.MyViewHolder>() {
    private var context: Context? = null
    var mList: List<GetAllowCityResponse.CitiesItem>? = null
    var mListener: SelectionAdapterInterface? = null

    interface SelectionAdapterInterface {
        fun onItemSelected(model: GetAllowCityResponse.CitiesItem?)
    }

    fun updateAdapter(mList: List<GetAllowCityResponse.CitiesItem>?) {
        this.mList = mList
        notifyDataSetChanged()
    }

    constructor(context: Context?, mList: List<GetAllowCityResponse.CitiesItem>?, mListener: SelectionAdapterInterface?) : this() {
        this.context = context
        this.mList = mList
        this.mListener = mListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_city_selection, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model: GetAllowCityResponse.CitiesItem = mList!![position]
        if (model.name != null) {
            holder.txtName.text = model.name
        }
        holder.itemView.setOnClickListener { mListener!!.onItemSelected(model) }
    }

    override fun getItemCount(): Int {
        return mList!!.size
    }

    class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val txtName: TextView = itemView.findViewById(R.id.txt_name)
    }
}