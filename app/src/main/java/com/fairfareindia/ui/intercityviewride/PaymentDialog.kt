package com.fairfareindia.ui.intercityviewride

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.fairfareindia.R
import com.fairfareindia.databinding.DialogPaymentBinding


class PaymentDialog(context: Context?) : Dialog(context!!) {
    lateinit var binding: DialogPaymentBinding
    private var message: String? = null
    private var title: String? = null
    private var btnName: String? = null
    private var listener: PaymentDialogInterface? = null

    open interface PaymentDialogInterface {
        fun onButtonClick()
    }

    constructor(context: Context?, btnName: String?,  message: String?, title: String?, listener: PaymentDialogInterface?) : this(context) {
        this.btnName = btnName
        this.title = title
        this.message = message
        this.listener = listener
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogPaymentBinding.inflate(layoutInflater)
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
        //Method calling

        val param = binding.crdMain.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(20,20,20,20)
        binding.crdMain.layoutParams = param

        setListener()
        setData()
    }

    private fun setListener() {
        binding.apply {
            btnOk.setOnClickListener {
                listener?.onButtonClick()
            }
        }
    }

    /**
     * FUNCTIONS
     */
    private fun setData() {
        binding.apply {
            if (btnName == context.resources.getString(R.string.btn_pay_now)){
                txtTitle.visibility = View.GONE
            }else{
                txtTitle.visibility = View.VISIBLE
            }
            txtTitle.text = title
            btnOk.text = btnName
            txtMessage.text = message
        }
    }



}