package com.fairfareindia.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.fairfareindia.R
import com.fairfareindia.ui.home.RecyclerViewAdapter.IClickListener
import com.fairfareindia.ui.home.pojo.GetSaveLocationResponsePOJO

class RecentRecyclerViewAdapter(
    var context: Context,
    private val LocatoinList: List<GetSaveLocationResponsePOJO.DataItem>
) : RecyclerView.Adapter<RecentRecyclerViewAdapter.MyViewHolder>() {
    private var iclickListener: IClickListener? = null
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recent_place_recycler_item_layout, viewGroup, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        holder.place_address!!.text = LocatoinList[position].fullAddress
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
        @BindView(R.id.place_item)
        var place_item: RelativeLayout? = null


        override fun onClick(v: View) {
            if (v.id == R.id.place_item) {
                iclickListener!!.seveRecent(LocatoinList[adapterPosition].placeId,
                    LocatoinList[adapterPosition].fullAddress,"")
            }
        }

        init {
            ButterKnife.bind(this, itemView!!)
            place_item!!.setOnClickListener(this)
        }
    }

    fun setClickListener(iclickListener: IClickListener?) {
        this.iclickListener = iclickListener
    }

}