package com.fairfareindia.ui.drawer.mylocation

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.fairfareindia.R
import com.fairfareindia.ui.home.pojo.GetSaveLocationResponsePOJO


class MyLocationAdapter(
    var context: FragmentActivity?,
    private val savedLocationList: List<GetSaveLocationResponsePOJO.DataItem>
) : RecyclerView.Adapter<MyLocationAdapter.MyViewHolder>() {

    private var iclickListener: IClickListener? = null
    var fClick = "1"
    var editClick = "1"
    lateinit var my: MyViewHolder
    var currentSelectedPosition = 0

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): MyLocationAdapter.MyViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.templete_my_location, viewGroup, false)
        return MyViewHolder(v)

    }

    override fun getItemCount(): Int {
        return savedLocationList.size
    }


    @SuppressLint("NewApi")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tv_place_area!!.setVisibility(View.VISIBLE)

        my = holder
        holder.place_address!!.text = savedLocationList[position].fullAddress
        holder.tv_place_area!!.text = savedLocationList[position].category
        holder.place_area!!.setText(savedLocationList[position].category)



        holder.ivEdit!!.setOnClickListener {
            currentSelectedPosition = position

            if (editClick == "1") {
                editClick = "2"

                for (q in savedLocationList.indices) {
                    if (q == position) {

                        Log.d("Logedit",q.toString())
                        holder.tv_place_area!!.setVisibility(View.GONE)
                        holder.ivEdit!!.setBackgroundResource(R.drawable.slice)

                        holder.place_area!!.setVisibility(View.VISIBLE)

                        holder.place_area!!.isEnabled=true
                        holder.place_area!!.isCursorVisible=true
                        holder.place_area!!.isFocusable = true

                    }
                }

            } else {
                editClick = "1"
                holder.place_area!!.visibility = View.GONE
                holder.place_area!!.isEnabled=false
                holder.place_area!!.isCursorVisible=false

                holder.tv_place_area!!.visibility = View.GONE
                iclickListener!!.editClick(savedLocationList[position].id, holder.place_area!!.text.toString())
                my.tv_place_area!!.text = holder.place_area!!.text.toString()
                holder.ivEdit!!.setBackgroundResource(R.drawable.slice_edit)


            }


        }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        @JvmField
        @BindView(R.id.place_address)
        var place_address: TextView? = null

        @JvmField
        @BindView(R.id.place_area)
        var place_area: EditText? = null

        @JvmField
        @BindView(R.id.iv_delete)
        var iv_delete: ImageView? = null

        @JvmField
        @BindView(R.id.ivEdit)
        var ivEdit: ImageView? = null

        @JvmField
        @BindView(R.id.tv_place_area)
        var tv_place_area: TextView? = null


        init {
            ButterKnife.bind(this, itemView)
            iv_delete!!.setOnClickListener(this)
            ivEdit!!.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (v!!.id == R.id.iv_delete) {
                if (fClick == "1") {
                    iclickListener!!.favClick(savedLocationList[adapterPosition].id)
                    fClick = "2"
                }

            }
        }


    }


    fun setClickListener(iclickListener: IClickListener?) {
        this.iclickListener = iclickListener
    }

    interface IClickListener {
        fun favClick(id: Int)
        fun editClick(id: Int, editedtxt: String?)
    }


}