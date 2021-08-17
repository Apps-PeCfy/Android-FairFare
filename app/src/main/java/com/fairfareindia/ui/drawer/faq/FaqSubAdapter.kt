package com.fairfareindia.ui.drawer.faq

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.fairfareindia.R
import com.fairfareindia.ui.drawer.faq.pojo.FAQResponsePOJO

class FaqSubAdapter (
    var context: FragmentActivity?,
    private val complaintList: List<FAQResponsePOJO.ValuesItem>
) :
    RecyclerView.Adapter<FaqSubAdapter.MyViewHolder>() {
     var selectedPosition = -1


    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.row_faq_sub, viewGroup, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return complaintList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvQuestion!!.text= complaintList[position].question
        holder.tvSubQuestion!!.text= complaintList[position].answer


        holder!!.tvQuestion!!.setOnClickListener(View.OnClickListener {
            if (selectedPosition == position) {
                selectedPosition = -1
            } else {
                selectedPosition = position
            }
            notifyDataSetChanged()
        })


        if (selectedPosition == position) {
            holder!!.tvSubQuestion!!.setVisibility(View.VISIBLE)
        } else {
            holder!!.tvSubQuestion!!.setVisibility(View.GONE)
        }

    }


    inner class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        @JvmField
        @BindView(R.id.tvQuestion)
        var tvQuestion: TextView? = null

        @JvmField
        @BindView(R.id.tvSubQuestion)
        var tvSubQuestion: TextView? = null



        init {
            ButterKnife.bind(this, itemView)

        }

        override fun onClick(v: View?) {

        }


    }


}