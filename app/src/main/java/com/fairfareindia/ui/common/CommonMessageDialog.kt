package com.fairfareindia.ui.common

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.fairfareindia.databinding.DialogCommonMessageBinding

class CommonMessageDialog(context: Context?) : Dialog(context!!) {
    lateinit var binding: DialogCommonMessageBinding
    private var message: String? = null
    private  var positiveButtonTitle:String? = null
    private  var negativeButtonTitle:String? = null
    private var mListener: CommonMessageDialogInterface? = null

    interface CommonMessageDialogInterface {
        fun onPositiveButtonClick()
        fun onNegativeButtonClick()
    }

    constructor(context: Context?, message: String?, positiveButtonTitle: String, negativeButtonTitle: String, mListener: CommonMessageDialogInterface) : this(context) {
        this.message = message
        this.positiveButtonTitle = positiveButtonTitle
        this.negativeButtonTitle = negativeButtonTitle
        this.mListener = mListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogCommonMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setGravity(Gravity.CENTER)
        init()
    }

    private fun init() {

        val param = binding.crdMain.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(20,20,20,20)
        binding.crdMain.layoutParams = param
        //Method calling
        setListener()
        setData()
    }

    /**
     * FUNCTIONS
     */
    private fun setData() {
        binding.apply {
           txtMessage.text = message
            btnNegative.text = negativeButtonTitle
            btnPositive.text = positiveButtonTitle
            if (positiveButtonTitle == null || positiveButtonTitle.equals("", ignoreCase = true)) {
                btnPositive.visibility = View.GONE
            } else {
                btnPositive.visibility = View.VISIBLE
            }
            if (negativeButtonTitle == null || negativeButtonTitle.equals("", ignoreCase = true)) {
                btnNegative.visibility = View.GONE
            } else {
                btnNegative.visibility = View.VISIBLE
            }
        }

    }

    private fun setListener() {
        binding.apply {
            btnNegative.setOnClickListener(View.OnClickListener {
                if (mListener != null) {
                    mListener!!.onNegativeButtonClick()
                }
            })

            btnPositive.setOnClickListener {
                if (mListener != null) {
                    mListener!!.onPositiveButtonClick()
                }
            }
        }
    }
}