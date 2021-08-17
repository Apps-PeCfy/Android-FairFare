package com.fairfareindia.utils

import android.app.Dialog
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.fairfareindia.R

class AddressPopUp {
    var eventInfoDialog: Dialog? = null


    @JvmField
    @BindView(R.id.rvEventInfo)
    var rvEventInfo: RecyclerView? = null


    @JvmField
    @BindView(R.id.ivPopUpClose)
    var ivPopUpClose: ImageView? = null

    @JvmField
    @BindView(R.id.tvDestinationAddress)
    var tvDestinationAddress: TextView? = null




    @OnClick(R.id.ivPopUpClose)
    fun ivPopUpClose() {
        eventInfoDialog!!.dismiss()
    }
}