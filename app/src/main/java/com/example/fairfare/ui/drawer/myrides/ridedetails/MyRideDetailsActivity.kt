package com.example.fairfare.ui.drawer.myrides.ridedetails

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.fairfare.R
import com.example.fairfare.networking.ApiClient
import com.example.fairfare.ui.Login.pojo.ValidationResponse
import com.example.fairfare.ui.drawer.mydisput.DisputWaitTimePopUpAdapter
import com.example.fairfare.ui.drawer.mydisput.disputDetail.DisputDetailActivity
import com.example.fairfare.ui.drawer.mydisput.disputDetail.pojo.DisputDetailResponsePOJO
import com.example.fairfare.utils.Constants
import com.example.fairfare.utils.PreferencesManager
import com.google.gson.GsonBuilder
import com.iarcuschin.simpleratingbar.SimpleRatingBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MyRideDetailsActivity : AppCompatActivity() {

    @JvmField
    @BindView(R.id.toolbar_endRide)
    var mToolbar: Toolbar? = null
    var preferencesManager: PreferencesManager? = null
    var ID: String? = null
    var token: String? = null

    @JvmField
    @BindView(R.id.tvEstSurCharge)
    var tvEstSurCharge: TextView? = null

    @JvmField
    @BindView(R.id.tvActualSurCharge)
    var tvActualSurCharge: TextView? = null

    @JvmField
    @BindView(R.id.tv_vahicalNO)
    var tv_vahicalNO: TextView? = null

    @JvmField
    @BindView(R.id.editReview)
    var editReview: TextView? = null

    @JvmField
    @BindView(R.id.tv_driverName)
    var tv_driverName: TextView? = null

    @JvmField
    @BindView(R.id.tv_bagCount)
    var tv_bagCount: TextView? = null

    @JvmField
    @BindView(R.id.tv_carName)
    var tv_carName: TextView? = null

    @JvmField
    @BindView(R.id.iv_vehical)
    var iv_vehical: ImageView? = null

    @JvmField
    @BindView(R.id.tv_myCurrentLocation)
    var tv_myCurrentLocation: TextView? = null

    @JvmField
    @BindView(R.id.tv_myDropUpLocation)
    var tv_myDropUpLocation: TextView? = null

    @JvmField
    @BindView(R.id.tv_actualDistance)
    var tv_actualDistance: TextView? = null

    @JvmField
    @BindView(R.id.tv_actualTime)
    var tv_actualTime: TextView? = null

    @JvmField
    @BindView(R.id.tv_actualFare)
    var tv_actualFare: TextView? = null

    @JvmField
    @BindView(R.id.tv_actualLuggage)
    var tv_actualLuggage: TextView? = null

    @JvmField
    @BindView(R.id.tv_actualTotalFare)
    var tv_actualTotalFare: TextView? = null

    @JvmField
    @BindView(R.id.tv_estDistance)
    var tv_estDistance: TextView? = null

    @JvmField
    @BindView(R.id.tv_estTime)
    var tv_estTime: TextView? = null

    @JvmField
    @BindView(R.id.tv_estFare)
    var tv_estFare: TextView? = null

    @JvmField
    @BindView(R.id.tvDisputReasn)
    var tvDisputReasn: TextView? = null

    @JvmField
    @BindView(R.id.tv_Datetime)
    var tv_Datetime: TextView? = null

    @JvmField
    @BindView(R.id.tv_estLuggage)
    var tv_estLuggage: TextView? = null

    @JvmField
    @BindView(R.id.tv_startMeterReading)
    var tv_startMeterReading: TextView? = null

    @JvmField
    @BindView(R.id.tv_endMeterReading)
    var tv_endMeterReading: TextView? = null


    @JvmField
    @BindView(R.id.tv_actualfare)
    var tv_actualfare: TextView? = null

    @JvmField
    @BindView(R.id.ratingBar)
    var ratingBar: SimpleRatingBar? = null

    @JvmField
    @BindView(R.id.tv_estTotalFare)
    var tv_estTotalFare: TextView? = null

    @JvmField
    @BindView(R.id.btnFileComplaint)
    var btnFileComplaint: TextView? = null


    @JvmField
    @BindView(R.id.tvActualWaitCharge)
    var tvActualWaitCharge: TextView? = null

    @JvmField
    @BindView(R.id.tvEstWaitCharge)
    var tvEstWaitCharge: TextView? = null

    @JvmField
    @BindView(R.id.tvActualWaitTime)
    var tvActualWaitTime: TextView? = null

    @JvmField
    @BindView(R.id.tvEstWaitTime)
    var tvEstWaitTime: TextView? = null

    @JvmField
    @BindView(R.id.ivViewInfo)
    var ivViewInfo: ImageView? = null

    var eventInfoDialog: Dialog? = null
    var eventDialogBind: EventDialogBind1? = null
    var waittimePopUpAdapter: RidePopUpAdapter? = null
    private var waitingList: List<RideDetailsResponsePOJO.WaitingsItem1> = ArrayList()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_ride_details)
        ButterKnife.bind(this)
        setStatusBarGradiant(this)
        eventInfoDialog = Dialog(this, R.style.alertDialog)

        PreferencesManager.initializeInstance(this@MyRideDetailsActivity)
        preferencesManager = PreferencesManager.instance
        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)
        ID = intent.getStringExtra("Id")

        mToolbar!!.title = "Ride Detail"
        mToolbar!!.setTitleTextColor(Color.WHITE)
        setSupportActionBar(mToolbar)
        mToolbar!!.setNavigationOnClickListener { onBackPressed() }

        getRideDetails()

    }


    @OnClick(R.id.ivViewInfo)
    fun iiewInfo() {

        eventInfoDialog!!.setCancelable(true)
        val inflater1 =
            this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view12: View = inflater1.inflate(R.layout.event_info, null)
        eventInfoDialog!!.setContentView(view12)
        eventDialogBind = EventDialogBind1()
        ButterKnife.bind(eventDialogBind!!, view12)

        eventDialogBind!!.rvEventInfo!!.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL, false
            )
        waittimePopUpAdapter = RidePopUpAdapter(this, waitingList)
        eventDialogBind!!.rvEventInfo!!.adapter = waittimePopUpAdapter

        eventInfoDialog!!.show()


    }


    inner class EventDialogBind1 {
        @JvmField
        @BindView(R.id.rvEventInfo)
        var rvEventInfo: RecyclerView? = null


        @JvmField
        @BindView(R.id.ivPopUpClose)
        var ivPopUpClose: ImageView? = null

        @OnClick(R.id.ivPopUpClose)
        fun ivPopUpClose() {
            eventInfoDialog!!.dismiss()
        }
    }

    private fun getRideDetails() {

        val progressDialog = ProgressDialog(this@MyRideDetailsActivity)
        progressDialog.setCancelable(false) // set cancelable to false
        progressDialog.setMessage("Please Wait") // set message
        progressDialog.show() // show progress dialog

        ApiClient.client.getDetailRide("Bearer $token", ID)!!.enqueue(object :
            Callback<RideDetailsResponsePOJO?> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<RideDetailsResponsePOJO?>,
                response: Response<RideDetailsResponsePOJO?>
            ) {
                progressDialog.dismiss()
                if (response.code() == 200) {

                    if((response.body()!!.data!!.reviews)!!.isNotEmpty()) {
                        editReview!!.text = response.body()!!.data!!.reviews!!.get(0)!!.reviews
                        ratingBar!!.setRating((response.body()!!.data!!.reviews!!.get(0)!!.stars).toFloat())
                    }

                    tv_vahicalNO!!.text = response.body()!!.data!!.vehicleNo
                    tv_driverName!!.text = response.body()!!.data!!.driverName


                    val formatviewRide = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

                    val formatRide = SimpleDateFormat("dd MMM, hh:mm a")
                    val formaredDate = formatRide.format(
                        formatviewRide.parse(
                            (response.body()!!.data!!.dateTime).toString()
                        )
                    )
                    val strformaredDate = formaredDate.replace("am", "AM").replace("pm", "PM")

                    tv_Datetime!!.text = strformaredDate

                    if (response.body()!!.data!!.luggageQuantity.equals("0")) {
                        tv_bagCount!!.text = "No Bags"
                    } else {

                        if (response.body()!!.data!!.luggageQuantity.equals("1")) {
                            tv_bagCount!!.text =
                                response.body()!!.data!!.luggageQuantity + " Bag"
                        } else {
                            tv_bagCount!!.text =
                                response.body()!!.data!!.luggageQuantity + " Bags"

                        }
                    }


                    tv_carName!!.text = response.body()!!.data!!.vehicleName
                    Glide.with(this@MyRideDetailsActivity)
                        .load(response.body()!!.data!!.vehicleImageUrl)
                        .apply(
                            RequestOptions()
                                .centerCrop()
                                .dontAnimate()
                                .dontTransform()
                        ).into(iv_vehical!!)



                    if ((response!!.body()!!.data!!.actualTrackRide) != null) {
                        tv_actualDistance!!.text =
                            response!!.body()!!.data!!.actualTrackRide!!.distance + " KM"
                        tv_actualTime!!.text =
                            response!!.body()!!.data!!.actualTrackRide!!.duration
                        tv_actualFare!!.text =
                            "₹ " + response!!.body()!!.data!!.actualTrackRide!!.subTotalCharges
                        tv_actualTotalFare!!.text =
                            "₹ " + response!!.body()!!.data!!.actualTrackRide!!.totalCharges

                        tv_actualLuggage!!.text =
                            "₹ " + response!!.body()!!.data!!.luggageCharges


                        tvActualWaitTime!!.text =
                            response!!.body()!!.data!!.actualTrackRide!!.waitingTime
                        tvActualWaitCharge!!.text =
                            response!!.body()!!.data!!.actualTrackRide!!.waitingCharges

                        tvActualSurCharge!!.text =
                            response!!.body()!!.data!!.actualTrackRide!!.surCharge

                    }



                    if ((response!!.body()!!.data!!.estimatedTrackRide) != null) {
                        tv_estDistance!!.text =
                            response!!.body()!!.data!!.estimatedTrackRide!!.distance + " KM"
                        tv_estTime!!.text =
                            response!!.body()!!.data!!.estimatedTrackRide!!.duration
                        tv_estFare!!.text =
                            "₹ " + response!!.body()!!.data!!.estimatedTrackRide!!.subTotalCharges
                        tv_estTotalFare!!.text =
                            "₹ " + response!!.body()!!.data!!.estimatedTrackRide!!.totalCharges

                        tv_estLuggage!!.text =
                            "₹ " + response!!.body()!!.data!!.luggageCharges

                        tvEstWaitCharge!!.text =
                            response!!.body()!!.data!!.estimatedTrackRide!!.waitingCharges
                        tvEstWaitTime!!.text =
                            response!!.body()!!.data!!.estimatedTrackRide!!.waitingTime


                        tvEstSurCharge!!.text =
                            response!!.body()!!.data!!.estimatedTrackRide!!.surCharge

                    }


                } else if (response.code() == 422) {
                    val gson = GsonBuilder().create()
                    var pojo: ValidationResponse? = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        Toast.makeText(
                            this@MyRideDetailsActivity,
                            pojo.message,
                            Toast.LENGTH_LONG
                        ).show()

                    } catch (exception: IOException) {
                    }

                } else {
                    Toast.makeText(
                        this@MyRideDetailsActivity,
                        "Internal server error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<RideDetailsResponsePOJO?>,
                t: Throwable
            ) {
                progressDialog.dismiss()
                Log.d("responseonse", t.stackTrace.toString())
            }
        })


    }

    private fun setStatusBarGradiant(activity: MyRideDetailsActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            val background = activity.resources.getDrawable(R.drawable.app_gradient)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(android.R.color.transparent)
            window.setBackgroundDrawable(background)
        }

    }
}
