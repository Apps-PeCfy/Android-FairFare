package com.fairfareindia.ui.drawer.intercityrides.ridedetails

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.fairfareindia.databinding.DialogWaitingInfoBinding
import com.fairfareindia.ui.intercitytrackpickup.RideDetailModel

class TollInfoDialog (context: Context?) : Dialog(context!!) {
    lateinit var binding: DialogWaitingInfoBinding
    private var list : ArrayList<RideDetailModel.Tolls> = ArrayList()
    private var mListener: WaitingInfoDialogInterface? = null
    private var mAdapter: TollAdapter? = null

    interface WaitingInfoDialogInterface {
        fun onItemClick(waitModel :RideDetailModel.WaitTimeModel)
    }

    constructor(context: Context?, list : ArrayList<RideDetailModel.Tolls>) : this(context) {
        this.list = list
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogWaitingInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setGravity(Gravity.CENTER)
        init()
    }

    private fun init() {

        val param = binding.rlMain.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(10,10,10,10)
        binding.rlMain.layoutParams = param
        //Method calling
        setListener()
        setRecyclerView()
    }

    /**
     * FUNCTIONS
     */
    private fun setRecyclerView() {
        mAdapter = TollAdapter(context, list, object : TollAdapter.TollAdapterInterface{
            override fun onItemSelected(model: RideDetailModel.Tolls?) {

            }

        })
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = mAdapter
    }

    private fun setListener() {
        binding.apply {
            imgClose.setOnClickListener(View.OnClickListener {
                dismiss()
            })

        }
    }
}