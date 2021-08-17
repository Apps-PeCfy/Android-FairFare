package com.fairfareindia.ui.drawer.myrides.ridedetails

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fairfareindia.R
import com.fairfareindia.networking.ApiClient
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.ridedetails.GridSpacingItemDecoration
import com.fairfareindia.ui.ridedetails.SelectedImageAdapter
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.google.gson.GsonBuilder
import com.iarcuschin.simpleratingbar.SimpleRatingBar
import kotlinx.android.synthetic.main.activity_my_ride_details.*
import kotlinx.android.synthetic.main.activity_ride_details.recycler_view_badge
import kotlinx.android.synthetic.main.activity_ride_details.recycler_view_driver
import kotlinx.android.synthetic.main.activity_ride_details.recycler_view_trip_meter
import kotlinx.android.synthetic.main.activity_ride_details.recycler_view_vehicle
import kotlinx.android.synthetic.main.activity_ride_details.txt_badge
import kotlinx.android.synthetic.main.activity_ride_details.txt_driver
import kotlinx.android.synthetic.main.activity_ride_details.txt_trip_meter
import kotlinx.android.synthetic.main.activity_ride_details.txt_vehicle_number
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.DecimalFormat
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
    @BindView(R.id.llRating)
    var llRating: LinearLayout? = null

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
    @BindView(R.id.tvNightChages)
    var tvNightChages: TextView? = null

    @JvmField
    @BindView(R.id.tvActualNightChages)
    var tvActualNightChages: TextView? = null

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

    @JvmField
    @BindView(R.id.ivUserIcon)
    var ivUserIcon: ImageView? = null

    @JvmField
    @BindView(R.id.rlRideDetails)
    var rlRideDetails: RelativeLayout? = null

    @JvmField
    @BindView(R.id.tvActualTollCharges)
    var tvActualTollCharges: TextView? = null

    @JvmField
    @BindView(R.id.tvEstTollCharges)
    var tvEstTollCharges: TextView? = null

 @JvmField
    @BindView(R.id.homeView)
    var homeView: ScrollView? = null


    @JvmField
    @BindView(R.id.tvActualRewardPoints)
    var tvActualRewardPoints: TextView? = null

    @JvmField
    @BindView(R.id.ivViewTollInfo)
    var ivViewTollInfo: ImageView? = null

    var eventInfoDialog: Dialog? = null
    var eventDialogBind: EventDialogBind1? = null
    var waittimePopUpAdapter: RidePopUpAdapter? = null
    private var waitingList: List<RideDetailsResponsePOJO.WaitingsItem1> = ArrayList()
    var tollPopUpAdapter: TollPopUpAdapter? = null
    private var TOllList: List<RideDetailsResponsePOJO.TollsItem> = ArrayList()

    var vehicleImageAdapter: SelectedImageAdapter? = null
    var meterImageAdapter: SelectedImageAdapter? = null
    var driverImageAdapter: SelectedImageAdapter? = null
    var badgeImageAdapter: SelectedImageAdapter? = null

    var vehicleImageList: ArrayList<String>? = ArrayList()
    var meterImageList: ArrayList<String>? = ArrayList()
    var driverImageList: ArrayList<String>? = ArrayList()
    var badgeImageList: ArrayList<String>? = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_ride_details)
        ButterKnife.bind(this)
        setStatusBarGradiant(this)

        PreferencesManager.initializeInstance(this@MyRideDetailsActivity)
        preferencesManager = PreferencesManager.instance
        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)
        ID = intent.getStringExtra("Id")

        mToolbar!!.title = "Ride Details"
        mToolbar!!.setTitleTextColor(Color.WHITE)
        setSupportActionBar(mToolbar)
        mToolbar!!.setNavigationOnClickListener { onBackPressed() }

        setSelectedImageList()
        getRideDetails()

    }


    @OnClick(R.id.ivViewInfo)
    fun iiewInfo() {

        if(waitingList.size>0) {
            eventInfoDialog = Dialog(this@MyRideDetailsActivity, R.style.dialog_style)

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


    }

    @OnClick(R.id.ivViewTollInfo)
    fun ivViewTollInfo() {

        if(TOllList.size>0) {
            eventInfoDialog = Dialog(this@MyRideDetailsActivity, R.style.dialog_style)

            eventInfoDialog!!.setCancelable(true)
            val inflater1 =
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view12: View = inflater1.inflate(R.layout.toll_layout, null)
            eventInfoDialog!!.setContentView(view12)
            eventDialogBind = EventDialogBind1()
            ButterKnife.bind(eventDialogBind!!, view12)


            eventDialogBind!!.rvEventInfo!!.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            tollPopUpAdapter = TollPopUpAdapter( TOllList)
            eventDialogBind!!.rvEventInfo!!.adapter = tollPopUpAdapter

            eventInfoDialog!!.show()
        }


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

    private fun setSelectedImageList() {

        vehicleImageAdapter = SelectedImageAdapter(this, vehicleImageList!!, object : SelectedImageAdapter.SelectedImageAdapterInterface{
            override fun itemClick(position: Int, imageName: String?) {

            }

            override fun onRemoveClick(position: Int, imageName: String?) {

            }

        } )

        val spanCount = 2
        recycler_view_vehicle!!.layoutManager = GridLayoutManager(this, spanCount)
        val spacing = 15
        val includeEdge = true
        recycler_view_vehicle!!.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount,
                spacing,
                includeEdge
            )
        )
        recycler_view_vehicle!!.adapter = vehicleImageAdapter

        // Meter Image Adapter

        meterImageAdapter = SelectedImageAdapter(this, meterImageList!!, object : SelectedImageAdapter.SelectedImageAdapterInterface{
            override fun itemClick(position: Int, imageName: String?) {

            }

            override fun onRemoveClick(position: Int, imageName: String?) {

            }

        } )

        recycler_view_trip_meter!!.layoutManager = GridLayoutManager(this, spanCount)
        recycler_view_trip_meter!!.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount,
                spacing,
                includeEdge
            )
        )
        recycler_view_trip_meter!!.adapter = meterImageAdapter

        // Driver Image Adapter

        driverImageAdapter = SelectedImageAdapter(this, driverImageList!!, object : SelectedImageAdapter.SelectedImageAdapterInterface{
            override fun itemClick(position: Int, imageName: String?) {

            }

            override fun onRemoveClick(position: Int, imageName: String?) {

            }

        } )

        recycler_view_driver!!.layoutManager = GridLayoutManager(this, spanCount)
        recycler_view_driver!!.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount,
                spacing,
                includeEdge
            )
        )
        recycler_view_driver!!.adapter = driverImageAdapter

        // Badge Image Adapter

        badgeImageAdapter = SelectedImageAdapter(this, badgeImageList!!, object : SelectedImageAdapter.SelectedImageAdapterInterface{
            override fun itemClick(position: Int, imageName: String?) {

            }

            override fun onRemoveClick(position: Int, imageName: String?) {

            }

        } )

        recycler_view_badge!!.layoutManager = GridLayoutManager(this, spanCount)
        recycler_view_badge!!.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount,
                spacing,
                includeEdge
            )
        )
        recycler_view_badge!!.adapter = badgeImageAdapter

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
                    homeView!!.visibility = View.VISIBLE
                    vehicleImageList = response.body()!!.data!!.vehicleNoImages as ArrayList<String>?
                    badgeImageList = response.body()!!.data!!.badgeNoImages as ArrayList<String>?
                    driverImageList = response.body()!!.data!!.driverImages as ArrayList<String>?
                    meterImageList = response.body()!!.data!!.startMeterImages as ArrayList<String>?
                    setImageData();

                    waitingList = response.body()!!.data!!.actualTrackRide!!.waitings!!
                    TOllList = response.body()!!.data!!.actualTrackRide!!.tolls!!

                    if(waitingList.size==0) {
                        ivViewInfo!!.visibility=View.GONE
                    }


                    if(TOllList.size==0){
                        ivViewTollInfo!!.visibility=View.GONE
                    }



                    if ((response.body()!!.data!!.reviews)!!.isNotEmpty()) {
                        val strReview = response.body()!!.data!!.reviews!!.get(0)!!.reviews
                        if (strReview==null) {
                            editReview!!.visibility = View.GONE
                        } else {
                            editReview!!.visibility = View.VISIBLE
                            editReview!!.text = response.body()!!.data!!.reviews!!.get(0)!!.reviews

                        }

                        if ((response.body()!!.data!!.reviews!!.get(0)!!.stars) > 0) {
                            llRating!!.visibility = View.VISIBLE
                            ratingBar!!.setRating((response.body()!!.data!!.reviews!!.get(0)!!.stars).toFloat())

                        } else {
                            llRating!!.visibility = View.GONE

                        }

                    }


                    if(response.body()!!.data!!.rewards != null){
                        tvActualRewardPoints?.visibility = View.VISIBLE
                        tvActualRewardPoints?.text = "Reward points earned for this ride " +response.body()!!.data!!!!.rewards
                    }else{
                        tvActualRewardPoints?.visibility = View.GONE
                    }

                    tv_vahicalNO!!.text = response.body()!!.data!!.vehicleNo
                    tv_driverName!!.text = response.body()!!.data!!.driverName

                    if(response.body()!!.data!!.driverName!!.isEmpty()){
                        ivUserIcon?.visibility = View.GONE
                    }





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
                        tv_bagCount!!.text = "No Luggage"
                    } else {

                        if (response.body()!!.data!!.luggageQuantity.equals("1")) {
                            tv_bagCount!!.text =
                                response.body()!!.data!!.luggageQuantity + " Luggage"
                        } else {
                            tv_bagCount!!.text =
                                response.body()!!.data!!.luggageQuantity + " Luggage"

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

                        val singleDecimalKM = response!!.body()!!.data!!.actualTrackRide!!.distance

                        tv_actualDistance!!.text =
                            DecimalFormat("####.#").format((singleDecimalKM!!.toDouble()))+ " KM"

                        if(response!!.body()!!.data!!.actualTrackRide!!.duration.equals("1")){
                            tv_actualTime!!.text = response!!.body()!!.data!!.actualTrackRide!!.duration+" min"

                        }else{

                            tv_actualTime!!.text = response!!.body()!!.data!!.actualTrackRide!!.duration+" mins"

                        }
                        tv_actualFare!!.text =
                            "₹ " + response!!.body()!!.data!!.actualTrackRide!!.subTotalCharges
                        tv_actualTotalFare!!.text =
                            "₹ " + response!!.body()!!.data!!.actualTrackRide!!.totalCharges

                        tv_actualLuggage!!.text =
                            "₹ " + response!!.body()!!.data!!.luggageCharges


                        tvActualWaitTime!!.text =
                            response!!.body()!!.data!!.actualTrackRide!!.waitingTime
                        tvActualWaitCharge!!.text =
                            "₹ "+ response!!.body()!!.data!!.actualTrackRide!!.waitingCharges

                        tvActualSurCharge!!.text =
                            "₹ "+response!!.body()!!.data!!.actualTrackRide!!.surCharge


                        tvActualNightChages!!.text = "₹ "+response.body()!!.data!!.nightCharges



                        if(response.body()!!.data!!.actualTrackRide!!.tollCharges!!.equals("-")){

                        }else{
                            tvActualTollCharges!!.text = "₹ "+response.body()!!.data!!.actualTrackRide!!.tollCharges

                        }



                    }



                    if ((response!!.body()!!.data!!.estimatedTrackRide) != null) {

                        if(response.body()!!.data!!.estimatedTrackRide!!.tollCharges!!.equals("-")){

                        }else{
                            tvEstTollCharges!!.text = "₹ "+response.body()!!.data!!.estimatedTrackRide!!.tollCharges

                        }


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
                            "₹ "+ response!!.body()!!.data!!.estimatedTrackRide!!.waitingCharges
                        tvEstWaitTime!!.text =
                            response!!.body()!!.data!!.estimatedTrackRide!!.waitingTime


                        tvEstSurCharge!!.text =
                            "₹ "+ response!!.body()!!.data!!.estimatedTrackRide!!.surCharge


                        tvNightChages!!.text = "₹ "+response.body()!!.data!!.nightCharges

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

    private fun setImageData() {
        if (vehicleImageList != null && vehicleImageList?.size!! >0){
            txt_vehicle_number.visibility = View.VISIBLE
            recycler_view_vehicle.visibility = View.VISIBLE
        }else{
            txt_vehicle_number.visibility = View.GONE
            recycler_view_vehicle.visibility = View.GONE
        }

        vehicleImageAdapter?.updateAdapter(vehicleImageList!!)

        if (meterImageList != null && meterImageList?.size!! >0){
            txt_trip_meter.visibility = View.VISIBLE
            recycler_view_trip_meter.visibility = View.VISIBLE
        }else{
            txt_trip_meter.visibility = View.GONE
            recycler_view_trip_meter.visibility = View.GONE
        }

        meterImageAdapter?.updateAdapter(meterImageList!!)

        if (driverImageList != null && driverImageList?.size!! >0){
            txt_driver.visibility = View.VISIBLE
            recycler_view_driver.visibility = View.VISIBLE
        }else{
            txt_driver.visibility = View.GONE
            recycler_view_driver.visibility = View.GONE
        }

        driverImageAdapter?.updateAdapter(driverImageList!!)

        if (badgeImageList != null && badgeImageList?.size!! >0){
            txt_badge.visibility = View.VISIBLE
            recycler_view_badge.visibility = View.VISIBLE
        }else{
            txt_badge.visibility = View.GONE
            recycler_view_badge.visibility = View.GONE
        }

        badgeImageAdapter?.updateAdapter(badgeImageList!!)

        if (meterImageList?.size!! >0 || vehicleImageList?.size!! >0 || driverImageList?.size!! >0 || badgeImageList?.size!! >0){
            txt_photos.visibility = View.VISIBLE
        }else{
            txt_photos.visibility = View.GONE
        }

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