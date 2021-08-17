package com.fairfareindia.ui.drawer.faq

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.fairfareindia.R
import com.fairfareindia.ui.drawer.faq.pojo.FAQResponsePOJO
import java.util.*

class FAQAdapter(
    var context: FragmentActivity?,
    private val complaintList: List<FAQResponsePOJO.FaqsItem>
) :
    RecyclerView.Adapter<FAQAdapter.MyViewHolder>() {

    var selectedPosition = -1
    private var faqSubQuestionArrayList: List<FAQResponsePOJO.ValuesItem> = ArrayList()


    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.row_faq, viewGroup, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return complaintList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        holder.tvQuestion!!.text = complaintList[position].key
        for (q in complaintList.indices) {
            if (complaintList[position].values!!.isNotEmpty()) {
                faqSubQuestionArrayList = complaintList[position]!!.values!!
            }
        }


        val mLayoutManager = LinearLayoutManager(context)
        mLayoutManager.stackFromEnd = true
        mLayoutManager.reverseLayout = true
        holder.recyclerView!!.setLayoutManager(mLayoutManager)

        if (faqSubQuestionArrayList.isNotEmpty()) {
            val adapter = FaqSubAdapter(context, faqSubQuestionArrayList)
            holder.recyclerView!!.setAdapter(adapter)
            adapter.notifyDataSetChanged()
        }

        holder.ivExpand!!.setOnClickListener(View.OnClickListener {
            if (selectedPosition == position) {
                selectedPosition = -1
            } else {
                selectedPosition = position
            }
            notifyDataSetChanged()
        })

        holder!!.tvQuestion!!.setOnClickListener(View.OnClickListener {
            if (selectedPosition == position) {
                selectedPosition = -1
            } else {
                selectedPosition = position
            }
            notifyDataSetChanged()
        })

        if (selectedPosition == -1) {
            holder.recyclerView!!.setVisibility(View.VISIBLE)
           // holder.ivExpand!!.setImageResource(R.drawable.downarrow)
        } else if (selectedPosition == position) {
            holder.recyclerView!!.setVisibility(View.VISIBLE)
            //holder.ivExpand!!.setImageResource(R.drawable.downarrow)
        } else {
            holder.recyclerView!!.setVisibility(View.GONE)
            //holder.ivExpand!!.setImageResource(R.drawable.rightarrow)
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

        @JvmField
        @BindView(R.id.ivExpand)
        var ivExpand: ImageView? = null

        @JvmField
        @BindView(R.id.recyclerView)
        var recyclerView: RecyclerView? = null


        init {
            ButterKnife.bind(this, itemView)

        }

        override fun onClick(v: View?) {

        }


    }


}