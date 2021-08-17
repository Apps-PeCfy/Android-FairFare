package com.fairfareindia.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.fairfareindia.R
import com.fairfareindia.ui.home.pojo.GetSaveLocationResponsePOJO


class RecyclerViewAdapter(
    var context: Context,

    private val LocatoinList: List<GetSaveLocationResponsePOJO.DataItem>
) : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>()
{
    private var iclickListener: IClickListener? = null
    var fClick = "1"
    var selectedPosition = -1
    lateinit var myHolder: MyViewHolder

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.place_recycler_item_layout, viewGroup, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        myHolder = holder
        if(position == selectedPosition){
             holder.ivSave!!.visibility = View.VISIBLE
             holder.ivEdit!!.visibility = View.GONE

        }else{
             holder.ivSave!!.visibility = View.GONE
             holder.ivEdit!!.visibility = View.VISIBLE

        }
        holder.place_area!!.setText(LocatoinList[position].category)

        holder.place_address!!.text = LocatoinList[position].fullAddress
        holder.iv_fav!!.setBackgroundResource(R.drawable.ic_fav_checked)

        holder.ivSave!!.setOnClickListener {
            iclickListener!!.update(LocatoinList[position].id,holder.place_area!!.text.toString())

        }
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
        var place_area: EditText? = null

        @JvmField
        @BindView(R.id.place_item)
        var place_item: RelativeLayout? = null

        @JvmField
        @BindView(R.id.iv_fav)
        var iv_fav: ImageView? = null

        @JvmField
        @BindView(R.id.ivEdit)
        var ivEdit: RelativeLayout? = null

        @JvmField
        @BindView(R.id.ivSave)
        var ivSave: RelativeLayout? = null


        override fun onClick(v: View) {
            if (v.id == R.id.iv_fav) {
                if (fClick == "1") {
                    iclickListener!!.favClick(LocatoinList[adapterPosition].id)
                    fClick = "2"
                }

            }
            if (v.id == R.id.place_item) {
                  iclickListener!!.seveRecent(LocatoinList[adapterPosition].placeId,
                      LocatoinList[adapterPosition].fullAddress,
                  LocatoinList[adapterPosition].category)
            }

            if (v.id == R.id.ivEdit) {


                val inputMethodManager =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.toggleSoftInput(
                    InputMethodManager.SHOW_FORCED,
                    0
                )
                place_area!!.requestFocus()
                place_area!!.setSelection(place_area!!.text.toString().length)
                ivEdit!!.visibility = View.GONE
                ivSave!!.visibility = View.VISIBLE

                selectedPosition = adapterPosition
            }


           /* if(v.id == R.id.ivSave){
                  }*/
        }

        init {
            ButterKnife.bind(this, itemView!!)
            iv_fav!!.setOnClickListener(this)
            place_item!!.setOnClickListener(this)
            ivEdit!!.setOnClickListener(this)
            ivSave!!.setOnClickListener(this)
        }
    }

    fun setClickListener(iclickListener: IClickListener?) {
        this.iclickListener = iclickListener
    }

    interface IClickListener {
        fun seveRecent(placeID: String?, fulladdress: String?,addressType:String?)
        fun favClick(id: Int)
        fun update(id: Int,fulladdress: String?)
    }

}