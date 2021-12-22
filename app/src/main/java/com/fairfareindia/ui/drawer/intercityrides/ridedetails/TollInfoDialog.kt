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
import com.fairfareindia.R
import com.fairfareindia.databinding.DialogWaitingInfoBinding
import com.fairfareindia.ui.intercitytrackpickup.RideDetailModel
import kotlin.math.roundToInt

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
        var height :Int = (context.resources.displayMetrics.heightPixels * 0.7).roundToInt()
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            height
        )
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setGravity(Gravity.CENTER)
        init()
    }

    private fun init() {

        val param = binding.rlMain.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(10,10,10,10)
        binding.rlMain.layoutParams = param

        binding.txtRightCornerLabel.text = context.resources.getString(R.string.str_charges)
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