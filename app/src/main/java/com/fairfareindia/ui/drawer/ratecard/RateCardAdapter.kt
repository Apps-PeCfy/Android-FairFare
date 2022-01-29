package com.fairfareindia.ui.drawer.ratecard;

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fairfareindia.R
import com.fairfareindia.ui.drawer.ratecard.pojo.RateCardModel
import com.fairfareindia.utils.ProjectUtilities

class RateCardAdapter() :
    RecyclerView.Adapter<RateCardAdapter.MyViewHolder>() {
    private var context: Context? = null
    var mList: List<RateCardModel.RateCardsDetailItem> = ArrayList()
    var mListener: RateCardAdapterInterface? = null

    fun updateAdapter(mList: List<RateCardModel.RateCardsDetailItem>) {
        this.mList = mList
        notifyDataSetChanged()
    }

    constructor(
        context: Context?,
        mList: List<RateCardModel.RateCardsDetailItem>,
        mListener: RateCardAdapterInterface?
    ) : this() {
        this.context = context
        this.mList = mList
        this.mListener = mListener
    }

    interface RateCardAdapterInterface {
        fun onItemSelected(position: Int, model: RateCardModel.RateCardsDetailItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_rate_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model: RateCardModel.RateCardsDetailItem = mList[position]

        holder.txtTitle.text = model.title
        holder.txtMessage.text = model.message



        holder.itemView.setOnClickListener{
            mListener?.onItemSelected(position, model)
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val txtTitle: TextView = itemView.findViewById(R.id.txt_title)
        val txtMessage: TextView = itemView.findViewById(R.id.txt_message)
    }
}
