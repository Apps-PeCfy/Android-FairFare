package com.fairfareindia.ui.viewridepay

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
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
    }




}
