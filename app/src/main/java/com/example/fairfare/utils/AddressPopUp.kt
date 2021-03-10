package com.example.fairfare.utils

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.example.fairfare.R

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