package com.fairfareindia.ui.endrides

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fairfareindia.R
import com.fairfareindia.base.BaseLocationClass
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.disputs.RegisterDisputActivity
import com.fairfareindia.ui.endrides.pojo.ResponseEnd
import com.fairfareindia.ui.mapDraw.DrawMap
import com.fairfareindia.ui.placeDirection.DirectionsJSONParser
import com.fairfareindia.ui.ridereview.RideReviewActivity
import com.fairfareindia.utils.AddressPopUp
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.fairfareindia.utils.ProjectUtilities
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class EndRidesActivity : BaseLocationClass(), OnMapReadyCallback, IEndRideView {

    private var iEndRidePresenter: IEndRidePresenter? = null

    var estAddressPopup: String? = ""
    var rideID: String? = null
    var sAdd: String? = null
    var EndRideCurrentLat: String? = null
    var EndRideCurrentLon: String? = null
    var EndRideCurrentAddress: String? = null


    var actualDistanceTravelled: String? = null
    var actualTimeTravelled: String? = null
    var actualDistanceTravelledForNightCharges: String? = null


    var dAdd: String? = null
    var startMeterReading: String? = null
    var originLat: String? = null
    var originLong: String? = null
    var destLat: String? = null
    var destLong: String? = null
    var mapDraw: DrawMap? = null
    var token: String? = null
    var preferencesManager: PreferencesManager? = null
    var mMap: GoogleMap? = null
    var sourecemarker: Marker? = null
    private var mPolyline: Polyline? = null
    var eventInfoDialog: Dialog? = null
    var eventDialogBind: EventDialogBind? = null
    var waittimePopUpAdapter: WaitTimePopUpAdapter? = null
    var tollsPopUPEndRIde: TollsPopUPEndRIde? = null
    var tollsJSONArrayFromTollGuru : JSONArray = JSONArray()


    @JvmField
    @BindView(R.id.tv_vahicalNO)
    var tv_vahicalNO: TextView? = null

    @JvmField
    @BindView(R.id.tv_driverName)
    var tv_driverName: TextView? = null

    @JvmField
    @BindView(R.id.tv_actualDistance)
    var tv_actualDistance: TextView? = null

    @JvmField
    @BindView(R.id.tv_estDistance)
    var tv_estDistance: TextView? = null

    @JvmField
    @BindView(R.id.tv_actualTime)
    var tv_actualTime: TextView? = null

    @JvmField
    @BindView(R.id.tv_estTime)
    var tv_estTime: TextView? = null

    @JvmField
    @BindView(R.id.tv_actualFare)
    var tv_actualFare: TextView? = null

    @JvmField
    @BindView(R.id.tv_estFare)
    var tv_estFare: TextView? = null

    @JvmField
    @BindView(R.id.tv_actualLuggage)
    var tv_actualLuggage: TextView? = null

    @JvmField
    @BindView(R.id.tv_estLuggage)
    var tv_estLuggage: TextView? = null

    @JvmField
    @BindView(R.id.tv_actualTotalFare)
    var tv_actualTotalFare: TextView? = null

    @JvmField
    @BindView(R.id.tv_estTotalFare)
    var tv_estTotalFare: TextView? = null

    @JvmField
    @BindView(R.id.tv_bagCount)
    var tv_bagCount: TextView? = null

    @JvmField
    @BindView(R.id.tv_Datetime)
    var tv_Datetime: TextView? = null

    @JvmField
    @BindView(R.id.tvActualSurCharge)
    var tvActualSurCharge: TextView? = null

    @JvmField
    @BindView(R.id.tvEstSurCharge)
    var tvEstSurCharge: TextView? = null

    @JvmField
    @BindView(R.id.tvActualRewardPoints)
    var tvActualRewardPoints: TextView? = null

    @JvmField
    @BindView(R.id.tv_myCurrentLocation)
    var tv_myCurrentLocation: TextView? = null

    @JvmField
    @BindView(R.id.btnRegisterDisput)
    var btnRegisterDisput: Button? = null

    @JvmField
    @BindView(R.id.btnSettleandCLose)
    var btnSettleandCLose: Button? = null

    @JvmField
    @BindView(R.id.tv_myDropUpLocation)
    var tv_myDropUpLocation: TextView? = null

    @JvmField
    @BindView(R.id.tv_carName)
    var tv_carName: TextView? = null

    @JvmField
    @BindView(R.id.tvActualWaitTime)
    var tvActualWaitTime: TextView? = null

    @JvmField
    @BindView(R.id.tvEstWaitTime)
    var tvEstWaitTime: TextView? = null

    @JvmField
    @BindView(R.id.tvActualTollCharges)
    var tvActualTollCharges: TextView? = null

    @JvmField
    @BindView(R.id.tvEstTollCharges)
    var tvEstTollCharges: TextView? = null

    @JvmField
    @BindView(R.id.iv_vehical)
    var iv_vehical: ImageView? = null


    @JvmField
    @BindView(R.id.tvActualWaitCharge)
    var tvActualWaitCharge: TextView? = null

    @JvmField
    @BindView(R.id.tvEstWaitCharge)
    var tvEstWaitCharge: TextView? = null

    @JvmField
    @BindView(R.id.tvhideShow)
    var tvhideShow: RelativeLayout? = null

    @JvmField
    @BindView(R.id.ivViewInfo)
    var ivViewInfo: ImageView? = null

    @JvmField
    @BindView(R.id.ivViewTollInfo)
    var ivViewTollInfo: ImageView? = null

    @JvmField
    @BindView(R.id.tvNightChages)
    var tvNightChages: TextView? = null

    @JvmField
    @BindView(R.id.tvActualNightChages)
    var tvActualNightChages: TextView? = null

    @JvmField
    @BindView(R.id.ivUserIcon)
    var ivUserIcon: ImageView? = null

    @JvmField
    @BindView(R.id.ivViewInfoAddress)
    var ivViewInfoAddress: ImageView? = null

    @JvmField
    @BindView(R.id.endRideView)
    var endRideView: ScrollView? = null

    @JvmField
    @BindView(R.id.llshowData)
    var llshowData: LinearLayout? = null

    @JvmField
    @BindView(R.id.locationCardView)
    var locationCardView: CardView? = null



    @JvmField
    @BindView(R.id.toolbar_endRide)
    var mToolbar: Toolbar? = null
    var sharedpreferences: SharedPreferences? = null

    var arrWaitTime: ArrayList<HashMap<String, String>> = ArrayList()
    var imgurl: String? = ""

    var hideshow: String? = "show"

    private var waitingList: List<ResponseEnd.WaitingsItem1> = ArrayList()
    private var tollList: List<ResponseEnd.TollsItem> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_rides)
        ButterKnife.bind(this)
        setStatusBarGradiant(this)



        PreferencesManager.initializeInstance(this@EndRidesActivity)
        preferencesManager = PreferencesManager.instance
        sharedpreferences = getSharedPreferences("mypref", Context.MODE_PRIVATE)

        arrWaitTime =
            intent.getSerializableExtra("ride_waitings") as ArrayList<HashMap<String, String>>

        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)
        rideID = intent.getStringExtra("RideID")
        sAdd = intent.getStringExtra("sAddress")
        dAdd = intent.getStringExtra("dAddress")
        originLat = intent.getStringExtra("originLat")
        originLong = intent.getStringExtra("originLong")
        destLat = intent.getStringExtra("destLat")
        destLong = intent.getStringExtra("destLong")
        EndRideCurrentAddress = intent.getStringExtra("EndRideCurrentAddress")
        EndRideCurrentLat = intent.getStringExtra("EndRideCurrentLat")
        EndRideCurrentLon = intent.getStringExtra("EndRideCurrentLon")

        actualTimeTravelled = intent.getStringExtra("actualTimeTravelled")
        actualDistanceTravelledForNightCharges =
            intent.getStringExtra("actualDistanceTravelledForNightCharges")

        actualTimeTravelled = actualTimeTravelled!!.toFloat().roundToInt().toString()
        actualDistanceTravelled = intent.getStringExtra("actualDistanceTravelled")

        tollsJSONArrayFromTollGuru =  JSONArray(intent.getStringExtra("tollGuruJsonArray"))

       // Toast.makeText(this, tollsJSONArrayFromTollGuru.toString(), Toast.LENGTH_LONG).show()

        actualDistanceTravelled =
            DecimalFormat("####.#").format((actualDistanceTravelled!!.toDouble()))


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        tv_myCurrentLocation!!.text = sAdd
        tv_myDropUpLocation!!.text = EndRideCurrentAddress


        iEndRidePresenter = EndRideImplementer(this)
        iEndRidePresenter!!.endRide(
            "$token",
            rideID,
            actualDistanceTravelled!!.toDouble(),
            actualTimeTravelled,
            arrWaitTime,
            EndRideCurrentLat,
            EndRideCurrentLon,
            EndRideCurrentAddress,
            originLat,
            originLong,
            sAdd,
            actualDistanceTravelledForNightCharges,
            tollsJSONArrayFromTollGuru
        )

        mToolbar!!.title = "End Ride"
        mToolbar!!.setTitleTextColor(Color.WHITE)
        setSupportActionBar(mToolbar)
        mToolbar!!.setNavigationOnClickListener { onBackPressed() }


    }


    private fun setStatusBarGradiant(activity: EndRidesActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            val background = activity.resources.getDrawable(R.drawable.app_gradient)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(android.R.color.transparent)
            window.setBackgroundDrawable(background)
        }
    }


    @OnClick(R.id.ivViewInfoAddress)
    fun setAddress() {
        var eventDialogBindAddressPopup = AddressPopUp()

        eventDialogBindAddressPopup?.eventInfoDialog =
            Dialog(this@EndRidesActivity, R.style.dialog_style)
        eventDialogBindAddressPopup?.eventInfoDialog!!.setCancelable(true)
        val inflater1 =
            this@EndRidesActivity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view12: View = inflater1.inflate(R.layout.destination_address_popup, null)
        eventDialogBindAddressPopup?.eventInfoDialog!!.setContentView(view12)
        ButterKnife.bind(eventDialogBindAddressPopup!!, view12)

        eventDialogBindAddressPopup!!.tvDestinationAddress!!.text = estAddressPopup
        eventDialogBindAddressPopup!!.eventInfoDialog!!.show()


    }

    @OnClick(R.id.ivViewInfo)
    fun iiewInfo() {
        if (waitingList.size > 0) {
            eventInfoDialog = Dialog(this@EndRidesActivity, R.style.dialog_style)
            eventInfoDialog!!.setCancelable(true)
            val inflater1 =
                this@EndRidesActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view12: View = inflater1.inflate(R.layout.event_info, null)
            eventInfoDialog!!.setContentView(view12)
            eventDialogBind = EventDialogBind()
            ButterKnife.bind(eventDialogBind!!, view12)

            eventDialogBind!!.rvEventInfo!!.layoutManager =
                LinearLayoutManager(this@EndRidesActivity, LinearLayoutManager.VERTICAL, false)
            eventDialogBind!!.rvEventInfo!!.layoutManager = LinearLayoutManager(
                this@EndRidesActivity,
                LinearLayoutManager.VERTICAL,
                false
            ) // set LayoutManager to RecyclerView
            waittimePopUpAdapter = WaitTimePopUpAdapter(this, waitingList)
            eventDialogBind!!.rvEventInfo!!.adapter = waittimePopUpAdapter


            eventInfoDialog!!.show()
        }


    }


    @OnClick(R.id.tvhideShow)
    fun hideshow() {
        if (hideshow.equals("show")) {
            hideshow = "hide"
            locationCardView!!.visibility = View.GONE
            llshowData!!.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            llshowData!!.requestLayout()

            endRideView!!.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            endRideView!!.requestLayout()

            //  slideUp(rlhideview)

        } else {
            hideshow = "show"
            locationCardView!!.visibility = View.VISIBLE
            llshowData!!.layoutParams.height = 1000
            llshowData!!.requestLayout()


        }
    }



    @OnClick(R.id.ivViewTollInfo)
    fun ivViewTollInfo() {
        if (tollList.size > 0) {
            eventInfoDialog = Dialog(this@EndRidesActivity, R.style.dialog_style)
            eventInfoDialog!!.setCancelable(true)
            val inflater1 =
                this@EndRidesActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view12: View = inflater1.inflate(R.layout.event_info, null)
            eventInfoDialog!!.setContentView(view12)
            eventDialogBind = EventDialogBind()
            ButterKnife.bind(eventDialogBind!!, view12)

            eventDialogBind!!.rvEventInfo!!.layoutManager =
                LinearLayoutManager(this@EndRidesActivity, LinearLayoutManager.VERTICAL, false)
            eventDialogBind!!.rvEventInfo!!.layoutManager = LinearLayoutManager(
                this@EndRidesActivity,
                LinearLayoutManager.VERTICAL,
                false
            ) // set LayoutManager to RecyclerView
            tollsPopUPEndRIde = TollsPopUPEndRIde(tollList)
            eventDialogBind!!.rvEventInfo!!.adapter = tollsPopUPEndRIde


            eventInfoDialog!!.show()
        }


    }


    inner class EventDialogBind {
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


    @OnClick(R.id.btnRegisterDisput)
    fun registerDisputes() {

        if(ProjectUtilities.checkInternetAvailable(this@EndRidesActivity)) {

            val intent = Intent(this@EndRidesActivity, RegisterDisputActivity::class.java)
        intent.putExtra("vahicalNo", tv_vahicalNO!!.text.toString())
        intent.putExtra("driverName", tv_driverName!!.text.toString())
        intent.putExtra("bagCount", tv_bagCount!!.text.toString())
        intent.putExtra("Datetime", tv_Datetime!!.text.toString())
        intent.putExtra("vahicalName", tv_carName!!.text.toString())
        intent.putExtra("vahicalImg", imgurl)
        intent.putExtra("RIDEID", rideID)
        intent.putExtra("MeterReading", startMeterReading)
        startActivity(intent)
        }else{
            ProjectUtilities.showToast(this@EndRidesActivity,getString(R.string.internet_error))
        }

    }

    @OnClick(R.id.btnSettleandCLose)
    fun settleandCLose() {

        if(ProjectUtilities.checkInternetAvailable(this@EndRidesActivity)) {
            val intent = Intent(this@EndRidesActivity, RideReviewActivity::class.java)
            intent.putExtra("RIDEID", rideID)
            startActivity(intent)
            finish()
        }else{
            ProjectUtilities.showToast(this@EndRidesActivity,getString(R.string.internet_error))
        }
    }


    override fun endRideSuccess(endRideResponsePOJO: ResponseEnd?) {

        endRideView!!.visibility = View.VISIBLE
        preferencesManager!!.setStringValue(Constants.SHARED_PREFERENCE_USER_REWARD, endRideResponsePOJO!!.rewardsupdate)


        estAddressPopup = endRideResponsePOJO?.ride?.estimatedTrackRide!!.destinationFullAddress

        waitingList = endRideResponsePOJO!!.ride?.actualTrackRide?.waitings!!
        tollList = endRideResponsePOJO!!.ride?.actualTrackRide?.tolls!!

        if (waitingList.size == 0) {
            ivViewInfo!!.visibility = View.GONE
        }

        if (tollList.size == 0) {
            ivViewTollInfo!!.visibility = View.GONE
        }



        imgurl = endRideResponsePOJO!!.ride?.vehicleImageUrl

        startMeterReading =
            endRideResponsePOJO!!.ride?.vehicleDetail!!.startMeterReading.toString()

        tv_vahicalNO!!.text = endRideResponsePOJO!!.ride?.vehicleDetail?.vehicleNo
        tv_carName!!.text = endRideResponsePOJO!!.ride?.vehicleName
        tv_driverName!!.text = endRideResponsePOJO!!.ride?.vehicleDetail?.driverName


        if (endRideResponsePOJO.ride?.vehicleDetail?.driverName?.isEmpty()!!) {
            ivUserIcon!!.visibility = View.GONE
        }
        tvEstWaitTime!!.text = endRideResponsePOJO!!.ride?.estimatedTrackRide?.waitingTime
        tvActualWaitTime!!.text = endRideResponsePOJO!!.ride?.actualTrackRide?.waitingTime


        if (endRideResponsePOJO!!.ride?.actualTrackRide?.tollCharges?.equals("-")!!) {

        } else {
            tvActualTollCharges!!.text =
                "₹ " + endRideResponsePOJO!!.ride?.actualTrackRide?.tollCharges
        }


        tvActualWaitCharge!!.text =
            "₹ " + endRideResponsePOJO!!.ride?.actualTrackRide?.waitingCharges
        tvEstWaitCharge!!.text =
            "₹ " + endRideResponsePOJO!!.ride?.estimatedTrackRide?.waitingCharges


        tvActualSurCharge!!.text = "₹ " + endRideResponsePOJO!!.ride?.actualTrackRide?.surCharge
        tvEstSurCharge!!.text = "₹ " + endRideResponsePOJO!!.ride?.estimatedTrackRide?.surCharge



        if(endRideResponsePOJO.rewards != null){
            tvActualRewardPoints?.visibility = View.VISIBLE
            tvActualRewardPoints?.text = "Reward points earned for this ride " +endRideResponsePOJO!!.rewards
        }else{
            tvActualRewardPoints?.visibility = View.GONE
        }





        Glide.with(this@EndRidesActivity)
            .load(endRideResponsePOJO!!.ride?.vehicleImageUrl)
            .apply(
                RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .dontTransform()
            ).into(iv_vehical!!)

        if (endRideResponsePOJO!!.ride?.luggageQuantity.equals("0")) {
            tv_bagCount!!.text = "No Luggage"

        } else {
            tv_bagCount!!.text =
                endRideResponsePOJO!!.ride?.luggageQuantity.toString() + " Luggage"

        }
        tv_actualDistance!!.text = endRideResponsePOJO!!.ride?.actualTrackRide?.distance + " KM"
        tv_estDistance!!.text = endRideResponsePOJO!!.ride?.estimatedTrackRide?.distance + " KM"


        tv_estTime!!.text = endRideResponsePOJO!!.ride?.estimatedTrackRide?.duration

        if(endRideResponsePOJO!!.ride?.actualTrackRide?.duration.equals("1")){
            tv_actualTime!!.text = endRideResponsePOJO!!.ride?.actualTrackRide?.duration + " min"

        }else{
            tv_actualTime!!.text = endRideResponsePOJO!!.ride?.actualTrackRide?.duration + " mins"

        }



        tv_estFare!!.text = "₹ " + endRideResponsePOJO!!.ride?.estimatedTrackRide?.subTotalCharges
        tv_actualFare!!.text = "₹ " + endRideResponsePOJO!!.ride?.actualTrackRide?.subTotalCharges




        if (endRideResponsePOJO!!.ride!!.estimatedTrackRide!!.tollCharges.equals("-")) {

        } else {
            tvEstTollCharges!!.text =
                "₹ " + endRideResponsePOJO!!.ride?.estimatedTrackRide?.tollCharges
        }



        tvActualNightChages!!.text = "₹ " + endRideResponsePOJO!!.ride?.nightCharges
        tvNightChages!!.text = "₹ " + endRideResponsePOJO!!.ride?.nightCharges



        tv_estTotalFare!!.text =
            "₹ " + endRideResponsePOJO!!.ride?.estimatedTrackRide?.totalCharges
        tv_actualTotalFare!!.text =
            "₹ " + endRideResponsePOJO!!.ride?.actualTrackRide?.totalCharges


        tv_estLuggage!!.text = "₹ " + endRideResponsePOJO!!.ride?.luggageCharges
        tv_actualLuggage!!.text = "₹ " + endRideResponsePOJO!!.ride?.luggageCharges


        val formatviewRide = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

        val formatRide = SimpleDateFormat("dd MMM, hh:mm a")
        val formaredDate =
            formatRide.format(formatviewRide.parse((endRideResponsePOJO.ride?.endDate).toString()))

        val strformaredDate = formaredDate.replace("am", "AM").replace("pm", "PM")


        tv_Datetime!!.text = strformaredDate


        /*  Glide.with(this@EndRidesActivity)
              .load(endRideResponsePOJO!!.ride!!.vehicleDetail!!.)
              .apply(
                  RequestOptions()
                      .centerCrop()
                      .dontAnimate()
                      .dontTransform()
              ).into(iv_vehical!!)
  */
    }

    override fun validationError(validationResponse: ValidationResponse?) {

        Toast.makeText(
            this@EndRidesActivity,
            validationResponse!!.errors!![0].message,
            Toast.LENGTH_LONG
        ).show()

    }

    override fun showWait() {
        ProjectUtilities.showProgressDialog(this@EndRidesActivity)
    }

    override fun removeWait() {
        ProjectUtilities.dismissProgressDialog()
    }

    override fun onFailure(appErrorMessage: String?) {
        Toast.makeText(this@EndRidesActivity, appErrorMessage, Toast.LENGTH_LONG).show()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap

        mMap!!.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(originLat!!.toDouble(), originLong!!.toDouble()), 15.0f
            )
        )
        sourecemarker = mMap!!.addMarker(
            MarkerOptions().position(
                LatLng(
                    (originLat)!!.toDouble(),
                    (originLong)!!.toDouble()
                )
            ).icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker))
        )
        sourecemarker = mMap!!.addMarker(
            MarkerOptions().position(
                LatLng(
                    (destLat)!!.toDouble(),
                    (destLong)!!.toDouble()
                )
            ).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.custom_marker_grey)
            )
        )


        drawRoute()
    }

    private fun drawRoute() {
        var mOrigin: LatLng? = null
        var mDestination: LatLng? = null



        mOrigin = LatLng(
            (originLat)!!.toDouble(),
            (originLong)!!.toDouble()
        )
        mDestination = LatLng(
            (destLat)!!.toDouble(),
            (destLong)!!.toDouble()
        )


        val url = getDirectionsUrl(mOrigin!!, mDestination!!)
        val downloadTask = DownloadTask()
        downloadTask.execute(url)
    }

    private fun getDirectionsUrl(mOrigin: LatLng, mDestination: LatLng): String {
        val str_origin = "origin=" + mOrigin.latitude + "," + mOrigin.longitude
        val str_dest = "destination=" + mDestination.latitude + "," + mDestination.longitude

        // Key
        val key = "key=" + getString(R.string.google_maps_key)

        // Building the parameters to the web service
        val parameters = "$str_origin&$str_dest&$key"

        // Output format
        val output = "json"

        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters"
    }

    private inner class DownloadTask :
        AsyncTask<String, Void?, String>() {
        // Downloading data in non-ui thread
        protected override fun doInBackground(vararg url: String): String {

            // For storing data from web service
            var data = ""
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0])
                Log.d("DownloadTask", "DownloadTask : $data")
            } catch (e: Exception) {
                Log.d("Background Task", e.toString())
            }
            return data
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            val parserTask =
                ParserTask()

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result)
        }
    }

    @Throws(IOException::class)
    private fun downloadUrl(strUrl: String): String {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(strUrl)

            // Creating an http connection to communicate with url
            urlConnection = url.openConnection() as HttpURLConnection

            // Connecting to url
            urlConnection.connect()

            // Reading data from url
            iStream = urlConnection!!.inputStream
            val br =
                BufferedReader(InputStreamReader(iStream))
            val sb = StringBuffer()
            var line: String? = ""
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            data = sb.toString()
            br.close()
        } catch (e: Exception) {
            Log.d("Exception on download", e.toString())
        } finally {
            iStream!!.close()
            urlConnection!!.disconnect()
        }
        return data
    }

    private inner class ParserTask :
        AsyncTask<String, Int?, List<List<HashMap<String, String>>>?>() {
        // Parsing the data in non-ui thread
        protected override fun doInBackground(vararg jsonData: String): List<List<HashMap<String, String>>>? {
            val jObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? = null
            try {
                jObject = JSONObject(jsonData[0])
                val parser =
                    DirectionsJSONParser()
                val array = jObject.getJSONArray("routes")
                val geoCodedarray = jObject.getJSONArray("geocoded_waypoints")
                val routes1 = array.getJSONObject(0)
                val legs = routes1.getJSONArray("legs")
                val steps = legs.getJSONObject(0)
                val distance = steps.getJSONObject("distance")
                val duration = steps.getJSONObject("duration")

                Log.d(
                    "DistanceTrack", distance.getString("text") + "   " + duration.getString("text")
                )

                routes = parser.parse(jObject)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return routes
        }

        // Executes in UI thread, after the parsing process
        override fun onPostExecute(result: List<List<HashMap<String, String>>>?) {
            var points: ArrayList<LatLng?>? = null
            var lineOptions: PolylineOptions? = null

            // Traversing through all the routes
            if (result != null) {
                for (i in result!!.indices) {
                    points = ArrayList()
                    lineOptions = PolylineOptions()

                    // Fetching i-th route
                    val path =
                        result[i]

                    // Fetching all the points in i-th route
                    for (j in path.indices) {
                        val point = path[j]
                        val lat = point["lat"]!!.toDouble()
                        val lng = point["lng"]!!.toDouble()
                        val position =
                            LatLng(lat, lng)
                        points.add(position)
                    }
                    lineOptions.addAll(points)
                    lineOptions.width(8f)
                    //  lineOptions.color(Color.GREEN);
                    lineOptions.color(
                        this@EndRidesActivity.resources.getColor(R.color.gradientstartcolor)
                    )
                }
            }


            if (lineOptions != null) {
                if (mPolyline != null) {
                    mPolyline!!.remove()
                }
                mPolyline = mMap!!.addPolyline(lineOptions)
            } else {
                Toast.makeText(applicationContext, "No route is found", Toast.LENGTH_LONG)
                    .show()
            }


        }
    }


    override fun onBackPressed() {

    }


}