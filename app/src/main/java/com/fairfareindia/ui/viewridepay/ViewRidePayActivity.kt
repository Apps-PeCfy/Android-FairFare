package com.fairfareindia.ui.viewridepay

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.fairfareindia.R

class ViewRidePayActivity : AppCompatActivity() {

    @JvmField
    @BindView(R.id.toolbar_home)
    var mToolbar: Toolbar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_ride_pay)
        ButterKnife.bind(this)
        setStatusBarGradiant(this)
        initView()
    }

    private fun initView() {
        mToolbar!!.title = "View Ride"
        mToolbar!!.setTitleTextColor(Color.WHITE)
        setSupportActionBar(mToolbar)
        mToolbar!!.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    private fun setStatusBarGradiant(activity: ViewRidePayActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            val background = activity.resources.getDrawable(R.drawable.app_gradient)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(android.R.color.transparent)
            window.setBackgroundDrawable(background)
        }
    }

    @OnClick(R.id.btnPayNow)
    fun btnBookRide() {
        showAlertDialog()
    }

    private fun showAlertDialog() {
        val alertDialog = AlertDialog.Builder(this@ViewRidePayActivity)
        val customLayout: android.view.View? =
            getLayoutInflater().inflate(R.layout.custom_alert_dialog, null)
        alertDialog.setView(customLayout)

        val btnSubmit: Button = customLayout!!.findViewById(R.id.btnOK)


        val alert = alertDialog.create()
        alert.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 40)
        alert.window!!.setBackgroundDrawable(inset)
        alert.setCanceledOnTouchOutside(false)
        alert.show()


        btnSubmit.setOnClickListener(View.OnClickListener {

            alert.dismiss()
            alert.cancel()




        })

    }


}
